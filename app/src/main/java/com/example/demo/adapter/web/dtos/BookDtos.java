package com.example.demo.adapter.web.dtos;

import com.example.demo.domain.model.Book;
import jakarta.validation.constraints.*;

public final class BookDtos {
    public record Create(
            @NotBlank String title,
            @NotBlank String author,
            @Min(0) @Max(2100) Integer year
    ) {}
    public record Update(
            @NotBlank String title,
            @NotBlank String author,
            @Min(0) @Max(2100) Integer year
    ) {}
    public record Response(Long id, String title, String author, Integer year) {
        public static Response from(Book b){
            return new Response(b.getId(), b.getTitle(), b.getAuthor(), b.getYear());
        }
    }
}
