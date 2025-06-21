package com.petstore.utils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExtension implements BeforeEachCallback, AfterEachCallback {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String testName = context.getDisplayName();
        MDC.put("testName", testName);
        logger.info("Начался тест: {}", testName);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        String testName = MDC.get("testName");
        if (context.getExecutionException().isPresent()) {
            logger.error("❌ Тест провален: {}", testName, context.getExecutionException().get());
        } else {
            logger.info("✅ Тест успешен🥳: {}", testName);
        }
        MDC.remove("testName");
    }
}