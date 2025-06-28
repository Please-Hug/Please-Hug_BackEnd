package org.example.hugmeexp.domain.attendance.validation;

import org.example.hugmeexp.domain.attendance.exception.InvalidValueException;
import org.example.hugmeexp.domain.attendance.exception.UsernameTooLongException;


public class UsernameValidator {
    public static final int USERNAME_MAX_LENGTH = 32;

    public static void validate(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidValueException("username must not be blank");
        }
        if (username.length() > USERNAME_MAX_LENGTH) {
            throw new UsernameTooLongException();
        }
    }
}