package com.b9.json.inventory;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

class InventoryApplicationTest {

    @Test
    void contextLoads() {
        InventoryApplication application = new InventoryApplication();
        assertNotNull(application);
    }

    @Test
    void mainRunsSuccessfully() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            String[] args = new String[]{"test"};
            assertDoesNotThrow(() -> InventoryApplication.main(args));
            mockedSpringApplication.verify(() -> SpringApplication.run(InventoryApplication.class, args));
        }
    }

    @Test
    void mainThrowsExceptionOnFailure() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            String[] args = new String[]{};
            mockedSpringApplication.when(() -> SpringApplication.run(InventoryApplication.class, args))
                    .thenThrow(new RuntimeException("Error"));

            assertThrows(RuntimeException.class, () -> InventoryApplication.main(args));
        }
    }
}