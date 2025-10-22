package com.example.demo.domain.model;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String title;
    @Column(nullable = false) private String author;
    private Integer publishedYear;

    protected Book() {}
    public Book(String title, String author, Integer publishedYear){
        this.title = title; this.author = author; this.publishedYear = publishedYear;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Integer getYear() { return publishedYear; }

    public void setTitle(String v){ this.title = v; }
    public void setAuthor(String v){ this.author = v; }
    public void setYear(Integer v){ this.publishedYear = v; }
}
