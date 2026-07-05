package org.sge.backend.exception;

import lombok.Getter;

@Getter
public class BusinessRuleViolationException extends RuntimeException {
    private final String codigo;

    public BusinessRuleViolationException(String codigo, String message) {
        super(message);
        this.codigo = codigo;
    }
}
