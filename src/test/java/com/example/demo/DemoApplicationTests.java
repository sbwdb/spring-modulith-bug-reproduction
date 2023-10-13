package com.example.demo;

import com.example.demo.module_a.ModuleAService;
import com.example.demo.module_b.ModuleBService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.modulith.events.core.EventPublicationRegistry;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoApplicationTests {

    public static final int NUMBER_OF_CALLS = 10;
    public static final int SLEEP_TIME_MILLIS = 3000;
    @Autowired
    ModuleAService moduleAService;

    @Autowired
    ModuleBService moduleBService;

    @Autowired
    IncompleteEventPublications incompleteEventPublications;


    /**
     * This works as expected. ModuleBService receives each call once.
     */
    @Test
    @Order(1)
    void testWithoutRetries() throws InterruptedException {
        moduleBService.resetInvocationCount();
        for (int i = 0; i < NUMBER_OF_CALLS; i++) {
            moduleAService.doSomething("event-" + i);
        }
        Thread.sleep(SLEEP_TIME_MILLIS);
        assertThat(moduleBService.getInvocationCount()).isEqualTo(NUMBER_OF_CALLS);
    }

    /**
     * This test fails in 1.1.0-M1, ModuleBService receives duplicate calls.
     *
     * On MacBookPro 14" / IntelliJ IDEA I get
     *  Expected :10
     *  Actual   :20
     *
     *  Sidenote: If I runt this test standalone I get "Actual   :12"
     */
    @Test
    @Order(2)
    void testWithRetries() throws InterruptedException {
        moduleBService.resetInvocationCount();
        for (int i = 0; i < NUMBER_OF_CALLS; i++) {
            moduleAService.doSomething("event-" + i);
        }
        incompleteEventPublications.resubmitIncompletePublications(__ -> true);
        Thread.sleep(SLEEP_TIME_MILLIS);
        assertThat(moduleBService.getInvocationCount()).isEqualTo(NUMBER_OF_CALLS);
    }

    /**
     * Rerun the first test just to make sure it still works after a failed test.
     */
    @Test
    @Order(3)
    void testWithoutRetriesAgain() throws InterruptedException {
        testWithoutRetries();
    }

    /**
     * The more calls to resubmitIncompletePublications the more duplicates you get.
     *
     * On MacBookPro 14" / IntelliJ IDEA I get
     *  Expected :10
     *  Actual   :40
     *
     *  Sidenote: If I runt this test standalone I get Actual   :16
     */
    @Test
    @Order(4)
    void testWithMultipleRetries() throws InterruptedException {
        moduleBService.resetInvocationCount();
        for (int i = 0; i < NUMBER_OF_CALLS; i++) {
            moduleAService.doSomething("event-" + i);
        }
        incompleteEventPublications.resubmitIncompletePublications(__ -> true);
        Thread.sleep(10);
        incompleteEventPublications.resubmitIncompletePublications(__ -> true);
        Thread.sleep(10);
        incompleteEventPublications.resubmitIncompletePublications(__ -> true);
        Thread.sleep(SLEEP_TIME_MILLIS);
        assertThat(moduleBService.getInvocationCount()).isEqualTo(NUMBER_OF_CALLS);
    }
}
