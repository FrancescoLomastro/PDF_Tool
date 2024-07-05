package org.example;

import java.io.IOException;

public class UnknownException extends Throwable {
    public UnknownException(IOException e) {
    }
}
