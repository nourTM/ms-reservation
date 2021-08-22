package com.maktabatic.msreservation.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor
public class KeyReservation implements Serializable {
    private Long idNotice;
    private String rr;
}
