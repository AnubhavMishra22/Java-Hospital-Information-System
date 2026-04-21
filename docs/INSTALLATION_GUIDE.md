# Hospital Management System - Installation Guide

## Step-by-Step Installation

### Step 1: Install Prerequisites

#### 1.1 Install Java Development Kit (JDK)

**Windows:**
1. Download JDK 8 or higher from Oracle or OpenJDK
2. Run the installer
3. Set JAVA_HOME environment variable
4. Add `%JAVA_HOME%\bin` to PATH

**Linux:**
```bash
sudo apt-get update
sudo apt-get install openjdk-8-jdk
```

**Mac:**
```bash
brew install openjdk@8
```

Verify installation:
```bash
java -version
javac -version
```

#### 1.2 Install MySQL Server

**Windows:**
1. Download MySQL Installer from mysql.com
2. Run installer and choose "Server only" or "Full"
3. Set root password during installation
4. Start MySQL service

**Linux:**
```bash
sudo apt-get update
sudo apt-get install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

**Mac:**
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

Verify installation:
```bash
mysql --version
```

### Step 2: Download Project

#### Option A: Clone from Git
```bash
git clone <repository-url>
cd Java-Hospital-Information-System
```

#### Option B: Download ZIP
1. Download project ZIP file
2. Extract to desired location
3. Navigate to project directory

### Step 3: Download MySQL Connector

1. Download MySQL Connector/J 8.0.28 from:
   https://dev.mysql.com/downloads/connector/j/

2. Extract the downloaded file

3. Copy `mysql-connector-java-8.0.28.jar` to project's `lib/` folder:
```bash
mkdir -p lib
cp /path/to/mysql-connector-java-8.0.28.jar lib/
```

### Step 4: Setup Database

#### 4.1 Start MySQL
```bash
# Linux/Mac
sudo systemctl start mysql
# or
sudo service mysql start

# Windows - Use Services or MySQL Workbench
```

#### 4.2 Login to MySQL
```bash
mysql -u root -p
# Enter your MySQL root password
```

#### 4.3 Create Database and Import Schema

**Option A: From MySQL command line**
```sql
mysql> source /path/to/Java-Hospital-Information-System/resources/sql/hospital_schema.sql
```

**Option B: From terminal**
```bash
mysql -u root -p < resources/sql/hospital_schema.sql
```

#### 4.4 Verify Database Creation
```bash
mysql -u root -p
```
```sql
mysql> SHOW DATABASES;
mysql> USE hospital_management;
mysql> SHOW TABLES;
mysql> SELECT * FROM users;
mysql> exit;
```

### Step 5: Configure Database Connection

Edit `src/com/hospital/database/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_management";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "YOUR_MYSQL_PASSWORD"; // Change this!
```

### Step 6: Compile the Project

#### Option A: Using Shell Script (Linux/Mac)
```bash
chmod +x dev/compile.sh dev/run-server.sh dev/run-client.sh dev/run-tests.sh
./dev/compile.sh
```

#### Option B: Using Windows Batch (Create compile.bat)
```batch
@echo off
mkdir build\classes
mkdir dist
javac -d build/classes -cp "lib/*" src/com/hospital/*/*.java src/com/hospital/*/*/*.java
cd build/classes
jar cvfm ..\..\dist\HospitalManagementSystem.jar ..\..\manifest.txt com\
cd ..\..
echo Build completed!
```

#### Option C: Using Ant
```bash
ant clean
ant compile
ant jar
```

#### Option D: Manual Compilation
```bash
# Create directories
mkdir -p build/classes
mkdir -p dist

# Compile all Java files
find src -name "*.java" > sources.txt
javac -d build/classes -cp "lib/*" @sources.txt

# Create JAR
cd build/classes
jar cvfm ../../dist/HospitalManagementSystem.jar ../../manifest.txt com/
cd ../..
```

### Step 7: Run the Application

#### 7.1 Start the Server (Terminal 1)
```bash
# Linux/Mac
./dev/run-server.sh

# Windows
java -cp "dist/HospitalManagementSystem.jar;lib/*" com.hospital.server.HospitalServer

# Manual
java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.server.HospitalServer
```

You should see:
```
===========================================
Hospital Management System Server
===========================================
Server starting on port 8888...
Server started successfully!
Waiting for client connections...
```

#### 7.2 Start the Client (Terminal 2)
```bash
# Linux/Mac
./dev/run-client.sh

# Windows
java -cp "dist/HospitalManagementSystem.jar;lib/*" com.hospital.gui.LoginFrame

# Manual
java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.gui.LoginFrame
```

### Step 8: Login and Test

1. The login window should appear
2. Use default credentials:
   - Username: `admin`
   - Password: `admin123`
3. Click "Login"

### Step 9: Open in NetBeans (Optional)

1. Open NetBeans IDE
2. File → Open Project
3. Navigate to project folder
4. Select the project
5. Click "Open Project"
6. Right-click project → Properties → Libraries
7. Ensure MySQL Connector is in classpath
8. Press F6 to run

## Common Issues and Solutions

### Issue 1: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Solution:**
- Ensure `mysql-connector-java-8.0.28.jar` is in lib/ folder
- Check classpath includes lib/* when running

### Issue 2: "Access denied for user 'root'@'localhost'"
**Solution:**
- Update password in DatabaseConnection.java
- Or create a new MySQL user:
```sql
CREATE USER 'hospital'@'localhost' IDENTIFIED BY 'hospital123';
GRANT ALL PRIVILEGES ON hospital_management.* TO 'hospital'@'localhost';
FLUSH PRIVILEGES;
```

### Issue 3: "Communications link failure"
**Solution:**
- Ensure MySQL server is running
- Check MySQL is running on port 3306
- Verify firewall settings

### Issue 4: "Database 'hospital_management' doesn't exist"
**Solution:**
- Re-run the schema SQL script
- Check for errors during database creation

### Issue 5: "Port 8888 already in use"
**Solution:**
- Kill process using port 8888
- Or change port in HospitalServer.java

### Issue 6: Compilation errors
**Solution:**
- Verify JDK installation: `javac -version`
- Ensure JAVA_HOME is set
- Check all .java files are in correct packages

## Testing the Features

### Test Patient Management
1. Login → Patient Management
2. Click "Add New Patient"
3. Fill in details, click Save
4. Search for the patient
5. View patient details

### Test Appointments
1. Login → Appointments
2. Click "Schedule Appointment"
3. Select patient, doctor, date, time
4. Click Schedule
5. View in appointments list

### Test Messaging
1. Open two client windows (login with different users)
2. In first client: Messages → Compose Message
3. Select second user as recipient
4. Send message
5. Check second client for received message

### Test File Transfer
1. Ensure server is running
2. Messages → File Transfer
3. Select recipient
4. Browse and select a file
5. Click Send File
6. Check recipient's client

## Uninstallation

1. Stop all running instances (server and clients)
2. Drop database:
```sql
mysql -u root -p
DROP DATABASE hospital_management;
exit;
```
3. Delete project folder
4. Remove MySQL Connector from lib/ if not needed elsewhere

## Next Steps

After successful installation:
1. Read the User Guide in README.md
2. Explore all features
3. Create test data
4. Customize for your needs
5. Implement additional security measures for production use

## Support

If you encounter issues not covered here:
1. Check the README.md file
2. Review error messages carefully
3. Check MySQL and Java logs
4. Verify all prerequisites are installed correctly

---

**Important Security Notes:**
- Change default passwords before production use
- Implement password encryption
- Use SSL for database connections
- Secure socket communication with encryption
- Regular database backups
