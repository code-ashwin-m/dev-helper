package org.ashwin;

import junit.framework.TestCase;
import org.ashwin.example1.User;
import org.ashwin.example2.Order;
import org.ashwin.example2.OrderService;
import org.ashwin.service.ApplicationContext;
import org.ashwin.service.annotations.ComponentScan;

/**
 * Unit test for simple App.
 */
@ComponentScan("org.ashwin")
public class OrderTest extends TestCase {
    ApplicationContext context;
    OrderService orderService;

    public OrderTest(String testName )
    {
        super( testName );
        context = new ApplicationContext(OrderTest.class, DatabaseConfig.class);
        orderService = context.getBean(OrderService.class);
        orderService.createTableIfNotExists();
    }

    public void testCreateOrder() throws Exception {
        Order order = new Order("ashwin", "ashwin@gmail.com", "mobile", 100);
        String uuid = orderService.create(order);
        System.out.println("Order created: " + uuid);
    }

    public void testUpdateOrder() throws Exception {
        Order order = orderService.findOrderByEmail("ashwin@gmail.com");
        order.setPrice(200);
        orderService.updateOrder(order);
        order = orderService.getUserById(order.getId());
        System.out.println("Order updated: " + order);
    }

    public void testDeleteOrder() throws Exception {
        Order order = orderService.findOrderByEmailAndProduct("ashwin@gmail.com", "mobile");
        orderService.deleteOrder(order.getId());
        System.out.println("Deleted order: " + order.getId());
    }
}
