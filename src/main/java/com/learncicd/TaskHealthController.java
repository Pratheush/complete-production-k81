package com.learncicd;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskHealthController {

    // ✅ Health check endpoint
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/health")
    public String health(){
        return "Task Application is healthy!";
    }
}
