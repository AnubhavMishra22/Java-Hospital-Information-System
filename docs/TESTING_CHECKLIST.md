# Testing Checklist for Hospital Management System

## Before Merging PR: feature/message-dialog-fix

### ✅ Pre-Merge Testing Steps

#### 1. **Compilation Test**
```bash
dev\COMPILE_SIMPLE.bat
```
- [ ] Compilation successful (no errors)
- [ ] JAR file created in `dist/` folder
- [ ] No warnings about missing files

---

#### 2. **Database Connection Test**
Before running, ensure:
- [ ] MySQL server is running
- [ ] Database `hospital_management` exists
- [ ] Username: `root`, Password: `root` (or update DatabaseConnection.java)

---

#### 3. **Application Startup Test**
```bash
RUN_HOSPITAL_SYSTEM.bat
```

**Expected behavior:**
- [ ] Launcher window appears and auto-closes after 5 seconds
- [ ] Server window opens and shows "Server started on port 5000"
- [ ] Client window opens showing login screen
- [ ] No error messages in console

---

#### 4. **Login Test**
- [ ] Can enter username and password
- [ ] Login button works
- [ ] Successful login shows main dashboard
- [ ] Welcome message shows correct user name and role
- [ ] No "erro = null" or connection errors

---

#### 5. **Main Dashboard Test**
- [ ] All menu buttons visible and clickable
- [ ] Button text is readable (not color issues)
- [ ] Patient Management button works
- [ ] Appointments button works
- [ ] Messaging button works
- [ ] File Transfer button works
- [ ] Diagnosis button works

---

#### 6. **Database Operations Test**

**Patient Management:**
- [ ] Can view existing patients
- [ ] Can add new patient
- [ ] Can search for patients
- [ ] No "erro = null" errors

**Appointments:**
- [ ] Can view appointments
- [ ] Can schedule new appointment
- [ ] Can select date and time
- [ ] No "erro = null" errors

**Diagnosis:**
- [ ] Can add diagnosis for a patient
- [ ] Form validation works
- [ ] Can save diagnosis successfully
- [ ] No "erro = null" errors

---

#### 7. **Messaging Panel Test (PR-specific)**

**Compose Message Dialog:**
- [ ] Click "Compose Message" button
- [ ] Dialog opens at size 700x550 (larger than before)
- [ ] Text area has line wrapping enabled
- [ ] Font size is 14 (readable)
- [ ] Can select recipient
- [ ] Can type subject and message
- [ ] Can send message

**View Message Dialog:**
- [ ] Click on a message to view
- [ ] Dialog opens at size 800x600 (larger than before)
- [ ] Message text wraps properly (no horizontal scrolling)
- [ ] Font size is 14 (readable)
- [ ] Can read full message content

---

#### 8. **File Transfer Test**

**File Chooser:**
- [ ] Click "Browse" button
- [ ] File chooser dialog opens
- [ ] **FILES ARE VISIBLE** (this was a bug fix)
- [ ] Can see all file types
- [ ] Can navigate to different folders
- [ ] Can select a file

---

#### 9. **Logout Test**
- [ ] Click "Logout" button
- [ ] Confirmation dialog appears
- [ ] Click "Yes"
- [ ] Returns to login screen
- [ ] Can login again successfully
- [ ] No connection errors after logout

---

#### 10. **Window Close Test**
- [ ] Login to application
- [ ] Do some operations (add patient, etc.)
- [ ] Click the **X button** to close window
- [ ] Window closes **immediately** (no freezing/hanging)
- [ ] Client CMD window auto-closes
- [ ] Server window stays open
- [ ] Can run application again and login

---

#### 11. **Multiple Operations Test**
Perform these operations in sequence:
- [ ] Login
- [ ] Add a patient
- [ ] Schedule an appointment
- [ ] Add a diagnosis
- [ ] Send a message
- [ ] View messages
- [ ] Browse files
- [ ] Logout
- [ ] Login again
- [ ] Verify all data is still there

**Look for:**
- No "erro = null" messages
- No connection timeout errors
- All operations complete successfully

---

#### 12. **Console Output Check**

**Watch for these in server/client console:**

**GOOD Signs:**
- ✅ "Database connected successfully!" (appears once at startup)
- ✅ "Server started on port 5000"
- ✅ "Client connected from..."
- ✅ Clean shutdown messages

**BAD Signs (report these):**
- ❌ "erro = null"
- ❌ "Database connection failed!"
- ❌ Stack traces or exceptions
- ❌ "Connection pool exhausted"
- ❌ Multiple "Database connected successfully!" (means reconnecting too often)

---

#### 13. **CMD Window Behavior Test**

**Using RUN_HOSPITAL_SYSTEM.bat:**
- [ ] Launcher window auto-closes after 5 seconds
- [ ] Server CMD window stays open
- [ ] Client CMD window stays open
- [ ] When closing GUI (X button), client CMD auto-closes
- [ ] Server can be stopped with Ctrl+C
- [ ] After Ctrl+C, server CMD auto-closes

---

## 🎯 Quick Test (Minimum)

If short on time, test these critical items:

1. **Compilation works** ✅
2. **Login works** ✅
3. **Schedule appointment** (no "erro = null") ✅
4. **Add diagnosis** (no "erro = null") ✅
5. **Messaging dialogs are larger** ✅
6. **File chooser shows files** ✅
7. **Logout and login again works** ✅
8. **Window closes without freezing** ✅

---

## 📝 Issues to Report

If you encounter any of these, report before merging:

- [ ] Compilation errors
- [ ] "erro = null" messages
- [ ] Login failures
- [ ] Database connection errors
- [ ] Window freeze/hang on close
- [ ] File chooser showing no files
- [ ] Message dialogs too small
- [ ] Button text not visible
- [ ] Any exceptions in console

---

## ✅ Sign-off

**Tested by:** _______________
**Date:** _______________
**Branch:** feature/message-dialog-fix
**Result:** PASS / FAIL

**Notes:**
_____________________________________________
_____________________________________________
_____________________________________________

---

**If all tests pass, the PR is ready to merge!** 🎉
