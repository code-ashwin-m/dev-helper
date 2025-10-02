package org.ashwin;

import org.ashwin.example1.*;
import org.ashwin.service.ApplicationContext;
import org.ashwin.service.annotations.ComponentScan;

import java.util.List;

/**
 * Hello world!
 *
 */
@ComponentScan("org.ashwin")
public class App 
{
    public static void main( String[] args )
    {
        try {
            example1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void example1() throws Exception {
        ApplicationContext context = new ApplicationContext(App.class);

        UserService userService = context.getBean(UserService.class);
        User user = new User("ashwin", "ashwin@gmail.com");
        userService.createUser(user);

        User userFound = userService.getUserByName("ashwin");
        System.out.println(userFound);

        List<User> usersFound = userService.getUserByEmail("ashwin@gmail.com");
        System.out.println(usersFound);

        usersFound = userService.getUsersByEmail("ashwin@gmail.com");
        System.out.println(usersFound);

        userFound = userService.getUserByUsernameAndEmail("ashwin", "ashwin@gmail.com");
        System.out.println(userFound);

        userService.deleteUserById(userFound.getId());
    }
}
