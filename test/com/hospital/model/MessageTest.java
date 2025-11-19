package com.hospital.model;

import com.hospital.utils.TestDataFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Message model class
 */
public class MessageTest {

    private Message message;

    @Before
    public void setUp() {
        message = TestDataFactory.createTestMessage();
    }

    @Test
    public void testMessageCreation() {
        assertNotNull("Message should not be null", message);
        assertEquals("Message ID should be 1", 1, message.getMessageId());
        assertEquals("Sender ID should be 1", 1, message.getSenderId());
        assertEquals("Receiver ID should be 2", 2, message.getReceiverId());
    }

    @Test
    public void testSubjectAndText() {
        assertEquals("Subject should match", "Test Message", message.getSubject());
        assertEquals("Message text should match", "This is a test message",
                    message.getMessageText());

        message.setSubject("New Subject");
        message.setMessageText("New message content");

        assertEquals("Subject should be updated", "New Subject", message.getSubject());
        assertEquals("Message text should be updated", "New message content",
                    message.getMessageText());
    }

    @Test
    public void testReadStatus() {
        assertFalse("Initial read status should be false", message.isReadStatus());

        message.setReadStatus(true);
        assertTrue("Read status should be true", message.isReadStatus());

        message.setReadStatus(false);
        assertFalse("Read status should be false", message.isReadStatus());
    }

    @Test
    public void testExtendedFields() {
        assertEquals("Sender name should match", "Test User", message.getSenderName());
        assertEquals("Receiver name should match", "Admin User", message.getReceiverName());

        message.setSenderName("John Doe");
        message.setReceiverName("Jane Smith");

        assertEquals("Sender name should be updated", "John Doe", message.getSenderName());
        assertEquals("Receiver name should be updated", "Jane Smith", message.getReceiverName());
    }

    @Test
    public void testParameterizedConstructor() {
        Message newMsg = new Message(5, 6, "Important", "Urgent message");

        assertEquals("Sender ID should be 5", 5, newMsg.getSenderId());
        assertEquals("Receiver ID should be 6", 6, newMsg.getReceiverId());
        assertEquals("Subject should match", "Important", newMsg.getSubject());
        assertEquals("Message text should match", "Urgent message", newMsg.getMessageText());
        assertFalse("Read status should default to false", newMsg.isReadStatus());
    }

    @Test
    public void testAllGettersAndSetters() {
        message.setMessageId(999);
        assertEquals("Message ID should be 999", 999, message.getMessageId());

        message.setSenderId(111);
        assertEquals("Sender ID should be 111", 111, message.getSenderId());

        message.setReceiverId(222);
        assertEquals("Receiver ID should be 222", 222, message.getReceiverId());
    }

    @Test
    public void testEmptyMessage() {
        Message emptyMsg = new Message();
        assertNotNull("Empty message should not be null", emptyMsg);
        assertEquals("Default message ID should be 0", 0, emptyMsg.getMessageId());
        assertFalse("Default read status should be false", emptyMsg.isReadStatus());
    }
}
