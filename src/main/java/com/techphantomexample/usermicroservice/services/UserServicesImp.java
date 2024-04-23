package com.techphantomexample.usermicroservice.services;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServicesImp implements UserService
{
    private static final Logger log = LoggerFactory.getLogger(UserServicesImp.class);
    @Autowired
    UserRepository userRepository;

    public UserServicesImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public String createUser(User user) {
        String userFullName = user.getUserFullName();
        String userEmail = user.getUserEmail();
        String userPassword = user.getUserPassword();
        String userRole = user.getUserRole();

        if (StringUtils.isBlank(userFullName) ||StringUtils.isBlank(userEmail) || StringUtils.isBlank(userPassword) || StringUtils.isBlank(userRole)) {
            log.error("Missing Fields");
            return "All fields are required";
        }

        if (!isValidEmail(userEmail)) {
            log.error("Invalid email address");
            return "Invalid email address";
        }

        if (existsByEmail(userEmail)) {
            log.error("User already exists");
            return "User with provided Email ID exists";
        }

        if (!isValidPassword(userPassword)) {
            log.error("Password format wrong");
            return "Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit";
        }

        String hashedPassword = BCrypt.hashpw(userPassword, BCrypt.gensalt());
        user.setUserPassword(hashedPassword);

        if (!isValidUserRole(userRole)) {
            log.error("Entered userRole is incorrect");
            return "User role should be one among: ADMIN, SUPERVISOR, BUYER, SELLER";
        }

        userRepository.save(user);
        log.info("User Created successfully");
        return "User Created successfully";
    }

    @Override
    public String updateUser(int userId, User newUserDetails) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            existingUser.setUserFullName(newUserDetails.getUserFullName());

            String newEmail = newUserDetails.getUserEmail();
            if (newEmail != null && !isValidEmail(newEmail)) {
                log.error("Invalid email address updated");
                return "Invalid email address";
            }
            existingUser.setUserEmail(newEmail);

            String newPassword = newUserDetails.getUserPassword();
            if (newPassword != null && !isValidPassword(newPassword)) {
                log.error(" Updated Password format wrong");
                return "Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit";
            }
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            existingUser.setUserPassword(hashedPassword);

            String newUserRole = newUserDetails.getUserRole();
            if (newUserRole != null && !isValidUserRole(newUserRole)) {
                log.error("Updated userRole is incorrect");
                return "User role should be one among: ADMIN, SUPERVISOR, BUYER, SELLER";
            }
            existingUser.setUserRole(newUserRole);


            userRepository.save(existingUser);
            log.info("User Updated successfully");
            return "User Updated Successfully";
        } else {

            return "";
        }
    }

    @Override
    public String deleteUser(int userId) {
        if (userRepository.existsById(userId))
        {
            userRepository.deleteById(userId);
            log.info("User deleted");
            return "User Deleted Successfully";
        }
        log.info("User does not exists");
        return "User Not Found";
    }

    @Override
    public User getUser(int userId) {
        if (userRepository.existsById(userId))
        {
            log.info("User found and returned");
            return userRepository.findById(userId).get();
        }
        log.info("user not found");
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Returning all users in database");
        return userRepository.findAll();
    }

    private boolean isValidEmail(String email) {
        // Regular expression for basic email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    private boolean existsByEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches("^(?=.*[A-Z])(?=.*\\d).+$");
    }

    private boolean isValidUserRole(String userRole) {
        if (userRole == null) {
            return false;
        }
        String userRoleString = userRole.toString().toUpperCase(); // Convert user role to uppercase
        List<String> validRoles = Arrays.asList("ADMIN", "SUPERVISOR", "BUYER", "SELLER");
        return validRoles.contains(userRoleString);
    }

}
