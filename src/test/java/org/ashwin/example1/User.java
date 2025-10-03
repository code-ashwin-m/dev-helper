package org.ashwin.example1;

import org.ashwin.service.annotations.Column;
import org.ashwin.service.annotations.Entity;
import org.ashwin.service.annotations.GeneratedId;
import org.ashwin.service.annotations.Id;

@Entity(table = "users")
public class User {
    @Id
    @GeneratedId
    @Column(name = "id")
    private int id;

    @Column(name = "username", uniqueCombo = true)
    private String username;

    @Column(name = "email", uniqueCombo = true)
    private String email;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}