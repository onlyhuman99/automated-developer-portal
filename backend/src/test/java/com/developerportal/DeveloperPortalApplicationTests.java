
package com.developerportal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "github.token=test-token"
})
class DeveloperPortalApplicationTests {

    @Test
    void contextLoads() {
    }
}
