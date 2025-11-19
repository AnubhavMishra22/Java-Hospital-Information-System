package com.hospital.server;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for HospitalServer
 */
public class HospitalServerTest {

    @Test
    public void testHospitalServerClassExists() {
        assertNotNull("HospitalServer class should exist", HospitalServer.class);
    }

    @Test
    public void testClientHandlerClassExists() {
        // Test that inner class ClientHandler exists
        Class<?>[] innerClasses = HospitalServer.class.getDeclaredClasses();
        boolean hasClientHandler = false;
        for (Class<?> cls : innerClasses) {
            if (cls.getSimpleName().equals("ClientHandler")) {
                hasClientHandler = true;
                break;
            }
        }
        assertTrue("ClientHandler inner class should exist", hasClientHandler);
    }

    @Test
    public void testServerMethodsExist() throws NoSuchMethodException {
        HospitalServer.class.getDeclaredMethod("sendMessageToUser", int.class, String.class);
        HospitalServer.class.getDeclaredMethod("broadcastMessage", String.class,
            HospitalServer.class.getDeclaredClasses()[0]);
    }
}
