package com.maktabatic.msreservation.dao;

import com.maktabatic.msreservation.entities.KeyReservation;
import com.maktabatic.msreservation.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface ReservartionRepository extends JpaRepository<Reservation, KeyReservation> {
    Long countReservationsById_IdNoticeAndDisponibleTrue(Long id);
    Long countReservationsById_IdNoticeAndDisponibleFalse(Long id);
    Long countReservationsById_RrAndDisponibleTrue(String rr);
    List<Reservation> findReservationsById_RrAndAndDisponibleFalse(String rr);
    List<Reservation> findReservationsByDisponibleTrue();
    List<Reservation> findReservationsByDisponibleFalseAndId_IdNoticeOrderByDateResvAsc(Long idNotice);
}
