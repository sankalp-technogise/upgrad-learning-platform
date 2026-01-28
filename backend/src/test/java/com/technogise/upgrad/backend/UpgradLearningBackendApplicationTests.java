package com.technogise.upgrad.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.springframework.context.annotation.Import({
    com.technogise.upgrad.backend.config.TestConfig.class
})
@org.springframework.test.context.ActiveProfiles("test")
class UpgradLearningBackendApplicationTests {

  @Test
  void contextLoads() {
    // Verify context loads
    org.assertj.core.api.Assertions.assertThat(this).isNotNull();
  }
}
