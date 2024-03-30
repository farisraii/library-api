package com.test.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.test.eigen.book.Book;
import com.test.eigen.book.BookDTO;
import com.test.eigen.book.BookRepository;
import com.test.eigen.book.BookService;

@ExtendWith(MockitoExtension.class) // Add this line to enable MockitoExtension
public class ServiceTesting {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        // No need for MockitoAnnotations.initMocks(this) anymore
    }

    @Test
    public void testGetAllBooks() {
        
        Book book1 = new Book();
        book1.setId(1L);
        book1.setCode("B001");
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setStock(10);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setCode("B002");
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setStock(5);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<BookDTO> books = bookService.getAllBooks();

        assertEquals(2, books.size());
        assertEquals("Book 1", books.get(0).getTitle());
        assertEquals("Book 2", books.get(1).getTitle());
    }
}
