package com.emzaz.eshoppers.exception;

public class OptimisticLockingFailureException extends RuntimeException {
    public OptimisticLockingFailureException(String msg) {
        super(msg);
    }
}
