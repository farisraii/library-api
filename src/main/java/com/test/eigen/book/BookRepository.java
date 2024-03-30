    package com.test.eigen.book;

    import org.springframework.data.jpa.repository.JpaRepository;

    public interface BookRepository extends JpaRepository<Book, Long> {
        Book findByCode(String code);
    }