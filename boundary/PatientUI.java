
package boundary;

import java.util.Scanner;
import ADT.MapInterface;
import ADT.HashMap;
import ADT.MapEntry;
import Entity.Patient;
import Entity.Payment;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PatientUI {
    private MapInterface<String, Payment> paymentMap = new HashMap<>();
    private MapInterface<String, Patient> patientMap;
    private Scanner scanner = new Scanner(System.in);
    private Queue<String> patientQueue;

// ---------------- Patient Managemnt Menu ----------------
 public int getPatientManagementMenu() {
    int choice = -1;

    do {
        System.out.println("\nPATIENT MANAGEMENT MENU\n------------------------");
        System.out.println("1. Register new patient");
        System.out.println("2. Edit patient record");
        System.out.println("3. Walk-in patient queue");
        System.out.println("4. Payment");
        System.out.println("0. Quit");
        System.out.print("Enter choice: ");

        // Validate input
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 5.");
            scanner.nextLine(); // clear invalid input
            continue;           // redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 4) {
            System.out.println("Invalid choice. Please enter a number between 0 and 4.");
        }

    } while (choice < 0 || choice > 4);

    return choice;
}

// ---------------- Patient Record Menu ----------------

public int getPatientRecordMenu() {
    int choice = -1;

    do {
        System.out.println("\nPATIENT RECORD MENU\n-------------------");
        System.out.println("1. View patient record");
        System.out.println("2. Search patient record");
        System.out.println("3. Update patient record");
        System.out.println("4. Delete patient record");
        System.out.println("0. Back to previous menu");
        System.out.print("Enter choice: ");

        // Validate non-integer input
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 4.");
            scanner.nextLine(); // clear invalid input
            continue;           // restart loop, redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 4) {
            System.out.println("Invalid choice. Please enter a number between 0 and 4.");
        }

    } while (choice < 0 || choice > 4);

    return choice;
}


// ---------------- Update Patient Menu ----------------

public int getPatientUpdateMenu() {
    int choice = -1;

    do {
        System.out.println("\nUPDATE PATIENT RECORD MENU\n-------------------------");
        System.out.println("1. Patient Name");
        System.out.println("2. IC Number");
        System.out.println("3. Gender");
        System.out.println("4. Age");
        System.out.println("5. Blood Type");
        System.out.println("6. Date of Birth");
        System.out.println("7. Contact Number");
        System.out.println("8. Emergency Number");
        System.out.println("9. Medical History");
        System.out.println("10. Address");
        System.out.println("11. Email");
        System.out.println("0. Back to previous menu");
        System.out.print("Select field to update: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 11.");
            scanner.nextLine(); // clear invalid input
            continue;           // restart loop, redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 11) {
            System.out.println("Invalid choice. Please enter a number between 0 and 11.");
        }

    } while (choice < 0 || choice > 11);

    return choice;
}



// ---------------- Walk in PAtient Queue Entry Menu ----------------
public int getQueueEntryMenu() {
    int choice = -1;

    do {
        System.out.println("\nWALK-IN PATIENT QUEUE MENU\n-------------------------");
        System.out.println("1. Add Queue Entry");
        System.out.println("2. View Current Queue Entry");
        System.out.println("3. Delete Queue Entry");
        System.out.println("0. Return to Previous Menu");
        System.out.print("Enter choice: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 3.");
            scanner.nextLine(); // clear invalid input
            continue;           // restart loop and redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 3) {
            System.out.println("Invalid choice. Please enter a number between 0 and 3.");
        }

    } while (choice < 0 || choice > 3);

    return choice;
}


