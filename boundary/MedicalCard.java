package boundary;

import Entity.Patient;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MedicalCard {

    private PatientUI pm;  // reference to PatientManagement

    public MedicalCard(Patient patient, PatientUI pm) {
        this.pm = pm;
        displayPatientCard(patient);
    }

    // Display patient info in CLI
    private void displayPatientCard(Patient patient) {
        System.out.println("====================================");
        System.out.println("       TARUMT MEDICAL CARD          ");
        System.out.println("====================================");
        System.out.println("Name            : " + patient.getName().toUpperCase());
        System.out.println("Patient ID      : " + patient.getPatientId().toUpperCase());
        System.out.println("IC No           : " + patient.getICNo());
        System.out.println("Blood Type      : " + patient.getBloodType().toUpperCase());
        System.out.println("Gender          : " + patient.getGender());
        System.out.println("Emergency No    : " + patient.getEmergencyNo());
        System.out.println();
        
        // Expiry date = today + 1 year
        LocalDate expiry = LocalDate.now().plusYears(1);
        String expiryDate = expiry.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("Expiry Date     : " + expiryDate);

        System.out.println("====================================");

Scanner sc = new Scanner(System.in);
String choice;
int attempts = 0; // counter for invalid inputs
final int MAX_ATTEMPTS = 5;

while (true) {
    System.out.print("Do you want to save this card as a PNG image? (yes/no): ");
    choice = sc.nextLine().trim().toLowerCase();

    if (choice.equals("yes") || choice.equals("y")) {
        saveCardAsImage(patient);
        break;
    } else if (choice.equals("no") || choice.equals("n")) {
        if (pm != null) {
            pm.getPatientManagementMenu();
        }
        break;
    } else {
        attempts++;
        System.out.println("Invalid input. Please enter Yes or No. Attempt " + attempts + "/" + MAX_ATTEMPTS);
        if (attempts >= MAX_ATTEMPTS) {
            System.out.println("\nToo many invalid attempts! Returning to Patient Management Menu...");
            if (pm != null) {
                pm.getPatientManagementMenu();
            }
            break;
        }
    }
}
}

    //  Save patient card as PNG image (with aligned colons)
    private void saveCardAsImage(Patient patient) {
        int width = 700;
        int height = 350;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Enable anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background color
        g.setColor(new Color(240, 248, 255)); // light blue
        g.fillRect(0, 0, width, height);

        // Card border
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(10, 10, width - 20, height - 20, 25, 25);

        // Title (centered)
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(new Color(0, 70, 140));
        String title = "TARUMT MEDICAL CARD";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (width - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 50);

        // Divider
        g.setStroke(new BasicStroke(2));
        g.drawLine(50, 70, width - 50, 70);

        // Patient info font
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.BLACK);

        // Define X positions for alignment
        int labelX = 50;
        int colonX = 220;  // fixed X for ":"
        int valueX = 240;  // fixed X for values

        int y = 110;
        int lineHeight = 28;

      // Draw other lines (Name → Emergency No)
      y = drawLine(g, "Name", patient.getName(), labelX, colonX, valueX, y, lineHeight);
      y = drawLine(g, "Patient ID", patient.getPatientId(), labelX, colonX, valueX, y, lineHeight);
      y = drawLine(g, "IC No", patient.getICNo(), labelX, colonX, valueX, y, lineHeight);
      y = drawLine(g, "Blood Type", patient.getBloodType(), labelX, colonX, valueX, y, lineHeight);
      y = drawLine(g, "Gender", patient.getGender(), labelX, colonX, valueX, y, lineHeight);
      y = drawLine(g, "Emergency No", patient.getEmergencyNo(), labelX, colonX, valueX, y, lineHeight);

      //  Expiry Date always near bottom (don’t move X position)
      LocalDate expiry = LocalDate.now().plusYears(1);
      String expiryDate = expiry.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      int expiryY = height - 40;  // 40px above bottom edge
      drawLine(g, "Expiry Date", expiryDate, labelX, colonX, valueX, expiryY, lineHeight);

        g.dispose();

        try {
            // Save on Desktop
            File desktopDir = new File(System.getProperty("user.home"), "Desktop");
            if (!desktopDir.exists()) {
                desktopDir = new File(System.getProperty("user.home"));
            }
            File file = new File(desktopDir, "MedicalCard_" + patient.getPatientId() + ".png");
            ImageIO.write(image, "png", file);
            System.out.println(" Medical card saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println(" Error saving image: " + e.getMessage());
        }

        // Return to menu after saving
        if (pm != null) {
            pm.getPatientManagementMenu();
        }
    }

    /**
     * Helper method to draw one line with aligned label, colon, and value.
     * Returns the updated Y position.
     */
    private int drawLine(Graphics2D g, String label, String value,
                         int labelX, int colonX, int valueX,
                         int y, int lineHeight) {
        g.drawString(label, labelX, y);
        g.drawString(":", colonX, y);
        g.drawString(value, valueX, y);
        return y + lineHeight;
    }
}
