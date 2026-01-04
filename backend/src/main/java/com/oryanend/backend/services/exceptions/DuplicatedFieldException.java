package com.oryanend.backend.services.exceptions;

public class DuplicatedField extends RuntimeException {
    public DuplicatedField(String message) {
        super(message);
    }
}
