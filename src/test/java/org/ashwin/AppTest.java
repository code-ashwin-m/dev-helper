package org.ashwin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ashwin.example1.User;
import org.ashwin.example1.UserService;
import org.ashwin.service.ApplicationContext;
import org.ashwin.service.annotations.ComponentScan;

/**
 * Unit test for simple App.
 */
@ComponentScan("org.ashwin")
public class AppTest extends TestCase {
    ApplicationContext context;
    UserService userService;

    public AppTest( String testName )
    {
        super( testName );
        context = new ApplicationContext(AppTest.class, DatabaseConfig.class);
        userService = context.getBean(UserService.class);
    }

    public void testCreateUser() throws Exception {
        User user = new User("ashwin", "ashwin@gmail.com");
        userService.createUser(user);
        System.out.println(user);
    }

    public void testUpdateUser() throws Exception {
        User user = userService.getUserByUsernameAndEmail("ashwin", "ashwin@gmail.com");
        user.setUsername("ashwin123");
        user.setEmail("ashwin123@gmail.com");
        userService.updateUser(user);

        user = userService.getUserById(user.getId());
        System.out.println(user);
    }

    public void testDeleteUser() throws Exception {
        User user = userService.getUserByUsernameAndEmail("ashwin123", "ashwin123@gmail.com");
        System.out.println(user);
        userService.deleteUserById(user.getId());
        System.out.println("User deleted: " + user.getId());
    }

}
