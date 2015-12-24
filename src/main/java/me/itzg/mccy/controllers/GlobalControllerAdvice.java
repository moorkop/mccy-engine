package me.itzg.mccy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<?> handleFileNotFound(FileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