// ------------------ Walk in Queue Report ------------------ //
// queueList is now Queue<String>, patientMap is MapInterface<String, Patient>
public String generateQueueReport(Queue<String> patientQueue, MapInterface<String, Patient> patientMap) {
    StringBuilder sb = new StringBuilder();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    int lineLength = 100; // width for centering
    String stars = "*".repeat(lineLength);
    String dashes = "-".repeat(lineLength);

    // Helper to center text
    java.util.function.Function<String, String> center = text -> {
        int padding = (lineLength - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    };

    // ---------------- Header ----------------
    sb.append(stars).append("\n");
    sb.append(center.apply("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY")).append("\n");
    sb.append(center.apply("CLINIC MANAGEMENT SYSTEM")).append("\n");
    sb.append(center.apply("PATIENTS WALK-IN QUEUE REPORT")).append("\n");
    sb.append(stars).append("\n");
    sb.append("Generated at: ").append(java.time.LocalDateTime.now().format(dtf)).append("\n\n");
    sb.append("PQ01 = Patient walk-in Queue Report\n");
    sb.append(dashes).append("\n");
    // ---------------- Table Header ----------------
    sb.append(String.format("%-5s | %-10s | %-6s | %-10s\n", "No", "Patient ID", "Gender", "Blood Type"));
    sb.append(dashes).append("\n");

    // ---------------- Table Rows ----------------
    if (patientQueue == null || patientQueue.isEmpty()) {
        sb.append(center.apply("No queue entries found")).append("\n");
    } else {
        int no = 1;
        for (String patientId : patientQueue) {
            Patient p = patientMap.get(patientId);
            if (p != null) {
                sb.append(String.format("%-5d | %-10s | %-6s | %-10s\n",
                        no++, p.getPatientId(), p.getGender(), p.getBloodType()));
            }
        }
    }

    sb.append(dashes).append("\n");
    sb.append("Total queue entries: ").append(patientQueue == null ? 0 : patientQueue.size()).append("\n");
    sb.append(stars).append("\n");
    sb.append(center.apply("END OF REPORT")).append("\n");
    sb.append(stars).append("\n");

    return sb.toString();
}

// ---------------- Display ---------------- //
public void displayQueueReport(String report) {
    System.out.println(report);
}

// ---------------- Export as PNG ---------------- //
public void exportQueueReportAsPNG(String reportContent, Dimension frameSize) {
    try {
        String[] lines = reportContent.split("\n");

        int width = frameSize.width - 20; // leave padding
        int lineHeight = 25;
        int height = Math.max(frameSize.height, lineHeight * (lines.length + 1));

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));

        int y = lineHeight;
        for (String line : lines) {
            g.drawString(line, 10, y);
            y += lineHeight;
        }

        g.dispose();

        File file = new File("QueueReport.png");
        ImageIO.write(image, "png", file);
        System.out.println("Queue report saved as " + file.getAbsolutePath());

    } catch (Exception e) {
        System.out.println("Error generating PNG: " + e.getMessage());
    }
}


// ------------------ Payment Menu ------------------ //
public int getPaymentMenu() {
    int choice = -1;

    do {
        System.out.println("\n--- Payment Menu ---");
        System.out.println("1. Generate Receipt");
        System.out.println("2. Payment History");
        System.out.println("3. Clear Payment");
        System.out.println("0. Return to Previous Menu");
        System.out.print("Enter choice: ");

        String input = scanner.nextLine().trim();

        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter digits only.");
            continue; // redraw menu
        }

        if (choice < 0 || choice > 3) {
            System.out.println("Invalid choice. Please enter a number between 0 and 3.");
            choice = -1; // force loop again
        }

    } while (choice < 0 || choice > 3);

    return choice;
}   

