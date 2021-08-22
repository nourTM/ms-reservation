package com.maktabatic.msreservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanReturn {
    private KeyLoanReturn id;
    private BookState state;
    private Date dateReturn;
}
