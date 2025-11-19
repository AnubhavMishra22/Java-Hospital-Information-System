package com.hospital.model;

import com.hospital.utils.TestDataFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for User model class
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = TestDataFactory.createTestUser();
    }

    @Test
    public void testUserCreation() {
        assertNotNull("User should not be null", user);
        assertEquals("User ID should be 1", 1, user.getUserId());
        assertEquals("Username should match", "testuser", user.getUsername());
    }

    @Test
    public void testGettersAndSetters() {
        user.setUserId(10);
        assertEquals("User ID should be 10", 10, user.getUserId());

        user.setUsername("newuser");
        assertEquals("Username should be updated", "newuser", user.getUsername());

        user.setFullName("New Name");
        assertEquals("Full name should be updated", "New Name", user.getFullName());

        user.setEmail("new@email.com");
        assertEquals("Email should be updated", "new@email.com", user.getEmail());

        user.setPhone("9999999999");
        assertEquals("Phone should be updated", "9999999999", user.getPhone());
    }

    @Test
    public void testUserRole() {
        user.setRole(User.UserRole.ADMIN);
        assertEquals("Role should be ADMIN", User.UserRole.ADMIN, user.getRole());

        user.setRole(User.UserRole.DOCTOR);
        assertEquals("Role should be DOCTOR", User.UserRole.DOCTOR, user.getRole());

        user.setRole(User.UserRole.NURSE);
        assertEquals("Role should be NURSE", User.UserRole.NURSE, user.getRole());

        user.setRole(User.UserRole.RECEPTIONIST);
        assertEquals("Role should be RECEPTIONIST", User.UserRole.RECEPTIONIST, user.getRole());
    }

    @Test
    public void testUserStatus() {
        user.setStatus(User.UserStatus.ACTIVE);
        assertEquals("Status should be ACTIVE", User.UserStatus.ACTIVE, user.getStatus());

        user.setStatus(User.UserStatus.INACTIVE);
        assertEquals("Status should be INACTIVE", User.UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    public void testPasswordHandling() {
        String password = "securePassword123";
        user.setPassword(password);
        assertEquals("Password should be set correctly", password, user.getPassword());
    }

    @Test
    public void testToString() {
        String result = user.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain full name", result.contains(user.getFullName()));
        assertTrue("toString should contain role", result.contains(user.getRole().toString()));
    }

    @Test
    public void testAllUserRolesExist() {
        User.UserRole[] roles = User.UserRole.values();
        assertEquals("Should have 4 roles", 4, roles.length);

        boolean hasAdmin = false, hasDoctor = false, hasNurse = false, hasReceptionist = false;
        for (User.UserRole role : roles) {
            if (role == User.UserRole.ADMIN) hasAdmin = true;
            if (role == User.UserRole.DOCTOR) hasDoctor = true;
            if (role == User.UserRole.NURSE) hasNurse = true;
            if (role == User.UserRole.RECEPTIONIST) hasReceptionist = true;
        }

        assertTrue("Should have ADMIN role", hasAdmin);
        assertTrue("Should have DOCTOR role", hasDoctor);
        assertTrue("Should have NURSE role", hasNurse);
        assertTrue("Should have RECEPTIONIST role", hasReceptionist);
    }

    @Test
    public void testEmptyUserCreation() {
        User emptyUser = new User();
        assertNotNull("Empty user should not be null", emptyUser);
        assertEquals("Default user ID should be 0", 0, emptyUser.getUserId());
        assertNull("Default username should be null", emptyUser.getUsername());
    }

    @Test
    public void testParameterizedConstructor() {
        User newUser = new User("john", "pass123", "John Doe", "john@test.com",
                                "1234567890", User.UserRole.DOCTOR);

        assertEquals("Username should be set", "john", newUser.getUsername());
        assertEquals("Password should be set", "pass123", newUser.getPassword());
        assertEquals("Full name should be set", "John Doe", newUser.getFullName());
        assertEquals("Email should be set", "john@test.com", newUser.getEmail());
        assertEquals("Phone should be set", "1234567890", newUser.getPhone());
        assertEquals("Role should be set", User.UserRole.DOCTOR, newUser.getRole());
        assertEquals("Status should be ACTIVE by default", User.UserStatus.ACTIVE, newUser.getStatus());
    }
}
