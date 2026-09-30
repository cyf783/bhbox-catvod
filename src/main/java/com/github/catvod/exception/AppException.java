package com.github.catvod.exception;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutionException;

public class AppException extends ExecutionException {

    public AppException(@Nullable String msg) {
        super(msg);
    }
}
