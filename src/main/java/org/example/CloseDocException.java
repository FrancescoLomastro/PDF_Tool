package org.example;

import java.io.IOException;

public class CloseDocException extends Throwable {
    public CloseDocException(IOException e) {
    }
}
