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
  com.technogise.upgrad.backend.config.TestConfig.class,
  com.technogise.upgrad.backend.config.TestContainersConfig.class
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

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("openapi");
  }

  @Test
  void shouldExposeSwaggerUi() throws Exception {
    // Swagger UI usually redirects, so we follow redirects or check the redirect
    // location
    // HttpClient follows redirects by default usually? No, default is NEVER.
    // Let's create a client that follows redirects or just check the first
    // response.
    // springdoc-openapi-starter-webmvc-ui serves at /swagger-ui/index.html directly
    // too.

    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/swagger-ui/index.html"))
            .GET()
            .build();

    final HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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
    // The spec should have required fields for LoginRequest and OtpRequest
    assertThat(openApiJson)
        .as("OpenAPI spec should contain 'required' field arrays")
        .contains("\"required\"");

    // Verify specific required fields for LoginRequest (email, otp)
    // The JSON structure should include: "required": ["email", "otp"] or similar
    assertThat(openApiJson)
        .as("LoginRequest schema should mark 'email' as required")
        .containsPattern("\"LoginRequest\".*\"required\".*\"email\"");

    // Verify OtpRequest has required email field
    assertThat(openApiJson)
        .as("OtpRequest schema should mark 'email' as required")
        .containsPattern("\"OtpRequest\".*\"required\".*\"email\"");
  }
}
