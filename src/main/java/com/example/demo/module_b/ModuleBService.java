package com.example.demo.module_b;

import com.example.demo.module_a.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class ModuleBService {

    final static Logger logger= LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    int invocationCount = 0;

    @ApplicationModuleListener
    public void on(DomainEvent domainEvent) throws InterruptedException {
        incrementInvocationCount();
        logger.info("----> received  {}", domainEvent.name());
        Thread.sleep(50); // simulate time to process the domain event
    }

    public int getInvocationCount() {
        return invocationCount;
    }

    public void resetInvocationCount() {
        invocationCount = 0;
    }

    public synchronized void incrementInvocationCount() {
        invocationCount++;
    }
}
