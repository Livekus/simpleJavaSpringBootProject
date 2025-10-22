package com.example.demo.application.service;

import com.example.demo.application.portout.repository.BookRepository;
import com.example.demo.domain.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.adapter.web.dtos.BookDtos.*;

@Service
@Transactional
public class BookService {
    private final BookRepository repo;
    public BookService(BookRepository repo){ this.repo = repo; }

    public Response create(Create in){
        var b = new Book(in.title(), in.author(), in.year());
        return Response.from(repo.save(b));
    }

    @Transactional(readOnly = true)
    public Response get(long id){
        var b = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return Response.from(b);
    }

    public Response update(long id, Update in){
        var b = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        b.setTitle(in.title()); b.setAuthor(in.author()); b.setYear(in.year());
        return Response.from(b);
    }

    public void delete(long id){
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        repo.deleteById(id);
    }
}
