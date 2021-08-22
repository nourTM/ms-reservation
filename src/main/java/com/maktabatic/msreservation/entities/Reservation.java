package com.maktabatic.msreservation.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@Data @AllArgsConstructor  @NoArgsConstructor
public class Reservation {
    @EmbeddedId
    private KeyReservation id;
    private boolean disponible;
    private Date dateResv;
}
