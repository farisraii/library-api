package com.test.eigen.loan;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String memberCode;
    private String bookCode;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private String status;

}