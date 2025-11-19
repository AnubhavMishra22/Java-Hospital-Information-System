package com.hospital.client;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for SocketClient
 */
public class SocketClientTest {

    private SocketClient client;

    @Before
    public void setUp() {
        client = new SocketClient();
    }

    @Test
    public void testSocketClientCreation() {
        assertNotNull("SocketClient should not be null", client);
    }

    @Test
    public void testSocketClientClassExists() {
        assertNotNull("SocketClient class should exist", SocketClient.class);
    }

    @Test
    public void testMessageListenerInterfaceExists() {
        Class<?>[] innerClasses = SocketClient.class.getDeclaredClasses();
        boolean hasMessageListener = false;
        for (Class<?> cls : innerClasses) {
            if (cls.getSimpleName().equals("MessageListener")) {
                hasMessageListener = true;
                assertTrue("MessageListener should be an interface", cls.isInterface());
                break;
            }
        }
        assertTrue("MessageListener interface should exist", hasMessageListener);
    }

    @Test
    public void testConnectMethodExists() throws NoSuchMethodException {
        SocketClient.class.getDeclaredMethod("connect", int.class);
    }

    @Test
    public void testDisconnectMethodExists() throws NoSuchMethodException {
        SocketClient.class.getDeclaredMethod("disconnect");
    }

    @Test
    public void testIsConnectedMethodExists() throws NoSuchMethodException {
        SocketClient.class.getDeclaredMethod("isConnected");
    }

    @Test
    public void testSendMessageMethodExists() throws NoSuchMethodException {
        SocketClient.class.getDeclaredMethod("sendMessageToUser", int.class, String.class);
    }

    @Test
    public void testSendFileMethodExists() throws NoSuchMethodException {
        SocketClient.class.getDeclaredMethod("sendFileToUser", int.class, String.class, String.class);
    }

    @Test
    public void testSetMessageListenerMethodExists() throws NoSuchMethodException {
        // Find the MessageListener interface first
        Class<?> listenerClass = null;
        for (Class<?> cls : SocketClient.class.getDeclaredClasses()) {
            if (cls.getSimpleName().equals("MessageListener")) {
                listenerClass = cls;
                break;
            }
        }
        assertNotNull("MessageListener interface should be found", listenerClass);
        SocketClient.class.getDeclaredMethod("setMessageListener", listenerClass);
    }

    @Test
    public void testInitialConnectionState() {
        assertFalse("Client should not be connected initially", client.isConnected());
    }
}
