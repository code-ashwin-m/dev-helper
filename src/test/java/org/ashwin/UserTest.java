package org.ashwin;

import junit.framework.TestCase;
import org.ashwin.example1.User;
import org.ashwin.example1.UserService;
import org.ashwin.service.ApplicationContext;
import org.ashwin.service.annotations.ComponentScan;

/**
 * Unit test for simple App.
 */
@ComponentScan("org.ashwin")
public class UserTest extends TestCase {
    ApplicationContext context;
    UserService userService;

    public UserTest(String testName )
    {
        super( testName );
        context = new ApplicationContext(UserTest.class, DatabaseConfig.class);
        userService = context.getBean(UserService.class);
        userService.createTableIfNotExists();
    }

    public void testCreateUser() throws Exception {
        User user = new User("ashwin", "ashwin@gmail.com");
        int id = userService.createUser(user);
        System.out.println("User created: " + id);
    }

    public void testUpdateUser() throws Exception {
        User user = userService.getUserByUsernameAndEmail("ashwin", "ashwin@gmail.com");
        user.setUsername("ashwin123");
        user.setEmail("ashwin123@gmail.com");
        userService.updateUser(user);
        user = userService.getUserById(user.getId());
        System.out.println("User updated: " + user);
    }

    public void testDeleteUser() throws Exception {
        User user = userService.getUserByUsernameAndEmail("ashwin123", "ashwin123@gmail.com");
        userService.deleteUserById(user.getId());
        System.out.println("User deleted: " + user.getId());
    }

}
