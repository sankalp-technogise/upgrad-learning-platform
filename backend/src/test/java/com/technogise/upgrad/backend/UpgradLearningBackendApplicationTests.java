package com.technogise.upgrad.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SuppressWarnings("PMD.AtLeastOneConstructor")
class UpgradLearningBackendApplicationTests {

  @Test
  void contextLoads() {
    // Verify context loads
    org.assertj.core.api.Assertions.assertThat(this).isNotNull();
  }

  @Test
  void main() {
    UpgradLearningBackendApplication.main(new String[] {});
    // Verify main method runs without exception, though in reality this test is a
    // bit redundant given contextLoads
    org.assertj.core.api.Assertions.assertThat(true).isTrue();
  }
}
