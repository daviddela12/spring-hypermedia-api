package com.baeldung.persistence;

import com.baeldung.SpringHypermediaApiApplication;
import com.baeldung.model.Book;
import com.baeldung.web.controller.BookClient;
import com.baeldung.web.controller.BookControllerFeignClientBuilder;
import com.baeldung.web.resource.BookResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringHypermediaApiApplication.class)
@WebAppConfiguration
public class BookRepositoryTest {
    private BookClient bookClient;

    @Autowired
    private BookRepository bookRepository;

    @Before
    public void setup() {
        BookControllerFeignClientBuilder bookFeignClient = new BookControllerFeignClientBuilder();
        bookClient = bookFeignClient.getBookClient();
    }

    @Test
    public void persisting() {
        Book book1 = new Book();
        book1.setAuthor("Author1");
        book1.setTitle("Title1");
        book1.setIsbn(UUID.randomUUID().toString());
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setAuthor("Author2");
        book2.setTitle("Title2");
        book2.setIsbn(UUID.randomUUID().toString());
        bookRepository.save(book2);

    }

    @Test
    public void givenBookClient_shouldRunSuccessfully() throws Exception {
        List<Book> books = bookClient.findAll().stream()
                .map(BookResource::getBook)
                .collect(Collectors.toList());

        assertTrue(books.size() > 2);
    }

    @Test
    public void givenBookClient_shouldFindOneBook() throws Exception {
        Book book = bookClient.findByIsbn("0151072558").getBook();
        assertThat(book.getAuthor(), containsString("Orwell"));
    }

    @Test
    public void givenBookClient_shouldPostBook() throws Exception {
        String isbn = UUID.randomUUID().toString();
        Book book = new Book(4L, isbn, "Me", "It's me!", null, null);
        bookClient.create(book);
        book = bookClient.findByIsbn(isbn).getBook();

        assertThat(book.getAuthor(), is("Me"));
    }
}
