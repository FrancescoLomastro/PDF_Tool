package org.example.Exceptions;

import java.io.IOException;

public class UnknownException extends Throwable {
    public UnknownException(IOException e) {
    }
}
