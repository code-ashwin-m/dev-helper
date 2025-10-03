package org.ashwin.example2;

import org.ashwin.service.annotations.Autowired;
import org.ashwin.service.annotations.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public void createTableIfNotExists() {
        orderRepository.createTableIfNotExists();
    }

    public String create(Order order) {
        return (String) orderRepository.create(order);
    }

    public Order findOrderByEmail(String email) {
        return orderRepository.findByEmail(email).get(0);
    }

    public void updateOrder(Order order) {
        orderRepository.update(order);
    }

    public Order getUserById(String id) {
        return orderRepository.findById(id);
    }

    public Order findOrderByEmailAndProduct(String mail, String mobile) {
        return orderRepository.findByEmailAndProduct(mail, mobile).get(0);
    }

    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }
}
