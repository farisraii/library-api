package com.test.eigen.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.test.eigen.book.Book;
import com.test.eigen.book.BookRepository;
import com.test.eigen.member.Member;
import com.test.eigen.member.MemberRepository;

@Service
public class LoanService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    public List<LoanDTO> getAllLoansByMember(String memberCode) {
        List<LoanDTO> loanDTOs = loanRepository.findByMemberCode(memberCode).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return loanDTOs;
    }

    public ResponseEntity<Object> borrowBook(String memberCode, String bookCode) {
        Member member = memberRepository.findByCode(memberCode);
        if (member == null) {
            return buildJsonErrorResponse("Member with code " + memberCode + " not found");
        }

        if (member.isPenaltyStatus()) {
            LocalDate lastReturnDate = getLastReturnDate(memberCode);
            if (lastReturnDate != null) {
                LocalDate currentDate = LocalDate.now();
                long daysBetween = ChronoUnit.DAYS.between(lastReturnDate, currentDate);
                if (daysBetween >= 3) {
                    member.setPenaltyStatus(false);
                    memberRepository.save(member);
                } else {
                    return buildJsonErrorResponse("Member with code " + memberCode + " is currently under penalty");
                }
            }
        }

        List<Loan> borrowedBooks = loanRepository.findByMemberCodeAndStatus(memberCode, "BORROWED");
        if (borrowedBooks.size() >= 2) {
            return buildJsonErrorResponse("Member with code " + memberCode + " has already borrowed 2 books");
        }

        Book book = bookRepository.findByCode(bookCode);
        if (book == null) {
            return buildJsonErrorResponse("Book with code " + bookCode + " not found");
        }

        if (book.getStock() <= 0) {
            return buildJsonErrorResponse("Stock of book " + book.getTitle() + " is depleted");
        }

        Loan loan = new Loan();
        loan.setMemberCode(memberCode);
        loan.setBookCode(bookCode);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus("BORROWED");
        loanRepository.save(loan);

        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        Map<String, Object> response = new HashMap<>();
        response.put("Message", "Book borrowed successfully.");
        response.put("Member", member.getName());
        response.put("Book", book.getTitle());
        response.put("Loan Date", loan.getLoanDate());
        return buildJsonResponse(response);
    }

    public ResponseEntity<Object> returnBook(String memberCode, String bookCode) {
        Loan loan = loanRepository.findByMemberCodeAndBookCodeAndStatus(memberCode, bookCode, "BORROWED");
        if (loan == null) {
            return buildJsonErrorResponse("Failed to return book. Loan entry not found.");
        }

        LocalDate returnDate = LocalDate.now();
        loan.setReturnDate(returnDate);
        loanRepository.save(loan);

        loan.setStatus("RETURNED");

        LocalDate loanDate = loan.getLoanDate();
        long daysBetween = ChronoUnit.DAYS.between(loanDate, returnDate);
        if (daysBetween > 7) {
            Member member = memberRepository.findByCode(memberCode);
            member.setPenaltyStatus(true);
            memberRepository.save(member);
        }

        Book book = bookRepository.findByCode(bookCode);
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        Member member = memberRepository.findByCode(memberCode);
        Map<String, Object> response = new HashMap<>();
        response.put("Message", "Book returned successfully.");
        response.put("Member", member.getName());
        response.put("Book", book.getTitle());
        response.put("Return Date", returnDate);
        return buildJsonResponse(response);
    }

    private ResponseEntity<Object> buildJsonResponse(Object responseBody) {
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    private ResponseEntity<Object> buildJsonErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("Error", errorMessage);
        return buildJsonResponse(response);
    }

    private LoanDTO convertToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setMemberCode(loan.getMemberCode());
        dto.setBookCode(loan.getBookCode());
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus());
        return dto;
    }

    private LocalDate getLastReturnDate(String memberCode) {
        Loan lastReturn = loanRepository.findTopByMemberCodeAndStatusOrderByReturnDateDesc(memberCode, "RETURNED");
        if (lastReturn != null) {
            return lastReturn.getReturnDate();
        }
        return null;
    }
}
