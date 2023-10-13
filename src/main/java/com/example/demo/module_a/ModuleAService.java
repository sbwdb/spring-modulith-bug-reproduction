package com.example.demo.module_a;

import com.example.demo.module_b.ModuleBService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
@RequiredArgsConstructor
public class ModuleAService {

    final static Logger logger= LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationEventPublisher eventPublisher;
    private final ModuleBService moduleBService;

    @Transactional
    public void doSomething(String name) {
        eventPublisher.publishEvent(new DomainEvent(name));
        logger.info("Event published {}", name);
    }

}
