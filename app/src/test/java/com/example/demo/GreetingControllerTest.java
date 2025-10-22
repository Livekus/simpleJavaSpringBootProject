package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GreetingController.class)
class GreetingControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void defaultGreeting() throws Exception {
        mvc.perform(get("/greeting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, World!"));
    }

    @Test
    void greetingWithName() throws Exception {
        mvc.perform(get("/greeting").param("name", "BOM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, BOM!"));
    }
}
