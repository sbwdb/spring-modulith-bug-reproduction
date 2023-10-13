package com.example.demo.module_a;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ModuleAController {

    private final ModuleAService moduleAService;

    @GetMapping("/do")
    public void doSomething() {
        moduleAService.doSomething("event-1");
    }
}
