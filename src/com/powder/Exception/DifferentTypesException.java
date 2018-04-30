package com.powder.Exception;

public class DifferentTypesException extends Throwable {
    @Override
    public String getMessage() {
        return "Different types Exception";
    }
}
