
//koo jing yik
package control;

import java.util.Scanner;
import Entity.Patient;
import boundary.PatientUI;
import ADT.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import Entity.Appointment;
import Entity.Payment;
import boundary.MedicalCard;
import dao.GenericDAO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PatientControl {
    private static MapInterface<String, Payment> paymentMap = new HashMap<>();
    private static MapInterface<String, Appointment> appointmentMap = new HashMap<>();
    private static MapInterface<String, Patient> patientMap;
    private static QueueInterface<Patient> patientQueue; // store Patients in queue
    private PatientUI patientUI;
    private Scanner scanner = new Scanner(System.in);
    private int paymentCounter = 1;
    private static String patientHashMapFile = "data/patientHashMap.dat";
    private static String patientQueueFile = "data/patientQueue.dat";
    private static String patientTextFile = "data/patients.txt";
        
    // Existing constructor
    public PatientControl(PatientUI patientUI) {
        this.patientUI = patientUI;

        patientMap = GenericDAO.retrieveFromFile(patientHashMapFile);
        patientQueue = GenericDAO.retrieveFromFile(patientQueueFile);
        if (patientMap == null || patientQueue == null) {
            patientMap = new HashMap<>(30);
            patientQueue = new ArrayQueue<>(30);
            if (GenericDAO.loadPatientsFromTextFile(patientTextFile, patientMap, patientQueue)){
                GenericDAO.saveToFile(patientMap, patientHashMapFile);
                GenericDAO.saveToFile(patientQueue, patientQueueFile);
            } else {
                System.out.println("Cannot initialize patient records from text file.");
            }
        }
    }  
    
    public static MapInterface<String,Patient> getPatientMap(){
        return patientMap;
    }
    
    public static MapInterface<String,Payment> getPaymentMap(){
        return paymentMap;
    }
    
    public static QueueInterface<Patient> getPatientQueue(){
        return patientQueue;
    }
        // ------------------  Selection Course Module  ------------------ //
    
    private static final HashMap<String, String> COURSE_MAP = new HashMap<>();
    static {
        COURSE_MAP.add("ACCOUNTING", "ACC");
        COURSE_MAP.add("FINANCE", "FIN");
        COURSE_MAP.add("BUSINESS", "COM");
        COURSE_MAP.add("ACCOUNTINGFINANCE", "AFN");
        COURSE_MAP.add("MARKETING", "MKT");
        COURSE_MAP.add("MANAGEMENT", "MGT");
        COURSE_MAP.add("HUMANRESOURCEMANAGEMENT", "HRM");
        COURSE_MAP.add("ISLAMICBANKINGFINANCE", "IBF");
        COURSE_MAP.add("MANAGEMENTMATHEMATICSCOMPUTING", "RMM");
        COURSE_MAP.add("INTERACTIVESOFTWARETECHNOLOGY", "RST");
        COURSE_MAP.add("ENTERPRISEINFORMATIONSYSTEMS", "REI");
        COURSE_MAP.add("INFORMATIONSECURITY", "RIS");
        COURSE_MAP.add("SOFTWARESYSTEMSDEVELOPMENT", "RSD");
        COURSE_MAP.add("ELECTRICALELECTRONICENGINEERING", "EEE");
        COURSE_MAP.add("TELECOMMUNICATIONSENGINEERING", "TEL");
        COURSE_MAP.add("MANUFACTURINGINDUSTRIALTECHNOLOGY", "MIT");
        COURSE_MAP.add("MATERIALSMANUFACTURINGTECHNOLOGY", "MTM");
        COURSE_MAP.add("CONSTRUCTIONMANAGEMENTECONOMICS", "CEM");
        COURSE_MAP.add("ARCHITECTURE", "ARC");
        COURSE_MAP.add("REALESTATEMANAGEMENT", "REM");
        COURSE_MAP.add("QUANTITYSURVEYING", "QSB");
        COURSE_MAP.add("INTERIORARCHITECTURE", "IAE");
        COURSE_MAP.add("MEDIAMANAGEMENT", "MMC");
        COURSE_MAP.add("ADVERTISINGMARKETING", "AMC");
        COURSE_MAP.add("GRAPHICDESIGN", "GDS");
        COURSE_MAP.add("INTERIORDESIGN", "IDS");
        COURSE_MAP.add("EVENTMANAGEMENT", "EMM");
        COURSE_MAP.add("PSYCHOLOGY", "PSY");
        COURSE_MAP.add("SOCIOLOGYSOCIALDEVELOPMENT", "SDS");
        COURSE_MAP.add("INTERNATIONALSTUDIES", "ISD");
        COURSE_MAP.add("BIOTECHNOLOGY", "BTH");
        COURSE_MAP.add("APPLIEDCHEMISTRY", "ACS");
        COURSE_MAP.add("ENVIRONMENTALSCIENCE", "ENV");
        COURSE_MAP.add("FOODSCIENCE", "FDS");
        COURSE_MAP.add("MARINESCIENCE", "MSC");
    }
    

    public int getValidatedMainMenuChoice() {
    int choice = -1;
    while (true) {
        try {
            choice = patientUI.getPatientManagementMenu(); 
            if (choice >= 0 && choice <= 4) {
                return choice;
            } else {
                patientUI.displayMessage("Invalid choice. Please enter 0-4.");
            }
        } catch (NumberFormatException e) {
            patientUI.displayMessage("Invalid input. Please enter a number.");
        }
    }
}

    // ------------------ Register New Patient Module ------------------ //
public void registerNewPatient() {
    System.out.println("\n--- Register New Patient ---");

    // Collect patient info
     // Name
    String name = inputValidPatientName();
    if (name == null) return; // stop registration and go back to menu

    // IC
    String ic = inputValidIC();
    if (ic == null) return; // stop registration and go back to menu

    // Gender
    String gender = inputValidGender();
    if (gender == null) return;

    // Age
    int age = inputValidAge();
    if (age == -1) return;

    // Blood Type
    String bloodType = inputValidBloodType();
    if (bloodType == null) return;

    // Date of Birth
    String dob = inputValidDateOfBirth(age);
    if (dob == null) return;

    // Contact No
    String contactNo = inputValidContactNo();
    if (contactNo == null) return;

    // Emergency No
    String emergencyNo = inputValidEmergencyNo(contactNo);
    if (emergencyNo == null) return;

    // Medical History
    String medicalHistory = patientUI.inputMedicalHistory();

    // Address
    String address = inputValidAddress();
    if (address == null) return;

    // Email
    String email = inputValidEmail();
    if (email == null) return;

    // Course
    String course = inputValidCourse();
    if (course == null) return;


    // Generate Patient ID automatically
    String id = generatePatientId(dob, course);

    // Create patient object
    Patient patient = new Patient(id, name, ic, gender, age, bloodType, dob, contactNo,
                                  emergencyNo, medicalHistory, address, email, course);

    // Store in patient map
    Scanner scanner = new Scanner(System.in);
    patientMap.add(id, patient);
    GenericDAO.saveToFile(patientMap, patientHashMapFile);
    System.out.print("\nPatient registered successfully. Patient ID: " + id + "\n");

    // Ask user if they want to generate ID card
    Scanner sc = new Scanner(System.in);
    String choice;
    while (true) {
        System.out.print("\nDo you want to generate the patient ID card? (Y/N): \n");
        choice = sc.nextLine().trim().toUpperCase();
     if (choice.equals("Y")) {
      // Open the MedicalCard JFrame with patient info
           PatientUI pm = new PatientUI();
        MedicalCard cardUI = new MedicalCard(patient, pm);
          break;
        } else if (choice.equals("N")) {
            System.out.println("Returning to Patient Management Menu...");
            break;
        } else {
            System.out.println("Invalid input. Please enter Y or N.");
        }
    }
}


    // ------------------ Automatic Patient ID Generation ------------------ //
    private String generatePatientId(String dob, String courseCode) {
        // Get last 2 digits of year
        String year = dob.substring(2, 4);
        String prefix = year + courseCode;

        int max = 0;
        MapEntry<String, Patient>[] entries = patientMap.getTable();
        for (MapEntry<String, Patient> e : entries) {
            if (e != null && !e.isRemoved()) {
                String existingId = e.getValue().getPatientId();
                if (existingId.startsWith(prefix)) {
                    int num = Integer.parseInt(existingId.substring(5));
                    if (num > max) max = num;
                }
            }
        }

        int newNum = max + 1;
        String formattedNum = String.format("%04d", newNum);
        return prefix + formattedNum;
    }

    
    
    // ---------------- Validate Edit Patient Menu Module ----------------
    public void handlePatientRecordMenu() {
    boolean recordExit = false;
    while (!recordExit) {
        int recordChoice = patientUI.getPatientRecordMenu();
        switch (recordChoice) {
            case 1: // View all
                listAllPatients();   
                break;
            case 2: // Search
                String searchId = patientUI.promptPatientId("search");
                searchPatient(searchId);
                break;
            case 3: // Update
                String updateId = patientUI.promptPatientId("update");
                updatePatientField(updateId);
                break;
            case 4: // Delete
                String delId = patientUI.promptPatientId("delete");
                deletePatient(delId);
                break;
            case 0:
                recordExit = true;   //  back to Patient Management Menu
                break;
            default:
                patientUI.displayMessage("Invalid choice.");
        }
    }
}

    private String inputValidPatientName() {
    String name;
    int attempts = 0;
    final int MAX_ATTEMPTS = 5;

    while (true) {
        name = patientUI.inputPatientName().trim();

        // Allow only alphabets, spaces, and must not be empty
        if (!name.matches("^[A-Za-z ]+$")) {
            System.out.println("Invalid Name. Only alphabets and spaces are allowed.");
            attempts++;
        } else if (name.length() < 2 || name.length() > 50) {
            System.out.println("Invalid Name length. Must be between 2 and 50 characters.");
            attempts++;
        } else {
            return name; //  valid name
        }

        if (attempts >= MAX_ATTEMPTS) {
            System.out.println("\nToo many invalid attempts! Returning to Patient Management Menu...");
            return null; // fail → return to menu
        }
    }
}

private String inputValidIC() {
    String ic;
    int attempts = 0; // track invalid attempts

    while (true) {
        ic = patientUI.inputPatientIC().trim();

        // Validate format: must be 12 digits
        if (!ic.matches("\\d{12}")) {
            System.out.println("Invalid IC. Must be 12 digits only.");
            attempts++;
        } else {
            // Check for duplicates in patientMap
            boolean exists = false;
            MapEntry<String, Patient>[] entries = patientMap.getTable();
            for (MapEntry<String, Patient> e : entries) {
                if (e != null && !e.isRemoved() && e.getValue().getICNo().equals(ic)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                System.out.println("IC number has already been registered.");
                attempts++;
            } else {
                return ic; //  valid and unique → return IC
            }
        }

        // Too many invalid attempts
        if (attempts >= 5) {
            System.out.println("\nToo many invalid attempts! Returning to Patient Management Menu...");
            patientUI.getPatientManagementMenu(); //  go back to menu
            return null; // stop input
        }
    }
}

    private String inputValidGender() {
        String gender;
        int attempts = 0; // counter for invalid input

        while (true) {
            gender = patientUI.inputPatientGender().trim();

            // Allow only "MALE", "male", "FEMALE", "female"
            if (gender.equalsIgnoreCase("MALE") || gender.equalsIgnoreCase("FEMALE")) {
                // Normalize input to capitalized form (optional)
                gender = gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
                break; // valid input
            } else {
                attempts++;
                System.out.println("Invalid Gender. Must be MALE or FEMALE.");

                if (attempts >= 5) {
                    System.out.println("Too many invalid attempts. Returning to Patient Management Menu...");
                    patientUI.getPatientManagementMenu();
                    return null; // exit method
                }
            }
        }
        return gender;
    }


private int inputValidAge() {
    int age;
    int invalidAttempts = 0;

    while (true) {
        age = patientUI.inputPatientAge();

        if (age >= 17 && age <= 100) {
            return age; //  valid, return age
        } else {
            invalidAttempts++;
            System.out.println("Invalid Age. Must be between 17 and 100. Attempt " 
                                + invalidAttempts + "/5");

            //  More than 5 invalid attempts → auto back to menu
            if (invalidAttempts >= 5) {
                System.out.println("\nToo many invalid attempts! Returning to Patient Management Menu...");
                patientUI.getPatientManagementMenu();
                return -1; // return dummy age to indicate failure
            }
        }
    }
}


private String inputValidBloodType() {
    String bloodType;
    int attempts = 0;  // counter for invalid attempts

    while (true) {
        bloodType = patientUI.inputBloodType();
        if (bloodType.matches("^(A\\+|A-|B\\+|B-|AB\\+|AB-|O\\+|O-)$")) {
            break; // valid blood type
        } else {
            attempts++;
            System.out.println("Invalid Blood Type. Allowed: A+/-, B+/-, AB+/-, O+/- . ");
            
            if (attempts >= 5) {
                System.out.println("Too many invalid attempts. Returning to Patient Management Menu...");
                // Redirect back to the menu
                PatientUI menu = new PatientUI();
                menu.getPatientManagementMenu();
                return null; // return null to stop further execution
            }
        }
    }

    return bloodType;
}

private String inputValidDateOfBirth(int expectedAge) {
    String dob;
    int attempts = 0; // counter for invalid attempts

    while (true) {
        dob = patientUI.inputDateOfBirth();
        attempts++;

        try {
            LocalDate date = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate today = LocalDate.now();

            //  Invalid: Future date
            if (date.isAfter(today)) {
                System.out.println("Date of Birth must not be in the future.");
            }
            //  Invalid: Same as today
            else if (date.isEqual(today)) {
                System.out.println("Date of Birth cannot be today's date.");
            }
            //  Invalid: Age mismatch
            else {
                int calculatedAge = java.time.Period.between(date, today).getYears();
                if (calculatedAge != expectedAge) {
                    System.out.println("Date of Birth does not match the entered age (" + expectedAge + ").");
                } else {
                    //  Valid input
                    return dob;
                }
            }

        } catch (Exception e) {
            System.out.println("Invalid Date Format. Use yyyy-MM-dd.");
        }

        //  Exceeded 5 attempts
        if (attempts >= 5) {
            System.out.println("\n️ Too many invalid attempts. Returning to Patient Management Menu...");
            PatientUI pm = new PatientUI();
            pm.getPatientManagementMenu();  // auto back to menu
            return null; // return null since DOB was not valid
        }
    }
}


private String inputValidContactNo( ) {
    String contact = null;
    int attempts = 0;

    while (attempts < 5) {
        contact = patientUI.inputContactNo();
        if (contact.matches("^\\d{1,11}$")) {
            return contact; //  valid -> return immediately
        } else {
            System.out.println("Invalid Personal Contact No. Max 11 digits.");
            attempts++;
        }
    }

    // If reached here, user failed 5 times
    System.out.println("\nToo many invalid attempts. Returning to Patient Management Menu...");
    patientUI.getPatientManagementMenu();  //  go back to menu
    return null; // return null since no valid input
}

private String inputValidEmergencyNo(String contactNo) {
    String emergency;
    int attempts = 0;

    while (true) {
        emergency = patientUI.inputEmergencyNo();
        attempts++;

        if (!emergency.matches("^\\d{1,11}$")) {
            System.out.println("Invalid Emergency Contact No. Max 11 digits.");
        } else if (emergency.equals(contactNo)) {
            System.out.println("Emergency Contact No. cannot be the same as Personal Contact No.");
        } else {
            return emergency; //  valid input
        }

        // Check if attempts reached 5
        if (attempts >= 5) {
            System.out.println("\nToo many invalid attempts. Returning to Patient Management Menu...");
            // Call back to menu
            PatientUI pm = new PatientUI();
            pm.getPatientManagementMenu();
            return null; // Exit method, no valid emergency number
        }
    }
}



private String inputValidAddress( ) {
    String address;
    int attempts = 0;
    final int MAX_ATTEMPTS = 5;

    while (true) {
        address = patientUI.inputAddress();

        if (address.length() <= 50) {
            return address; //  valid, return
        } else {
            System.out.println("Invalid Address. Max 50 characters.");
            attempts++;
        }

        //  If exceeded max retries, return to menu
        if (attempts >= MAX_ATTEMPTS) {
            System.out.println("Too many invalid attempts. Returning to Patient Management Menu...");
            patientUI.getPatientManagementMenu(); 
            return null; // stop current operation
        }
    }
}


 private String inputValidEmail( ) {
    String email;
    int attempts = 0; // Track invalid attempts

    while (true) {
        email = patientUI.inputEmail().trim();

        // Validate format
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid Email format.");
            attempts++;
        } else {
            // Check for duplicates in patientMap
            boolean exists = false;
            MapEntry<String, Patient>[] entries = patientMap.getTable();
            for (MapEntry<String, Patient> e : entries) {
                if (e != null && !e.isRemoved() && 
                    e.getValue().getEmail().equalsIgnoreCase(email)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                System.out.println("Email address has already been registered.");
                attempts++;
            } else {
                break; // valid and unique
            }
        }

        // Check attempt limit
        if (attempts >= 5) {
            System.out.println("\nToo many invalid attempts. Returning to Patient Management Menu...");
            patientUI.getPatientManagementMenu();
            return null; // or break out depending on your flow
        }
    }

    return email;
}

 
 private String inputValidCourse() {
    String courseCode = null;
    int attempts = 0;

    while (true) {
        String input = patientUI.inputCourse(); // call input method
        courseCode = null; // reset each attempt

        //  Check if input matches course full name or 3-letter code
        boolean found = false;
        for (MapEntry<String, String> entry : COURSE_MAP.getTable()) {
            if (entry != null && !entry.isRemoved()) {
                if (entry.getValue().equals(input)) { 
                    courseCode = entry.getValue(); 
                    found = true; 
                    break;
                }
            }
        }

        if (!found && COURSE_MAP.contains(input)) {
            courseCode = COURSE_MAP.get(input);
        }

        //  Valid course found
        if (courseCode != null) {
            return courseCode;
        }

        // Invalid course handling
        attempts++;
        System.out.println("Invalid course. Please try again. (" + attempts + "/5)");

        if (attempts >= 5) {
            System.out.println("\nToo many invalid attempts! Returning to Patient Management Menu...");
            patientUI.getPatientManagementMenu(); // return to menu
            return null; // stop input
        }
    }
}
 

    // ------------------ Edit Patient Module  ------------------ //
    // ------------------ view Patient Report Module  ------------------ //  
    private void listAllPatients() {
        String fullReport = patientUI.generatePatientReport(patientMap);
        patientUI.displayPatientReport(fullReport);

        while (true) {
            System.out.print("Do you want to download this report as PNG? (Y/N): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                System.out.println("Downloading report...");
                // pass frame size (match display frame)
                exportReportAsPNG(fullReport, new Dimension(1800, 800));
                break;
            } else if (input.equals("n") || input.equals("no")) {
                System.out.println("Returning to previous menu...");
                break;
            } else {
                System.out.println("Invalid input. Please enter Y/Yes or N/No.");
            }
        }
    }

    private void exportReportAsPNG(String reportContent, Dimension frameSize) {
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

            File file = new File("PatientReport.png");
            ImageIO.write(image, "png", file);
            System.out.println("Report saved as " + file.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("Error generating PNG: " + e.getMessage());
        }
    }

    // ------------------  Search Patient Module  ------------------ //
    public Patient searchPatient(String id) {
        Patient p = patientMap.get(id);
        if (p != null) {
            patientUI.printPatientDetails(p);
        } else {
            System.out.println("No patient found with ID: " + id);
        }
        return p;
    }

    // ------------------  Update Patient Module  ------------------ //
    public void updatePatientField(String patientId) {
        Patient patient = patientMap.get(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        boolean done = false;
        while (!done) {
            int choice = patientUI.getPatientUpdateMenu();
            switch (choice) {
                case 1:
                    patient.setName(inputValidPatientName());
                    break;
                case 2:
                    patient.setICNo(inputValidIC());
                    break;
                case 3:
                    patient.setGender(inputValidGender());
                    break;
                case 4:
                    patient.setAge(inputValidAge());
                    break;
                case 5:
                    patient.setBloodType(inputValidBloodType());
                    break;
                case 6:
                    patient.setDateOfBirth(inputValidDateOfBirth(patient.getAge()));
                    break;
                case 7:
                    String newContact = inputValidContactNo();
                    if (newContact.equals(patient.getEmergencyNo())) {
                        System.out.println("Contact No. cannot be the same as Emergency No.");
                    } else {
                        patient.setContactNo(newContact);
                    }
                    break;
                case 8:
                    String newEmergency = inputValidEmergencyNo(patient.getContactNo());
                    patient.setEmergencyNo(newEmergency);
                    break;
                case 9:
                    patient.setMedicalHistory(patientUI.inputMedicalHistory());
                    break;
                case 10:
                    patient.setAddress(inputValidAddress());
                    break;
                case 11:
                    patient.setEmail(inputValidEmail());
                    break;
                case 0:
                    done = true; // exit update menu
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
            if (!done) {
                GenericDAO.saveToFile(patientMap, patientHashMapFile);
                System.out.println("Field updated successfully.");
            }
        }
    }
    // ------------------  delete patient Module  ------------------ //

    public boolean deletePatient(String id) {
        if (patientMap.contains(id)) {
            patientMap.remove(id);
            GenericDAO.saveToFile(patientMap, patientHashMapFile);
            System.out.println("Patient deleted successfully.");
            return true;
        }
        System.out.println("Patient not found.");
        return false;
    }

    // ------------------  Queue Entry Module  ------------------ //
    // ---------------- Walk-in Queue Entry Validation Module ---------------- //
    public void handleQueueMenu() {
        boolean queueExit = false;
        while (!queueExit) {
            int queueChoice = patientUI.getQueueEntryMenu(); // still UI
            switch (queueChoice) {
                case 1:
                    addQueueEntry();
                    break;
                case 2:
                    viewQueueEntriesReport();
                    break;
                case 3:
                    deleteQueueEntry();
                    break;
                case 0:
                    queueExit = true;
                    break;
                default:
                    patientUI.displayMessage("Invalid choice.");
            }
        }
    }

    // ------------------ View Queue Entries Report Module  ------------------ //
    public void viewQueueEntries() {
        if (patientQueue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }

        System.out.println("\n--- Current Queue Entries ---");
        System.out.printf("%-5s %-15s\n", "No", "Patient ID");
        System.out.println("==============================");

        int index = 1;
        for (int i = 0; i < patientQueue.size(); i++) {
            System.out.printf("%-5d %-15s\n", index++, patientQueue.get(i).getPatientId());
        }
    }
    
    private void viewQueueEntriesReport() {
        String fullReport = patientUI.generateQueueReport(patientQueue, patientMap); // pass queue + map
        patientUI.displayQueueReport(fullReport);

        while (true) {
            System.out.print("Do you want to download this queue report as PNG? (Y/N): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                System.out.println("Downloading report...");
                patientUI.exportQueueReportAsPNG(fullReport, new Dimension(1200, 800));
                break;
            } else if (input.equals("n") || input.equals("no")) {
                System.out.println("Returning to previous menu...");
                break;
            } else {
                System.out.println("Invalid input. Please enter Y/Yes or N/No.");
            }
        }
    }

    // ------------------ add Queue Entries Module  ------------------ //
    public void addQueueEntry() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Patient ID to add to queue (Format: YYCCCXXXX): ");
        String id = scanner.nextLine().trim().toUpperCase();

        // Validate format: 2 digits + 3 letters + 4 digits
        if (!id.matches("\\d{2}[A-Z]{3}\\d{4}")) {
            System.out.println("Invalid Patient ID format. Must be YYCCCXXXX.");
            return;
        }

        // Optional: check if patient exists in patientMap
        if (!patientMap.contains(id)) {
            System.out.println("No patient found with this ID.");
            return;
        }

        patientQueue.enqueue(patientMap.get(id));
        GenericDAO.saveToFile(patientQueue, patientQueueFile);
        System.out.println("Patient " + id + " added to queue.");
    }

    // ---------------- Delete Queue Entry Module ---------------- //

    public void deleteQueueEntry() {
        if (patientQueue.isEmpty()) {
            System.out.println("The queue is empty. Nothing to delete.");
            return;
        }

        boolean invalid;
        do {
            invalid = false;
            viewQueueEntries();
            System.out.println("\nPatient at front of the queue: " + patientQueue.peek().getPatientId());

            System.out.print("\nConfirm to dequeue this patient? (Y/N): ");
            char choice = scanner.nextLine().charAt(0);

            switch (choice) {
                case 'Y':
                case 'y':
                    Patient removedPatient = patientQueue.dequeue();
                    GenericDAO.saveToFile(patientQueue, patientQueueFile);
                    System.out.println("Patient " + removedPatient.getPatientId() + " removed from queue.");
                    break;
                case 'N':
                case 'n':
                    System.out.println("Removal cancelled.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    invalid = true;
            }
        } while (invalid);
    }

// ------------------ Payment Menu Valiidate Module ------------------ //
public void handlePaymentMenu() {
    int choice;
    do {
        choice = patientUI.getPaymentMenu();
        switch (choice) {
            case 1:// Generate Receipt
                generateReceipt();
                break;
            case 2:
                viewPaymentHistory();
                break;
            case 3:
                cancelPayment();
                break;
            case 0:
                System.out.println("Returning to previous menu...");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    } while (choice != 0);
}

  // ---------------- Generate Receipt Module ---------------- //


private void generateReceipt() {
    Scanner sc = new Scanner(System.in);
    String patientId;

    // Validate patient ID
    System.out.print("Enter Patient ID: ");
    patientId = sc.nextLine().trim().toUpperCase();
    if (!patientMap.contains(patientId)) {
        System.out.println("Patient not found. Please enter a valid Patient ID.");
        return;
    }

    // Choose payment method
    int methodChoice = 0;
    while (methodChoice < 1 || methodChoice > 3) {
        System.out.println("Select Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. Touch 'n Go");
        System.out.print("Enter choice (1-3): ");
        try {
            methodChoice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            methodChoice = 0;
        }
        if (methodChoice < 1 || methodChoice > 3) System.out.println("Invalid choice. Try again.");
    }

    String method = (methodChoice == 1) ? "Cash" : (methodChoice == 2) ? "Credit Card" : "Touch 'n Go";
    double fee = 1.0; // Fee fixed as RM1

    // Generate payment ID
    String paymentId = String.format("PAY%04d", paymentCounter++);

    //  Date & time auto-generated in Payment
    Payment payment = new Payment(paymentId, patientId, method, fee);
    paymentMap.add(paymentId, payment);

    System.out.println("\n--- Payment Receipt ---");
    System.out.println(payment);
}

  // ---------------- View Payment History Report Module ---------------- //
public void viewPaymentHistory() {
    // Generate report as string (even if empty)
    String fullReport = patientUI.generatePaymentHistoryReport(paymentMap, patientMap);

    // Display report via PatientManagement UI
    patientUI.displayPaymentHistoryReport(fullReport);

    // Ask user if they want to download
    while (true) {
        System.out.print("\nDo you want to download this report as PNG? (Y/N): ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("y") || input.equals("yes")) {
            System.out.println("Downloading report...");
            patientUI.exportPaymentHistoryAsPNG(fullReport, new Dimension(1200, 800));
            break;
        } else if (input.equals("n") || input.equals("no")) {
            System.out.println("Returning to previous menu...");
            break;
        } else {
            System.out.println("Invalid input. Please enter Y/Yes or N/No.");
        }
    }
}

  // ---------------- Cancel Payment Module  ---------------- //
private void cancelPayment() {
    System.out.print("Enter Payment ID to remove: ");
    String paymentId = new java.util.Scanner(System.in).nextLine().trim();
    if (paymentMap.contains(paymentId)) {
        paymentMap.remove(paymentId);
        System.out.println("Payment " + paymentId + " has been removed.");
    } else {
        System.out.println("Payment ID not found.");
    }
}
}
