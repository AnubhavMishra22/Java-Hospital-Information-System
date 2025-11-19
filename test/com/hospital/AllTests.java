package com.hospital;

import com.hospital.model.*;
import com.hospital.database.*;
import com.hospital.server.HospitalServerTest;
import com.hospital.client.SocketClientTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test Suite - Runs all unit tests
 */
@RunWith(Suite.class)
@SuiteClasses({
    // Model Tests
    UserTest.class,
    PatientTest.class,
    DoctorTest.class,
    AppointmentTest.class,
    DiagnosisTest.class,
    PrescriptionTest.class,
    MessageTest.class,

    // DAO Tests
    UserDAOTest.class,
    PatientDAOTest.class,
    AppointmentDAOTest.class,

    // Network Tests
    HospitalServerTest.class,
    SocketClientTest.class
})
public class AllTests {
    // This class remains empty, used only as a holder for the above annotations
}
