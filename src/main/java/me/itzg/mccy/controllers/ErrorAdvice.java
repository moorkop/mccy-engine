package me.itzg.mccy.controllers;

import com.spotify.docker.client.DockerRequestException;
import me.itzg.mccy.model.FailedRequest;
import me.itzg.mccy.types.MccyClientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
@ControllerAdvice(basePackageClasses = ErrorAdvice.class)
public class ErrorAdvice {

    @ExceptionHandler
    public ResponseEntity<?> handleDockerClientException(DockerRequestException e) {
        return ResponseEntity.badRequest()
                .body(new FailedRequest(e.getClass(), e.message()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleDockerClientException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new FailedRequest(e.getClass(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleDockerClientException(MccyClientException e) {
        return ResponseEntity.badRequest()
                .body(new FailedRequest(e.getClass(), e.getMessage()));
    }
}
