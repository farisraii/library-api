package com.test.eigen.loan;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private String memberCode;
    private String bookCode;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private String status;
}
