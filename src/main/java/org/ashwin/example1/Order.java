package org.ashwin.example1;

import org.ashwin.service.enums.GenerationType;
import org.ashwin.service.annotations.Column;
import org.ashwin.service.annotations.Entity;
import org.ashwin.service.annotations.GeneratedId;
import org.ashwin.service.annotations.Id;

@Entity(table = "orders")
public class Order {
    @Id
    @GeneratedId(strategy = GenerationType.CUSTOM, generator = UUIDGenerator.class)
    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "price")
    private int price;

    public Order() {
    }

    public Order(String username, int price) {
        this.username = username;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", price=" + price +
                '}';
    }
}