// ---------------- Generate Payment History Report ---------------- //
public String generatePaymentHistoryReport(MapInterface<String, Payment> paymentMap,
                                           MapInterface<String, Patient> patientMap) {
    StringBuilder sb = new StringBuilder();
    int lineLength = 100;
    String stars = "*".repeat(lineLength);
    String dashes = "-".repeat(lineLength);
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    java.util.function.Function<String, String> center = text -> {
        int padding = (lineLength - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    };

    // ---------------- Header ----------------
    sb.append(stars).append("\n");
    sb.append(center.apply("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY")).append("\n");
    sb.append(center.apply("CLINIC MANAGEMENT SYSTEM")).append("\n");
    sb.append(center.apply("PATIENT PAYMENT HISTORY REPORT")).append("\n");
    sb.append(stars).append("\n");
    sb.append("Generated at: ").append(java.time.LocalDateTime.now().format(dtf)).append("\n\n");
    sb.append("PH01 = Patient Payment History Report\n");
    sb.append(dashes).append("\n");

    // ---------------- Table Header ----------------
    sb.append(String.format("%-12s | %-10s | %-15s | %-10s\n",
            "Patient ID", "Payment ID", "Payment Method", "Payment Amount"));
    sb.append(dashes).append("\n");

    // ---------------- Table Rows ----------------
    MapEntry<String, Payment>[] entries = paymentMap.getTable();
    for (MapEntry<String, Payment> e : entries) {
        if (e != null && !e.isRemoved()) {
            Payment payment = e.getValue();
            Patient patient = patientMap.get(payment.getPatientId());
            if (patient != null) {
                sb.append(String.format("%-12s | %-10s | %-15s | %-10.2f\n",
                        patient.getPatientId(),
                        payment.getPaymentId(),
                        payment.getPaymentMethod(),
                        payment.getAmount()));
            }
        }
    }

    sb.append(dashes).append("\n");
    sb.append("Total payments: ").append(paymentMap.size()).append("\n");
    sb.append(stars).append("\n");
    sb.append(center.apply("END OF REPORT")).append("\n");
    sb.append(stars).append("\n");

    return sb.toString();
}

    // ---------------- Display ---------------- //
    public void displayPaymentHistoryReport(String report) {
        System.out.println(report);
    }

    // ---------------- Export as PNG ---------------- //
    public void exportPaymentHistoryAsPNG(String reportContent, Dimension frameSize) {
        try {
            String[] lines = reportContent.split("\n");

            int width = frameSize.width - 20; // leave padding
            int lineHeight = 25;
            int height = Math.max(frameSize.height, lineHeight * (lines.length + 1));

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.PLAIN, 16));

            int y = lineHeight;
            for (String line : lines) {
                g.drawString(line, 10, y);
                y += lineHeight;
            }

            g.dispose();

            File file = new File("PaymentHistoryReport.png");
            ImageIO.write(image, "png", file);
            System.out.println("Payment history report saved as " + file.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("Error generating PNG: " + e.getMessage());
        }
    }
    
    
    // --------------- Input Patient Registeration Form Module ---------------- //
    public String inputPatientName() {
        System.out.print("Enter patient name: ");
        return scanner.nextLine();
    }

    public String inputPatientIC() {
        System.out.print("Enter IC No: ");
        return scanner.nextLine();
    }

    public String inputPatientGender() {
        System.out.print("Enter Gender (M/F): ");
        return scanner.nextLine();
    }

   public int inputPatientAge() {
    int age = -1;
    while (true) {
        System.out.print("Enter age: ");
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
            if (age < 0) {
                System.out.println("Age cannot be negative. Please enter again.");
            } else {
                break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter digits only.");
        }
    }
    return age;
}

    public String inputBloodType() {
        System.out.print("Enter Blood Type: ");
        return scanner.nextLine();
    }

    public String inputDateOfBirth() {
        System.out.print("Enter Date of Birth (yyyy-MM-dd): ");
        return scanner.nextLine();
    }

    public String inputContactNo() {
        System.out.print("Enter Personal Contact Number: ");
        return scanner.nextLine();
    }

    public String inputEmergencyNo() {
        System.out.print("Enter Emergency Contact Number: ");
        return scanner.nextLine();
    }

    public String inputMedicalHistory() {
        System.out.print("Enter Medical History: ");
        return scanner.nextLine();
    }

    public String inputAddress() {
        System.out.print("Enter Address: ");
        return scanner.nextLine();
    }

    public String inputEmail() {
        System.out.print("Enter Email: ");
        return scanner.nextLine();
    }
    
    public String inputCourse() {
    System.out.print("Enter Course (Full Name or 3-letter code): ");
    return scanner.nextLine().trim().toUpperCase();
}
    
    // ------------------ Search Patient ID Input ------------------ //
    public String promptPatientId(String action) {
    System.out.print("Enter Patient ID to " + action + ": ");
    return scanner.nextLine().trim().toUpperCase();
}

    // ---------------- Search Patient Output ---------------- //
    public void printPatientDetails(Patient patient) {
        System.out.println("\nPatient Details\n---------------");
        System.out.println(patient.toString());
    }

    // ---------------- Patients Report Output ---------------- //
     public String generatePatientReport(MapInterface<String, Patient> patientMap) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int lineLength = 179;
        String stars = "*".repeat(lineLength);
        String dashes = "-".repeat(lineLength);

        java.util.function.Function<String, String> center = text -> {
            int padding = (lineLength - text.length()) / 2;
            return " ".repeat(Math.max(0, padding)) + text;
        };

        // Header
        sb.append(stars).append("\n");
        sb.append(center.apply("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY")).append("\n");
        sb.append(center.apply("CLINIC MANAGEMENT SYSTEM")).append("\n");
        sb.append(center.apply("PATIENTS REPORT 2025")).append("\n");
        sb.append(stars).append("\n");
        sb.append("Generated at: ").append(java.time.LocalDateTime.now().format(dtf)).append("\n\n");
        sb.append("PC01 = Patient Registration Report\n");
        sb.append(dashes).append("\n");

        // Table header
        sb.append(String.format(
            " %-10s | %-18s | %-12s | %-6s | %-3s | %-9s | %-10s | %-11s | %-11s | %-19s | %-19s | %-13s \n",
            "Patient ID", "Patient Name", "IC Number", "Gender", "Age",
            "BloodType", "DateOfBirth", "ContactNo", "EmergencyNo", "MedicalHistory", "Address", "Email"));
        sb.append(dashes).append("\n");

        // Table rows
        boolean found = false;
        int count = 0;
        MapEntry<String, Patient>[] entries = patientMap.getTable();

        for (MapEntry<String, Patient> e : entries) {
            if (e != null && !e.isRemoved()) {
                Patient p = e.getValue();
                sb.append(String.format(
                    " %-10s | %-18s | %-12s | %-6s | %-3d | %-9s | %-10s | %-11s | %-11s | %-19s | %-19s | %-13s \n",
                    p.getPatientId(),
                    p.getName(),
                    p.getICNo(),
                    p.getGender(),
                    p.getAge(),
                    p.getBloodType(),
                    p.getDateOfBirth(),
                    p.getContactNo(),
                    p.getEmergencyNo(),
                    p.getMedicalHistory(),
                    p.getAddress(),
                    p.getEmail()));
                found = true;
                count++;
            }
        }

        if (!found) {
            sb.append(" No patient records found.").append(" ".repeat(151)).append("\n");
        }

        sb.append(dashes).append("\n");
        sb.append(" Total Patients Registered: ").append(count).append("\n");

        // Footer
        sb.append(stars).append("\n");
        sb.append(center.apply("END OF REPORT")).append("\n");
        sb.append(stars).append("\n");

        return sb.toString();
    }

    // ---------------- Display Report ---------------- //
    public void displayPatientReport(String report) {
        System.out.println(report);
    }

    // ---------------- Export as PNG ---------------- //
    public void exportReportAsPNG(String reportContent) {
        try {
            String[] lines = reportContent.split("\n");

            int lineHeight = 25;
            int padding = 20;
            int width = 1600; // wide enough for full report
            int height = lineHeight * lines.length + padding;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.PLAIN, 16));

            int y = lineHeight;
            for (String line : lines) {
                g.drawString(line, 10, y);
                y += lineHeight;
            }

            g.dispose();

            File file = new File("PatientReport.png");
            ImageIO.write(image, "png", file);
            System.out.println("Report saved as " + file.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("Error generating PNG: " + e.getMessage());
        }};   
    
    
    public void displayPaymentReceipt(String receipt) {
        System.out.println("\n--- Payment Receipt ---");
        System.out.println(receipt);
}    
    // ------------------ Input Patient ID with Validation ------------------ //
        public String inputPatientId() {
        String patientId;
            while (true) {
            System.out.print("Enter Patient ID: ");
            patientId = scanner.nextLine().trim().toUpperCase();

        // Validate ID exists in patientMap
        if (patientMap.containsKey(patientId)) break;
        System.out.println("Patient not found. Please enter a valid Patient ID.");
    }
    return patientId;
}

// ------------------ Input Payment Method Module ------------------ //
    public int inputPaymentMethod() {
    int method = -1;
    do {
        System.out.println("Select Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. Touch 'n Go");
        System.out.print("Enter choice (1-3): ");
        try {
            method = Integer.parseInt(scanner.nextLine());
            if (method < 1 || method > 3) {
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter digits only.");
        }
    } while (method < 1 || method > 3);
    return method;
}
 
    
    // ------------------ Generic Messages ------------------ //
public void displayMessage(String msg) {
    System.out.println(msg);
} 
}
