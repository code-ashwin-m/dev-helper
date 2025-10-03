package org.ashwin.example1;

import org.ashwin.service.annotations.Autowired;
import org.ashwin.service.annotations.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void createTableIfNotExists() {
        userRepository.createTableIfNotExists();
    }
    public int createUser(User user) throws Exception {
        return (int) userRepository.create(user);
    }

    public User getUserByName(String name) {
        return userRepository.findByUsername(name).get(0);
    }

    public User getUserById(int id) throws Exception {
        return userRepository.findById(id);
    }

    public List<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByEmail(String email) {
        return userRepository.findUsersByEmail(email);
    }

    public User getUserByUsernameAndEmail(String username, String email) {
        return userRepository.findUserByUsernameAndEmail(username, email);
    }

    public void updateUser(User user) {
        userRepository.update(user);
    }

    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }
}
