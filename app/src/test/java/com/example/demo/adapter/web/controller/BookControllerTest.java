package com.example.demo.adapter.web.controller;

import com.example.demo.adapter.web.dtos.BookDtos;
import com.example.demo.application.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mvc;
    @MockBean
    BookService svc;

    @Test
    void getBook() throws Exception {
        when(svc.get(1L)).thenReturn(new BookDtos.Response(1L, "DDD", "Evans", 2003));
        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("DDD"));
    }
}
