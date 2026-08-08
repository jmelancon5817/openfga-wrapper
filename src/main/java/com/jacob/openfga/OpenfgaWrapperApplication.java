package com.jacob.openfga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point for the OpenFGA wrapper service.
 *
 * <p>This service exposes a small, opinionated REST facade over the OpenFGA
 * fine-grained authorization engine so that internal callers do not have to
 * depend on the OpenFGA SDK directly.
 */
@SpringBootApplication
public class OpenfgaWrapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenfgaWrapperApplication.class, args);
    }
}
