package dev.majek.passwordmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class TestController {

    @RestController
    @RequestMapping("/test")
    public class Test {
        @GetMapping
        public String hello() {
            return "Swagger is alive";
        }
    }

}
