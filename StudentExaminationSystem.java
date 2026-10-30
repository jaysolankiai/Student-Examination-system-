import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.*;

// ==========================================
// CUSTOM EXCEPTION (CO3: Exception Handling)
// ==========================================
class InvalidMarksException extends Exception {
    public InvalidMarksException(String message) {
        super(message);
    }
}

// ==========================================
// INTERFACE FOR POLYMORPHISM (CO2)
// ==========================================
interface Gradable {
    String calculateGrade(double percentage);
}

// ==========================================
// STUDENT MODEL CLASS (CO2, CO4)
// ==========================================
class Student implements Comparable<Student>, Gradable {
    private String studentId;
    private String name;
    private String department;
    private double[] marks;
    private double totalMarks;
    private double percentage;
    private String grade;
    private String status;

    public Student(String studentId, String name, String department, double[] marks) throws InvalidMarksException {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        setMarks(marks);
        computeResults();
    }

    // Encapsulation with validation
    public void setMarks(double[] marks) throws InvalidMarksException {
        for (double m : marks) {
            if (m < 0 || m > 100) {
                throw new InvalidMarksException("Marks must be between 0 and 100 for all subjects!");
            }
        }
        this.marks = marks;
    }

    public void computeResults() {
        totalMarks = 0;
        for (double m : marks) {
            totalMarks += m;
        }
        percentage = totalMarks / marks.length;
        grade = calculateGrade(percentage);
        status = percentage >= 40.0 ? "PASS" : "FAIL";
    }

    @Override
    public String calculateGrade(double pct) {
        if (pct >= 90) return "A+";
        else if (pct >= 80) return "A";
        else if (pct >= 70) return "B+";
        else if (pct >= 60) return "B";
        else if (pct >= 50) return "C";
        else if (pct >= 40) return "D";
        else return "F";
    }

    // Default sorting by Percentage (Descending) using Comparable
    @Override
    public int compareTo(Student other) {
        return Double.compare(other.percentage, this.percentage);
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getTotalMarks() { return totalMarks; }
    public double getPercentage() { return percentage; }
    public String getGrade() { return grade; }
    public String getStatus() { return status; }
}

// ==========================================
// MAIN GUI APPLICATION (CO2, CO3, CO4)
// ==========================================
public class StudentExaminationSystem extends JFrame {

    JTextField idField, nameField, mark1Field, mark2Field, mark3Field;
    JComboBox<String> deptComboBox;
    JTable table;
    DefaultTableModel tableModel;

    // Collections Framework (CO4)
    ArrayList<Student> studentList = new ArrayList<>();
    HashMap<String, Student> studentMap = new HashMap<>();

    public StudentExaminationSystem() {
        setTitle("Student Examination and Result Management System");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // --- Form Components ---
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setBounds(30, 25, 100, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(140, 25, 180, 25);
        add(idField);

        JLabel nameLabel = new JLabel("Student Name:");
        nameLabel.setBounds(30, 65, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 65, 180, 25);
        add(nameField);

        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setBounds(30, 105, 100, 25);
        add(deptLabel);

        String[] departments = {"Computer Science", "Information Tech", "Electronics", "Mechanical"};
        deptComboBox = new JComboBox<>(departments);
        deptComboBox.setBounds(140, 105, 180, 25);
        add(deptComboBox);

        JLabel m1Label = new JLabel("Subject 1 Marks:");
        m1Label.setBounds(390, 25, 120, 25);
        add(m1Label);

        mark1Field = new JTextField();
        mark1Field.setBounds(520, 25, 160, 25);
        add(mark1Field);

        JLabel m2Label = new JLabel("Subject 2 Marks:");
        m2Label.setBounds(390, 65, 120, 25);
        add(m2Label);

        mark2Field = new JTextField();
        mark2Field.setBounds(520, 65, 160, 25);
        add(mark2Field);

        JLabel m3Label = new JLabel("Subject 3 Marks:");
        m3Label.setBounds(390, 105, 120, 25);
        add(m3Label);

        mark3Field = new JTextField();
        mark3Field.setBounds(520, 105, 160, 25);
        add(mark3Field);

        // --- Buttons ---
        JButton addButton = new JButton("Add & Calculate");
        addButton.setBounds(30, 155, 150, 30);
        add(addButton);

        JButton sortNameButton = new JButton("Sort by Name");
        sortNameButton.setBounds(195, 155, 150, 30);
        add(sortNameButton);

        JButton sortPctButton = new JButton("Sort by %");
        sortPctButton.setBounds(360, 155, 150, 30);
        add(sortPctButton);

        JButton summaryButton = new JButton("Class Summary");
        summaryButton.setBounds(525, 155, 155, 30);
        add(summaryButton);

        // --- Table Setup ---
        String[] columns = {"ID", "Name", "Department", "Total", "Percentage", "Grade", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 205, 650, 330);
        add(scrollPane);

        // --- Action Listeners ---
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Input Validation & Exception Handling (CO3)
                    if (idField.getText().isEmpty() || nameField.getText().isEmpty() ||
                            mark1Field.getText().isEmpty() || mark2Field.getText().isEmpty() || mark3Field.getText().isEmpty()) {
                        throw new IllegalArgumentException("All fields must be filled out!");
                    }

                    if (studentMap.containsKey(idField.getText())) {
                        throw new IllegalArgumentException("Student ID already exists!");
                    }

                    double m1 = Double.parseDouble(mark1Field.getText());
                    double m2 = Double.parseDouble(mark2Field.getText());
                    double m3 = Double.parseDouble(mark3Field.getText());

                    double[] marks = {m1, m2, m3};
                    String id = idField.getText();
                    String name = nameField.getText();
                    String dept = (String) deptComboBox.getSelectedItem();

                    // Create Student object (throws InvalidMarksException if out of bounds)
                    Student student = new Student(id, name, dept, marks);

                    // Store in Collections (CO4)
                    studentList.add(student);
                    studentMap.put(id, student);

                    refreshTable();
                    clearFields();

                    JOptionPane.showMessageDialog(null, "Result calculated and saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric values for marks.", "Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (InvalidMarksException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Marks Validation Error", JOptionPane.WARNING_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        sortPctButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Uses Comparable implemented in Student class
                Collections.sort(studentList);
                refreshTable();
            }
        });

        sortNameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Uses Comparator to sort by name alphabetically
                studentList.sort(Comparator.comparing(Student::getName));
                refreshTable();
            }
        });

        summaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (studentList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No student records available.", "Summary", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                double totalClassPct = 0;
                int passCount = 0;
                for (Student s : studentList) {
                    totalClassPct += s.getPercentage();
                    if (s.getStatus().equals("PASS")) passCount++;
                }
                double avgPct = totalClassPct / studentList.size();

                String summary = "Total Students: " + studentList.size() + "\n" +
                                 "Passed: " + passCount + "\n" +
                                 "Failed: " + (studentList.size() - passCount) + "\n" +
                                 "Average Class Percentage: " + String.format("%.2f", avgPct) + "%";

                JOptionPane.showMessageDialog(null, summary, "Examination Summary", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear existing table data
        for (Student s : studentList) {
            Object[] row = {
                s.getStudentId(),
                s.getName(),
                s.getDepartment(),
                s.getTotalMarks(),
                String.format("%.2f%%", s.getPercentage()),
                s.getGrade(),
                s.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        mark1Field.setText("");
        mark2Field.setText("");
        mark3Field.setText("");
        deptComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        new StudentExaminationSystem();
    }
}