package com.technogise.upgrad.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.context.annotation.Import({
  com.technogise.upgrad.backend.config.TestConfig.class
})
@org.springframework.test.context.ActiveProfiles("test")
class OpenApiIntegrationTest {

  @LocalServerPort private int port;

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Test
  void shouldExposeOpenApiDocs() throws Exception {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/v3/api-docs"))
            .GET()
            .build();

    final HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("DEBUG: Status: " + response.statusCode());
    System.out.println("DEBUG: Body: " + response.body());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("openapi");
  }

  @Test
  void shouldExposeSwaggerUi() throws Exception {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/swagger-ui/index.html"))
            .GET()
            .build();

    final HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("DEBUG: SwaggerUI Status: " + response.statusCode());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("swagger-ui");
  }

  @Test
  void shouldIncludeRequiredFieldsInOpenApiSpec() throws Exception {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/v3/api-docs"))
            .GET()
            .build();

    final HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);

    final String openApiJson = response.body();

    // Verify that the OpenAPI spec contains "required" arrays for our DTOs
    assertThat(openApiJson)
        .as("OpenAPI spec should contain 'required' field arrays")
        .contains("\"required\"");

    // Verify specific required fields for LoginRequest (email, otp)
    assertThat(openApiJson)
        .as("LoginRequest schema should mark 'email' as required")
        .containsPattern("\"LoginRequest\".*\"required\".*\"email\"");

    assertThat(openApiJson)
        .as("LoginRequest schema should mark 'otp' as required")
        .containsPattern("\"LoginRequest\".*\"required\".*\"otp\"");

    // Verify OtpRequest has required email field
    assertThat(openApiJson)
        .as("OtpRequest schema should mark 'email' as required")
        .containsPattern("\"OtpRequest\".*\"required\".*\"email\"");
  }
}
