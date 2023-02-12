package com.example.demo.controller;

import com.example.demo.dto.Person;
import com.example.demo.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/persons")
    public List<Person> getAll() {
        return testService.getAll();
    }

    @GetMapping("/person/{id}")
    public Person getById(@PathVariable Long id) {
        return testService.getById(id);
    }

}
