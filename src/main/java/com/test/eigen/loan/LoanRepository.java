package com.test.eigen.loan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberCode(String memberCode);

    List<Loan> findByMemberCodeAndStatus(String memberCode, String status);

    Loan findTopByMemberCodeAndStatusOrderByReturnDateDesc(String memberCode, String string);

    Loan findByMemberCodeAndBookCodeAndStatus(String memberCode, String bookCode, String string);
}
