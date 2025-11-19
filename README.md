# Hospital Patient Management System

A comprehensive desktop application for managing hospital operations including patient records, appointments, diagnoses, and internal staff communication.

## Features

### Core Functionality
- **Patient Management**: Register, update, and search patient records
- **Appointment Scheduling**: Schedule and manage patient appointments
- **Diagnosis Management**: Record patient diagnoses and prescriptions
- **Doctor Management**: Manage doctor profiles and specializations
- **User Authentication**: Secure login system with role-based access

### Advanced Features
- **Internal Messaging**: Real-time messaging between hospital staff using socket programming
- **File Transfer**: Share medical files and documents between staff members
- **Client-Server Architecture**: Dedicated server for handling messaging and file transfers
- **Database Integration**: MySQL database for persistent data storage

## Technologies Used

- **Core Java**: Main application logic
- **Java Swing/AWT**: GUI components and user interface
- **MySQL**: Database management
- **Socket Programming**: Real-time communication (messaging & file transfer)
- **JDBC**: Database connectivity
- **NetBeans**: IDE project configuration

## Project Structure

```
Hospital-Management-System/
├── src/
│   └── com/
│       └── hospital/
│           ├── client/          # Socket client implementation
│           ├── database/        # Database connection and DAO classes
│           ├── gui/             # Swing GUI components
│           ├── model/           # POJO classes
│           ├── server/          # Socket server implementation
│           └── utils/           # Utility classes
├── resources/
│   └── sql/                     # Database schema and scripts
├── lib/                         # External libraries
├── nbproject/                   # NetBeans configuration
├── build.xml                    # Ant build file
├── compile.sh                   # Compilation script
├── run-client.sh               # Client launcher
└── run-server.sh               # Server launcher
```

## Database Schema

The system uses the following main tables:
- `users` - Staff/user accounts
- `patients` - Patient records
- `doctors` - Doctor information
- `departments` - Hospital departments
- `appointments` - Appointment scheduling
- `diagnoses` - Medical diagnoses
- `prescriptions` - Prescription records
- `messages` - Internal messaging
- `file_transfers` - File transfer logs
- `billing` - Billing information

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- MySQL Connector/J 8.0.28 (included in lib/)

### Database Setup

1. Start MySQL server
2. Run the database schema script:
```bash
mysql -u root -p < resources/sql/hospital_schema.sql
```

3. Update database credentials in `src/com/hospital/database/DatabaseConnection.java`:
```java
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

### Compilation

#### Using Shell Script (Linux/Mac):
```bash
./compile.sh
```

#### Using Ant:
```bash
ant compile
ant jar
```

#### Manual Compilation:
```bash
mkdir -p build/classes
javac -d build/classes -cp "lib/*" $(find src -name "*.java")
```

## Running the Application

### Method 1: Using Shell Scripts

1. **Start the Server** (for messaging and file transfer):
```bash
./run-server.sh
```

2. **Start the Client** (in a new terminal):
```bash
./run-client.sh
```

### Method 2: Using Java Commands

1. **Start the Server**:
```bash
java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.server.HospitalServer
```

2. **Start the Client**:
```bash
java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.gui.LoginFrame
```

### Method 3: Using NetBeans

1. Open the project in NetBeans
2. Click "Run Project" or press F6

## Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| dr.smith | doctor123 | DOCTOR |
| dr.jones | doctor123 | DOCTOR |
| nurse.mary | nurse123 | NURSE |
| reception | reception123 | RECEPTIONIST |

## User Guide

### 1. Login
- Launch the application
- Enter username and password
- Click "Login"

### 2. Patient Management
- Click "Patient Management" from the sidebar
- **Add Patient**: Click "Add New Patient" button
- **Search**: Enter name or phone number
- **View Details**: Select patient and click "View Details"

### 3. Appointments
- Click "Appointments" from the sidebar
- **Schedule**: Click "Schedule Appointment"
- Select patient, doctor, date, and time
- **Manage**: Mark appointments as completed or cancelled

### 4. Diagnoses
- Click "Diagnoses" from the sidebar
- **Add Diagnosis**: Click "Add Diagnosis"
- Enter symptoms, diagnosis, and treatment notes
- Set follow-up dates if needed

### 5. Internal Messaging
- Click "Messages" from the sidebar
- **Compose**: Click "Compose Message"
- Select recipient and write message
- Messages are delivered in real-time if server is running

### 6. File Transfer
- Click "File Transfer" from the sidebar
- Select recipient
- Choose file to send (max 5MB)
- Click "Send File"

## Architecture

### Client-Server Communication
The system uses TCP sockets for real-time communication:

**Server** (Port 8888):
- Handles multiple client connections
- Routes messages between users
- Manages file transfers

**Client**:
- Connects to server on startup
- Sends/receives messages
- Handles file transfers

### Protocol Format
```
REGISTER|<user_id>
MESSAGE|<receiver_id>|<message_content>
FILE|<receiver_id>,<filename>|<base64_encoded_data>
```

## Key Features Explained

### 1. Data-Driven Diagnosis Management
- Complete medical history tracking
- Symptom and diagnosis recording
- Prescription management
- Follow-up scheduling

### 2. Internal Communication
- Real-time messaging between staff
- Message read/unread status
- Sent/received message views
- Notification system

### 3. Client-Server Architecture
- Centralized message routing
- Multi-client support
- Connection management
- Error handling

### 4. Custom GUI
- Modern, professional interface
- Color-coded components
- Responsive design
- User-friendly navigation

## Troubleshooting

### Database Connection Issues
- Ensure MySQL server is running
- Verify database credentials in `DatabaseConnection.java`
- Check if database `hospital_management` exists

### Server Connection Issues
- Ensure server is running before starting clients
- Check if port 8888 is available
- Verify firewall settings

### Compilation Errors
- Ensure JDK is properly installed
- Verify MySQL Connector JAR is in lib/ folder
- Check JAVA_HOME environment variable

## Development

### Adding New Features
1. Create model class in `src/com/hospital/model/`
2. Create DAO class in `src/com/hospital/database/`
3. Create GUI panel in `src/com/hospital/gui/`
4. Add menu item in `MainDashboard.java`

### Database Changes
1. Update `resources/sql/hospital_schema.sql`
2. Create migration scripts if needed
3. Update corresponding model and DAO classes

## Future Enhancements

- Laboratory test management
- Pharmacy integration
- Billing and invoicing
- Report generation (PDF)
- Email notifications
- Backup and restore functionality
- Multi-language support
- Mobile app integration

## License

This project is created for educational purposes.

## Contact

For questions or support, please contact the development team.

---

**Note**: This is a demonstration project. For production use, implement additional security measures including:
- Password encryption
- SQL injection prevention
- Input validation
- SSL/TLS for socket communication
- Session management
- Audit logging
