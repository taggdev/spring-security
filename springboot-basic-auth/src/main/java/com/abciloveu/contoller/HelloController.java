package com.abciloveu.contoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/hello")
    public String hello() {
        logger.debug("Debug level - Hello Logback");

        logger.info("Info level - Hello Logback");

        logger.error("Error level - Hello Logback");
        return "Welcome tp Global Markets Solution Department(GMS)";
    }
}
