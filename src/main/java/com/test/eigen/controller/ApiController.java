package com.test.eigen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.test.eigen.book.BookService;
import com.test.eigen.loan.LoanService;
import com.test.eigen.member.MemberService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private MemberService memberService;

    // Endpoint untuk mendapatkan semua buku
    @GetMapping("/books")
    public Object getAllBooks() {
        return bookService.getAllBooks();
    }

    // Endpoint untuk meminjam buku
    @PostMapping("/borrow/{memberCode}/{bookCode}")
    public Object borrowBook(@PathVariable String memberCode, @PathVariable String bookCode) {
        return loanService.borrowBook(memberCode, bookCode);
    }

    // Endpoint untuk mengembalikan buku
    @PostMapping("/return/{memberCode}/{bookCode}")
    public Object returnBook(@PathVariable String memberCode, @PathVariable String bookCode) {
        Object returnMessage = loanService.returnBook(memberCode, bookCode);
        return returnMessage;
    }

    // Endpoint untuk mendapatkan semua peminjaman buku oleh anggota tertentu
    @GetMapping("/loans/{memberCode}")
    public Object getAllLoansByMember(@PathVariable String memberCode) {
        return loanService.getAllLoansByMember(memberCode);
    }

    // Endpoint untuk mendapatkan semua anggota
    @GetMapping("/members")
    public Object getAllMembers() {
        return memberService.getAllMembers();
    }
}
