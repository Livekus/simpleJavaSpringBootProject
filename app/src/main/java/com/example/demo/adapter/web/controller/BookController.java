package com.example.demo.adapter.web.controller;

import com.example.demo.application.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.adapter.web.dtos.BookDtos.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookService svc;
  public BookController(BookService svc){ this.svc = svc; }

  @PostMapping
  public ResponseEntity<Response> create(@RequestBody @Valid Create in){
    var out = svc.create(in);
    return ResponseEntity.created(java.net.URI.create("/api/books/" + out.id())).body(out);
  }

  @GetMapping("/{id}")
  public Response get(@PathVariable long id){ return svc.get(id); }

  @PutMapping("/{id}")
  public Response update(@PathVariable long id, @RequestBody @Valid Update in){ return svc.update(id, in); }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable long id){
    svc.delete(id);
    return ResponseEntity.noContent().build();
  }
}
