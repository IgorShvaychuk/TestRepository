package com.example.springtest.controller;

import com.example.springtest.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    // List of users
    private List<User> users = new ArrayList<>();

    private User findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    // Method to create a new user
    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({User.NotEmptyCheck.class, User.NotNullCheck.class, User.PastCheck.class, User.DigitsNumberCheck.class}) @RequestBody User user) {
        LocalDate today = LocalDate.now();
        LocalDate eighteenYearsAgo = today.minusYears(18);
        // Check email uniqueness before adding a new user
        if (!isEmailUnique(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with such email already exists.");
        }
        if (user.getBirthDate().isAfter(eighteenYearsAgo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 18 years old.");
        }
        users.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // Method to handle validation exception
    @ExceptionHandler({MethodArgumentNotValidException.class, })
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

        });
        return ResponseEntity.badRequest().body(errors);
    }

    // Method to update user data
    @RequestMapping(value = "/{email}", method = {RequestMethod.PATCH})
    public ResponseEntity<Object> updateUser(@PathVariable String email, @Validated({User.PastCheck.class, User.DigitsNumberCheck.class}) @RequestBody User userUpdates) {
        LocalDate today = LocalDate.now();
        LocalDate eighteenYearsAgo = today.minusYears(18);
        // Find user by email
        User existingUser = findUserByEmail(email);
        // If user not found, return error
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (userUpdates.getBirthDate().isAfter(eighteenYearsAgo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 18 years old.");
        }
        // Check email uniqueness before updating data
        if (isEmailUnique(userUpdates.getEmail())) {
            updateUserFields(existingUser, userUpdates);
            return ResponseEntity.status(HttpStatus.OK).body(existingUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }
    }

    // Method to update all user fields
    @PutMapping("/{email}")
    public ResponseEntity<Object> updateAllFields(@PathVariable String email, @Validated({User.NotEmptyCheck.class, User.NotNullCheck.class, User.PastCheck.class, User.DigitsNumberCheck.class}) @RequestBody User user) {
        LocalDate today = LocalDate.now();
        LocalDate eighteenYearsAgo = today.minusYears(18);
        // Find user by email
        User existingUser = findUserByEmail(email);
        // If user not found, return error
        if (user.getBirthDate().isAfter(eighteenYearsAgo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 18 years old.");
        }
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } else if (!isEmailUnique(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        } else {
            existingUser.setEmail(user.getEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setBirthDate(user.getBirthDate());
            existingUser.setAddress(user.getAddress());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(existingUser);
        }
    }

    // Method to delete user
    @DeleteMapping("/{email}")
    public ResponseEntity<Object> deleteUser(@PathVariable String email) {
        // Find user by email
        User existingUser = findUserByEmail(email);
        // If user not found, return error
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        users.remove(existingUser);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    // Method to search users by birth date range
    @GetMapping("/search")
    public ResponseEntity<Object> searchUsersByBirthDateRange(@RequestParam("from") String fromDate,
                                                              @RequestParam("to") String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        if (from.isAfter(to)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'From' date must be before 'To' date");
        }

        List<User> usersWithinRange = new ArrayList<>();
        for (User user : users) {
            if (!user.getBirthDate().isBefore(from) && !user.getBirthDate().isAfter(to)) {
                usersWithinRange.add(user);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(usersWithinRange);
    }

    // Method to update one or more user fields
    private void updateUserFields(User existingUser, User userUpdates) {
        if(userUpdates.getEmail() != null) {
            existingUser.setEmail(userUpdates.getEmail());
        }
        if(userUpdates.getFirstName() != null) {
            existingUser.setFirstName(userUpdates.getFirstName());
        }
        if(userUpdates.getLastName() != null) {
            existingUser.setLastName(userUpdates.getLastName());
        }
        if(userUpdates.getBirthDate() != null) {
            existingUser.setBirthDate(userUpdates.getBirthDate());
        }
        if(userUpdates.getAddress() != null) {
            existingUser.setAddress(userUpdates.getAddress());
        }
        if(userUpdates.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userUpdates.getPhoneNumber());
        }
    }

    // Method to check email uniqueness
    private boolean isEmailUnique(String email) {
        return users.stream().noneMatch(user -> user.getEmail().equals(email));
    }
}
