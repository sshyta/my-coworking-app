package services.interfaces;


import domains.clients.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    User createUser(User user);
    User updateUser(Integer id, User user);
    boolean deleteUser(Integer id);
}
