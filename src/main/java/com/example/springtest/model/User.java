package com.example.springtest.model;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class User {

    @NotBlank(groups = NotEmptyCheck.class, message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(groups = NotEmptyCheck.class, message = "First name is required")
    private String firstName;

    @NotBlank(groups = NotEmptyCheck.class, message = "Last name is required")
    private String lastName;

    @NotNull(groups = NotNullCheck.class, message = "Birth date is required")
    @Past(groups = PastCheck.class, message = "Birth date must be in the past")
    private LocalDate birthDate;

    private String address;

    @Pattern(groups = DigitsNumberCheck.class, regexp="\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;

    public User(String email, String firstName, String lastName, LocalDate birthDate) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public interface NotEmptyCheck{}

    public interface NotNullCheck{}

    public interface DigitsNumberCheck{}

    public interface PastCheck{}
}


