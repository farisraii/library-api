package com.test.eigen;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.test.eigen.book.Book;
import com.test.eigen.book.BookRepository;
import com.test.eigen.loan.Loan;
import com.test.eigen.loan.LoanRepository;
import com.test.eigen.loan.LoanService;
import com.test.eigen.member.Member;
import com.test.eigen.member.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTesting {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Member member;
    private Book book;

    @BeforeEach
    public void setup() {
        member = new Member();
        member.setCode("M001");
        member.setName("John Doe");
        member.setPenaltyStatus(false);

        book = new Book();
        book.setCode("B001");
        book.setTitle("Book 1");
        book.setStock(1);
    }

    @Test
    public void testBorrowBookSuccess() {
        when(memberRepository.findByCode("M001")).thenReturn(member);
        when(bookRepository.findByCode("B001")).thenReturn(book);
        when(loanRepository.findByMemberCodeAndStatus("M001", "BORROWED")).thenReturn(null);

        Object result = loanService.borrowBook("M001", "B001");

        assertEquals("Book returned successfully.", result);
        assertEquals(0, book.getStock());
    }

    @Test
    public void testBorrowBookMemberNotFound() {
        when(memberRepository.findByCode("M002")).thenReturn(null);

        Object result = loanService.borrowBook("M002", "B001");

        assertEquals("Member dengan kode M002 tidak ditemukan", result);
    }

    @Test
    public void testBorrowBookBookNotFound() {
        when(memberRepository.findByCode("M001")).thenReturn(member);
        when(bookRepository.findByCode("B002")).thenReturn(null);

        Object result = loanService.borrowBook("M001", "B002");

        assertEquals("Buku dengan kode B002 tidak ditemukan", result);
    }

    @Test
    public void testBorrowBookStockEmpty() {
        book.setStock(0);
        when(memberRepository.findByCode("M001")).thenReturn(member);
        when(bookRepository.findByCode("B001")).thenReturn(book);

        Object result = loanService.borrowBook("M001", "B001");

        assertEquals("Stok buku Book 1 habis", result);
    }

    @Test
    public void testReturnBookSuccess() {
        Loan loan = new Loan();
        loan.setMemberCode("M001");
        loan.setBookCode("B001");
        loan.setStatus("BORROWED");
        loan.setLoanDate(LocalDate.now().minusDays(8)); // To trigger penalty

        when(loanRepository.findByMemberCodeAndBookCodeAndStatus("M001", "B001", "BORROWED")).thenReturn(loan);
        when(memberRepository.findByCode("M001")).thenReturn(member);
        when(bookRepository.findByCode("B001")).thenReturn(book);

        Object result = loanService.returnBook("M001", "B001");

        assertEquals("Book returned successfully.", result);
        assertEquals(1, book.getStock());
        assertEquals(true, member.isPenaltyStatus());
    }

    @Test
    public void testReturnBookLoanNotFound() {
        when(loanRepository.findByMemberCodeAndBookCodeAndStatus("M002", "B001", "BORROWED")).thenReturn(null);

        Object result = loanService.returnBook("M002", "B001");

        assertEquals("Failed to return book. Loan entry not found.", result);
    }
}
