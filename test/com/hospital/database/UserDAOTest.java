package com.hospital.database;

import com.hospital.model.User;
import com.hospital.utils.TestDataFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for UserDAO
 */
public class UserDAOTest {

    @Test
    public void testUserDAOExists() {
        assertNotNull("UserDAO class should exist", UserDAO.class);
    }

    @Test
    public void testAuthenticateUserMethod() throws NoSuchMethodException {
        UserDAO.class.getDeclaredMethod("authenticateUser", String.class, String.class);
    }

    @Test
    public void testGetAllUsersMethod() throws NoSuchMethodException {
        UserDAO.class.getDeclaredMethod("getAllUsers");
    }

    @Test
    public void testGetUsersByRoleMethod() throws NoSuchMethodException {
        UserDAO.class.getDeclaredMethod("getUsersByRole", User.UserRole.class);
    }

    @Test
    public void testAddUserMethod() throws NoSuchMethodException {
        UserDAO.class.getDeclaredMethod("addUser", User.class);
    }

    @Test
    public void testGetUserByIdMethod() throws NoSuchMethodException {
        UserDAO.class.getDeclaredMethod("getUserById", int.class);
    }

    @Test
    public void testUserObjectForAuth() {
        User user = TestDataFactory.createTestUser();
        assertNotNull("User username should not be null", user.getUsername());
        assertNotNull("User password should not be null", user.getPassword());
        assertNotNull("User role should not be null", user.getRole());
    }
}
