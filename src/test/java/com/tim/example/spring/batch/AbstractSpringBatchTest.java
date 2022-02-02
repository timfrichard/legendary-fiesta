package com.tim.example.spring.batch;

import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@SpringBatchTest
@ContextConfiguration(classes = { TestSpringBatchConfiguration.class},
        initializers = {ConfigDataApplicationContextInitializer.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public abstract class AbstractSpringBatchTest {
}
