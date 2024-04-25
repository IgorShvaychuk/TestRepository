package com.example.springtest.controller;

import com.example.springtest.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private List<User> users = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
        LocalDate today = LocalDate.now();
        LocalDate eighteenYearsAgo = today.minusYears(18);
        if (user.getBirthDate().isAfter(eighteenYearsAgo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 18 years old.");
        }
        users.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(value = "/{userId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Valid @RequestBody User userUpdates) {
        Optional<User> optionalUser = users.stream().filter(u -> u.getId().equals(userId)).findFirst();
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            updateUserFields(existingUser, userUpdates);
            return ResponseEntity.status(HttpStatus.OK).body(existingUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateAllFields(@PathVariable Long userId, @Valid @RequestBody User user) {
        Optional<User> optionalUser = users.stream().filter(u -> u.getId().equals(userId)).findFirst();
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setEmail(user.getEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setBirthDate(user.getBirthDate());
            existingUser.setAddress(user.getAddress());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(existingUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        Optional<User> optionalUser = users.stream().filter(u -> u.getId().equals(userId)).findFirst();
        if (optionalUser.isPresent()) {
            users.remove(optionalUser.get());
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

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

    // Метод обновления одного или нескольких полей пользователя
    private void updateUserFields(User existingUser, User userUpdates) {
        existingUser.setEmail(userUpdates.getEmail());
        existingUser.setFirstName(userUpdates.getFirstName());
        existingUser.setLastName(userUpdates.getLastName());
        existingUser.setBirthDate(userUpdates.getBirthDate());
        existingUser.setAddress(userUpdates.getAddress());
        existingUser.setPhoneNumber(userUpdates.getPhoneNumber());
    }
}
