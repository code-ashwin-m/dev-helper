package org.ashwin.example1;


import org.ashwin.service.annotations.Query;
import org.ashwin.service.annotations.Repository;

import java.util.List;

@Repository(entity = User.class)
public interface UserRepository {
    User save(User user);
    User findById(int id);
    List<User> findByUsername(String username);
    List<User> findByEmail(String email);
    @Query("SELECT * FROM users WHERE email = ?")
    List<User> findUsersByEmail(String email);
    @Query("SELECT * FROM users WHERE username = ? AND email = ?")
    User findUserByUsernameAndEmail(String username, String email);
    void deleteById(int id);

}
