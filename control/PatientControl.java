package control;

import java.util.LinkedList;////need to change, cannot use java built in adt
import java.util.Queue; ///same here
import java.util.Scanner;
import Entity.Patient;
import boundary.PatientUI;
import ADT.MapInterface;
import ADT.HashMap;
import ADT.MapEntry;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import Entity.Appointment;
import Entity.Payment;
import boundary.MedicalCard;
import java.time.format.DateTimeParseException;



public class PatientControl {
    private static MapInterface<String, Payment> paymentMap = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private int paymentCounter = 1;
    private static MapInterface<String, Appointment> appointmentMap = new HashMap<>();
    private static MapInterface<String, Patient> patientMap;
    private PatientUI patientUI;
    private static Queue<String> patientQueue; // store Patient IDs in queue
    
    
    
     // Existing constructor
    public PatientControl(PatientUI patientUI) {
        this.patientUI = patientUI;   
        this.patientMap = new HashMap<>();
        this.patientQueue = new LinkedList<>();

   }   

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
    
    // ------------------ Register New Patient ------------------ //
public void registerNewPatient() {
    System.out.println("\n--- Register New Patient ---");

    // Collect patient info
    String name = inputValidPatientName();
    String ic = inputValidIC();
    String gender = inputValidGender();
    int age = inputValidAge();
    String bloodType = inputValidBloodType();
    String dob = inputValidDateOfBirth(age);
    String contactNo = inputValidContactNo();
    String emergencyNo = inputValidEmergencyNo(contactNo);
    String medicalHistory = patientUI.inputMedicalHistory();
    String address = inputValidAddress();
    String email = inputValidEmail();
    String course = inputValidCourse();

    // Generate Patient ID automatically
    String id = generatePatientId(dob, course);

    // Create patient object
    Patient patient = new Patient(id, name, ic, gender, age, bloodType, dob, contactNo,
                                  emergencyNo, medicalHistory, address, email, course);

    // Store in patient map
    Scanner scanner = new Scanner(System.in);
    patientMap.add(id, patient);
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

    // ------------------ Registration form validation methods ------------------ //
private String inputValidPatientName() {
    String name;
    int attempts = 0; // track invalid attempts

    while (true) {
        name = patientUI.inputPatientName();

        if (name.matches("^[a-zA-Z\\s]{1,50}$")) {
            return name; //  valid name, return it
        } else {
            System.out.println("Invalid Name. Only letters & spaces allowed (max 50).");
            attempts++;

            if (attempts >= 5) {
                System.out.println("\nToo many invalid attempts! Returning to Patient Management Menu...");
                patientUI.getPatientManagementMenu(); // call menu again
                return null; // stop input, return to menu
            }
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

        if (gender.matches("(?i)^(M|F)$")) {
            break; // valid input
        } else {
            attempts++;
            System.out.println("Invalid Gender. Must be M or F.");

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
 
  public void listAllPatients() {
    StringBuilder sb = new StringBuilder();

    // Table Header (Patient ID first)
    sb.append(String.format("%-15s %-20s %-15s %-8s %-5s %-10s %-15s %-15s %-25s %-20s %-25s\n",
            "Patient ID", "Patient Name", "IC No", "Gender", "Age", "BloodType",
            "Date Of Birth", "Contact No", "Emergency No", "Medical History", "Address", "Email"));
    sb.append("=".repeat(200)).append("\n"); // separator line (slightly longer)

    MapEntry<String, Patient>[] entries = patientMap.getTable();
    boolean found = false;

    for (MapEntry<String, Patient> e : entries) {
        if (e != null && !e.isRemoved()) {
            Patient p = e.getValue();

            // Patient ID shown first
            sb.append(String.format("%-15s %-20s %-15s %-8s %-5d %-10s %-15s %-15s %-25s %-20s %-25s\n",
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
        }
    }

    if (!found) {
        sb.append("No patient records found.\n");
    }

    // Send to UI for display
    patientUI.listAllPatients(sb.toString());
}
 
    public Patient searchPatient(String id) {
        Patient p = patientMap.get(id);
        if (p != null) patientUI.printPatientDetails(p);
        else System.out.println("No patient found with ID: " + id);
        return p;
    }
    
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
        if (!done) System.out.println("Field updated successfully.");
    }
}


    public boolean deletePatient(String id) {
        if (patientMap.contains(id)) {
            patientMap.remove(id);
            System.out.println("Patient deleted successfully.");
            return true;
        }
        System.out.println("Patient not found.");
        return false;
    }
    // ------------------  Queue Entry Module  ------------------ //

    
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

    patientQueue.add(id);
    System.out.println("Patient " + id + " added to queue.");
}


public void viewQueueEntries() {
    if (patientQueue.isEmpty()) {
        System.out.println("The queue is empty.");
        return;
    }

    System.out.println("\n--- Current Queue Entries ---");
    System.out.printf("%-5s %-15s\n", "No", "Patient ID");
    System.out.println("==============================");

    int i = 1;
    for (String id : patientQueue) {
        System.out.printf("%-5d %-15s\n", i, id);
        i++;
    }
}


public void deleteQueueEntry() {
    if (patientQueue.isEmpty()) {
        System.out.println("The queue is empty. Nothing to delete.");
        return;
    }
    System.out.print("Enter Patient ID to remove from queue: ");
    String id = new Scanner(System.in).nextLine().trim();
    if (patientQueue.remove(id)) {
        System.out.println("Patient " + id + " removed from queue.");
    } else {
        System.out.println("Patient ID not found in queue.");
    }
}


// ------------------ Appointment Menu methods ------------------ //
public void handleAppointmentMenu() {
    int choice;
    do {
        choice = patientUI.getAppointmentMenu();
        switch (choice) {
            case 1: // Schedule new appointment
                String consultationType = inputConsultationType();
                String patientIdOrName = inputPatientIdOrName();
                String reason = inputReason();
                String date = inputDate();
                String time = inputTime();

                // Save appointment
                Appointment appointment = new Appointment(patientIdOrName, consultationType, reason, date, time);
                appointmentMap.add(patientIdOrName, appointment);

                System.out.println("\n Appointment Scheduled Successfully:");
                System.out.println(appointment);
                break;
                
            case 2: // View appointment list
                  viewAppointments();
                break;
                
            case 3: // Search a patient appointment
                  System.out.print("Enter Patient ID to search: ");
                  String searchId = scanner.nextLine().trim();
                  MapEntry<String, Appointment>[] entries = appointmentMap.getTable();
                  boolean found = false;

                  System.out.println("\n--- Appointment Record for Patient: " + searchId + " ---");
                  System.out.printf("%-15s %-35s %-20s %-12s %-8s\n",
                  "Patient ID", "Consultation Type", "Reason", "Date", "Time");
                   System.out.println("-------------------------------------------------------------------------------------------");

                for (MapEntry<String, Appointment> e : entries) {
                     if (e != null && !e.isRemoved()) {
                     Appointment appt = e.getValue();
                         if (appt.getPatientId().equalsIgnoreCase(searchId)) {
                         System.out.printf("%-15s %-35s %-20s %-12s %-8s\n",
                         appt.getPatientId(),
                         appt.getConsultationType(),
                         appt.getReason(),
                         appt.getDate(),
                         appt.getTime());
                         found = true;
            }
        }
    }

    if (!found) {
        System.out.println("No appointment found for Patient ID: " + searchId);
    }
    break;

            case 4: // Cancel appointment
                System.out.print("Enter Patient ID to cancel appointment: ");
                String cancelId = new Scanner(System.in).nextLine().trim();
                if (appointmentMap.contains(cancelId)) {
                    appointmentMap.remove(cancelId);
                    System.out.println("Appointment for Patient " + cancelId + " has been canceled.");
                } else {
                    System.out.println("No appointment found for Patient ID: " + cancelId);
                }
                break;

            case 0:
                System.out.println("Returning to previous menu...");
                break;

            default:
                System.out.println("Invalid choice.");
        }
    } while (choice != 0);
}




// ------------------ Appointment-related methods ------------------ //

// Input consultation type from predefined list
public String inputConsultationType() {
    String[] consultations = {
        "General Practitioner (GP) Consultation",
        "Pediatric Consultation",
        "Obstetrics & Gynecology (O&G) Consultation",
        "Dermatology Consultation",
        "ENT (Ear, Nose, Throat) Consultation",
        "Dental Consultation",
        "Ophthalmology Consultation",
        "Orthopedic Consultation",
        "Cardiology Consultation",
        "Neurology Consultation",
        "Endocrinology Consultation",
        "Psychiatry / Mental Health Consultation",
        "Nutrition & Diet Consultation",
        "Physiotherapy / Rehabilitation Consultation",
        "Specialist Clinics"
    };

    System.out.println("\nSelect Consultation Type:");
    for (int i = 0; i < consultations.length; i++) {
        System.out.println((i + 1) + ". " + consultations[i]);
    }

    int choice = -1;
    while (choice < 1 || choice > consultations.length) {
        System.out.print("Enter choice (1-" + consultations.length + "): ");
        try {
            choice = Integer.parseInt(new Scanner(System.in).nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter digits only.");
        }
    }

    return consultations[choice - 1];
}

// Input Patient ID or Name
// Input Patient ID or Name with retry limit
public String inputPatientIdOrName() {
    String input;
    int attempts = 0;
    final int MAX_ATTEMPTS = 5;

    while (true) {
        System.out.print("Enter Patient ID or Name: ");
        input = new Scanner(System.in).nextLine().trim();
        boolean found = false;

        // Check if ID or name exists in patientMap
        MapEntry<String, Patient>[] entries = patientMap.getTable();
        for (MapEntry<String, Patient> e : entries) {
            if (e != null && !e.isRemoved()) {
                Patient p = e.getValue();
                if (p.getPatientId().equalsIgnoreCase(input) || 
                    p.getName().equalsIgnoreCase(input)) {
                    found = true;
                    break;
                }
            }
        }

        if (found) {
            return input; //  Valid input found
        } else {
            attempts++;
            System.out.println("Patient not found. Please enter a valid registered patient.");
            if (attempts >= MAX_ATTEMPTS) {
                System.out.println("\nToo many invalid attempts. Returning to Appointment Menu...");
                return null; //  Indicate failure (go back to Appointment Menu)
            }
        }
    }
}


// Input reason
public String inputReason() {
    System.out.print("Enter reason for appointment: ");
    return new Scanner(System.in).nextLine().trim();
}

// Input date (digits only)
// Input date in format YYYY-MM-DD (must be future date)
public static String inputDate() {
        Scanner sc = new Scanner(System.in);
        String inputDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (true) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            inputDate = sc.nextLine().trim();

            try {
                // Try to parse
                LocalDate date = LocalDate.parse(inputDate, formatter);

                // Must be in the future
                if (date.isAfter(LocalDate.now())) {
                    return inputDate; // valid
                } else {
                    System.out.println("Date must be in the future. Try again.");
                }

            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please enter in YYYY-MM-DD format.");
            }
        }
    }

    public static void main(String[] args) {
        String date = inputDate();
        System.out.println("Valid future date entered: " + date);
    }


// Input time (HH:MM, 24-hour format, allowed 09:00–21:59)
public String inputTime() {
    Scanner sc = new Scanner(System.in);
    String time;

    while (true) {
        System.out.print("Enter time (HH:MM, 24-hour format, allowed 09:00 to 21:59): ");
        time = sc.nextLine().trim();

        // Must match HH:MM format
        if (!time.matches("\\d{2}:\\d{2}")) {
            System.out.println("Invalid format. Enter as HH:MM (e.g., 09:00).");
            continue;
        }

        // Extract hour and minute
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));

        // Validate hour
        if (hour < 9 || hour > 21) {
            System.out.println("Invalid hour. Must be between 09 and 21.");
            continue;
        }

        // Validate minutes
        if (minute < 0 || minute > 59) {
            System.out.println("Invalid minutes. Must be between 00 and 59.");
            continue;
        }

        return time; // valid input in HH:MM format
    }
}



public void viewAppointments() {
    MapEntry<String, Appointment>[] entries = appointmentMap.getTable();
    boolean found = false;

    System.out.println("\n--- Appointment List ---");
    System.out.printf("%-15s %-35s %-20s %-12s %-8s\n",
            "Patient ID", "Consultation Type", "Reason", "Date", "Time");
    System.out.println("-------------------------------------------------------------------------------------------");

    for (MapEntry<String, Appointment> e : entries) {
        if (e != null && !e.isRemoved()) {
            Appointment appt = e.getValue();
            System.out.printf("%-15s %-35s %-20s %-12s %-8s\n",
                    appt.getPatientId(),
                    appt.getConsultationType(),
                    appt.getReason(),
                    appt.getDate(),
                    appt.getTime());
            found = true;
        }
    }

    if (!found) {
        System.out.println("No appointments scheduled.");
    }
}


// ------------------ Payment Menu Logic ------------------ //
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


private void viewPaymentHistory() {
    boolean found = false;
    MapEntry<String, Payment>[] entries = paymentMap.getTable();
    for (MapEntry<String, Payment> e : entries) {
        if (e != null && !e.isRemoved()) {
            Payment p = e.getValue();
            System.out.println("------------------------");
            System.out.println("Patient ID: " + p.getPatientId());
            System.out.println("Payment ID: " + p.getPaymentId());
            System.out.println("Payment Method: " + p.getPaymentMethod());
            System.out.println("Date: " + p.getDate());
            System.out.println("Time: " + p.getTime()); 
            System.out.println("Amount: RM " + p.getAmount());
            found = true;
        }
    }
    if (!found) {
        System.out.println("No payment records found.");
    }
}

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
// ------------------ Input Patient ID with Validation ------------------ //
public String inputPatientId() {
    String patientId;
    while (true) {
        System.out.print("Enter Patient ID: ");
        patientId = scanner.nextLine().trim().toUpperCase();

        // Validate ID exists in patientMap
        if (patientMap.contains(patientId)) break;
        System.out.println("Patient not found. Please enter a valid Patient ID.");
    }
    return patientId;
}

// ------------------ Input Payment Method with Validation ------------------ //
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

// ------------------ Display Payment Message ------------------ //
public void displayMessage(String msg) {
    System.out.println(msg);
}

// ------------------ Display Payment Receipt ------------------ //
public void displayPaymentReceipt(String receipt) {
    System.out.println("\n--- Payment Receipt ---");
    System.out.println(receipt);
}

}


    
