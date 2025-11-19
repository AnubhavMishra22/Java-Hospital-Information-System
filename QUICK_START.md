# Hospital Management System - Quick Start Guide

## Prerequisites Checklist
- [ ] Java JDK 8+ installed
- [ ] MySQL Server installed and running
- [ ] MySQL Connector/J 8.0.28 in `lib/` folder

## 5-Minute Setup

### Step 1: Setup Database (2 minutes)
```bash
# Start MySQL
sudo systemctl start mysql  # Linux
# or use MySQL Workbench on Windows

# Import database
mysql -u root -p < resources/sql/hospital_schema.sql
```

### Step 2: Configure Database Connection (1 minute)
Edit `src/com/hospital/database/DatabaseConnection.java`:
```java
private static final String DB_PASSWORD = "YOUR_PASSWORD_HERE";
```

### Step 3: Compile (1 minute)
```bash
chmod +x *.sh
./compile.sh
```

### Step 4: Run (1 minute)
```bash
# Terminal 1 - Start Server
./run-server.sh

# Terminal 2 - Start Client
./run-client.sh
```

### Step 5: Login
```
Username: admin
Password: admin123
```

## Quick Navigation

### Patient Management
1. Login
2. Click "Patient Management"
3. Click "Add New Patient"
4. Fill form → Save
5. Search patients by name/phone

### Schedule Appointment
1. Click "Appointments"
2. Click "Schedule Appointment"
3. Select patient & doctor
4. Enter date (YYYY-MM-DD) and time (HH:MM:SS)
5. Click "Schedule"

### Add Diagnosis
1. Click "Diagnoses"
2. Click "Add Diagnosis"
3. Enter appointment ID
4. Select patient & doctor
5. Enter symptoms and diagnosis
6. Click "Save"

### Send Message
1. Click "Messages"
2. Click "Compose Message"
3. Select recipient
4. Type subject and message
5. Click "Send"

### Transfer File
1. Click "File Transfer"
2. Select recipient
3. Click "Browse" and select file
4. Click "Send File"

## Common Commands

### Compile Only
```bash
./compile.sh
```

### Run Server Only
```bash
./run-server.sh
# or
java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.server.HospitalServer
```

### Run Client Only
```bash
./run-client.sh
# or
java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.gui.LoginFrame
```

### Clean Build
```bash
rm -rf build/ dist/
./compile.sh
```

## Troubleshooting Quick Fixes

### "Database connection failed"
```bash
# Check MySQL is running
sudo systemctl status mysql

# Verify password in DatabaseConnection.java
# Ensure database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'hospital_management';"
```

### "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
```bash
# Download MySQL Connector
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.28.tar.gz
tar -xzf mysql-connector-java-8.0.28.tar.gz
cp mysql-connector-java-8.0.28/mysql-connector-java-8.0.28.jar lib/
```

### "Port 8888 already in use"
```bash
# Find and kill process
lsof -i :8888
kill -9 <PID>
```

### Compilation errors
```bash
# Verify Java version
java -version
javac -version

# Check JAVA_HOME
echo $JAVA_HOME
```

## Default User Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Doctor | dr.smith | doctor123 |
| Doctor | dr.jones | doctor123 |
| Nurse | nurse.mary | nurse123 |
| Receptionist | reception | reception123 |

## Project Structure
```
Java-Hospital-Information-System/
├── src/com/hospital/
│   ├── client/      → Socket client
│   ├── database/    → DAO & DB connection
│   ├── gui/         → Swing panels
│   ├── model/       → POJO classes
│   └── server/      → Socket server
├── resources/sql/   → Database schema
├── lib/            → MySQL connector
├── compile.sh      → Build script
├── run-server.sh   → Server launcher
└── run-client.sh   → Client launcher
```

## Key Features Access

| Feature | Menu Item | Description |
|---------|-----------|-------------|
| Patient Records | Patient Management | Add, search, view patients |
| Appointments | Appointments | Schedule & manage |
| Medical Records | Diagnoses | Record diagnoses |
| Chat | Messages | Staff messaging |
| File Share | File Transfer | Share files |

## Database Tables

- `users` - Staff accounts
- `patients` - Patient records
- `doctors` - Doctor info
- `departments` - Hospital departments
- `appointments` - Appointments
- `diagnoses` - Medical diagnoses
- `prescriptions` - Prescriptions
- `messages` - Internal messages
- `file_transfers` - File transfers
- `billing` - Billing records

## Server Information

- Protocol: TCP
- Port: 8888
- Max File Size: 5MB
- Encoding: Base64 for files

## Tips

1. Always start the server before clients
2. Each client connects automatically on login
3. Messages work only when server is running
4. Database must be running for any operation
5. Check logs if something fails

## Getting Help

1. Check `README.md` for detailed info
2. See `INSTALLATION_GUIDE.md` for setup help
3. Read `FEATURES.md` for feature details
4. Review code comments for implementation details

## Next Steps

After setup:
1. Create test patients
2. Schedule some appointments
3. Add diagnoses
4. Test messaging between users
5. Try file transfer
6. Explore all features

## Security Notes

For production use:
- Change all default passwords
- Enable password encryption
- Use SSL for database connection
- Implement SSL/TLS for sockets
- Add input sanitization
- Enable audit logging
- Set up regular backups

---

**Ready to use!** Open two terminals, run server and client, and start managing hospital operations.
