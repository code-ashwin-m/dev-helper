package org.ashwin.example2;

import org.ashwin.service.annotations.Column;
import org.ashwin.service.annotations.Entity;
import org.ashwin.service.annotations.GeneratedId;
import org.ashwin.service.annotations.Id;
import org.ashwin.service.enums.GenerationType;

@Entity(table = "orders")
public class Order {
    @Id
    @GeneratedId(strategy = GenerationType.CUSTOM, generator = UUIDGenerator.class)
    @Column(name = "id")
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "product")
    private String product;

    @Column(name = "price")
    private double price;

    public Order() {
    }

    public Order(String username, String email, String product, double price) {
        this.username = username;
        this.email = email;
        this.product = product;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", product='" + product + '\'' +
                ", price=" + price +
                '}';
    }
}
