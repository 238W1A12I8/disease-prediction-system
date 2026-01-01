package com.example.diseaseprediction.controller;

import com.example.diseaseprediction.dto.LoginRequest;
import com.example.diseaseprediction.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterEndpoint {

        @Test
        @DisplayName("Should register new user and return JWT token")
        void shouldRegisterNewUser() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setName("New User");
            request.setEmail("newuser@test.com");
            request.setPassword("password123");

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", notNullValue()))
                    .andExpect(jsonPath("$.role", is("USER")));
        }

        @Test
        @DisplayName("Should return 400 for duplicate email")
        void shouldRejectDuplicateEmail() throws Exception {
            // First registration
            RegisterRequest request = new RegisterRequest();
            request.setName("First User");
            request.setEmail("duplicate@test.com");
            request.setPassword("password123");

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // Second registration with same email
            request.setName("Second User");
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for invalid request body")
        void shouldRejectInvalidRequest() throws Exception {
            // Missing required fields
            String invalidJson = "{}";

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginEndpoint {

        @Test
        @DisplayName("Should login with demo user credentials")
        void shouldLoginWithDemoUser() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("user@demo.com");
            request.setPassword("password");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", notNullValue()))
                    .andExpect(jsonPath("$.role", is("USER")));
        }

        @Test
        @DisplayName("Should login with admin credentials")
        void shouldLoginWithAdminCredentials() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("admin@demo.com");
            request.setPassword("admin123");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", notNullValue()))
                    .andExpect(jsonPath("$.role", is("ADMIN")));
        }

        @Test
        @DisplayName("Should return error for invalid credentials")
        void shouldRejectInvalidCredentials() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("user@demo.com");
            request.setPassword("wrongpassword");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Should return error for non-existent user")
        void shouldRejectNonExistentUser() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("nonexistent@test.com");
            request.setPassword("password123");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Authentication Flow")
    class AuthenticationFlow {

        @Test
        @DisplayName("Should allow access to protected endpoint with valid JWT")
        void shouldAllowAccessWithValidJwt() throws Exception {
            // Login to get token
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("user@demo.com");
            loginRequest.setPassword("password");

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("token").asText();

            // Access protected endpoint - use predictions history which exists
            mockMvc.perform(get("/predictions/me")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should reject access to protected endpoint without JWT")
        void shouldRejectAccessWithoutJwt() throws Exception {
            mockMvc.perform(get("/predictions/me"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should reject access with invalid JWT")
        void shouldRejectAccessWithInvalidJwt() throws Exception {
            mockMvc.perform(get("/predictions/me")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isForbidden());
        }
    }
}
