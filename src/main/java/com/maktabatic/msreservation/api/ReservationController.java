package com.maktabatic.msreservation.api;

import com.maktabatic.msreservation.dao.ReservartionRepository;
import com.maktabatic.msreservation.entities.KeyReservation;
import com.maktabatic.msreservation.entities.Reservation;
import com.maktabatic.msreservation.model.BookState;
import com.maktabatic.msreservation.model.EmailTemplate;
import com.maktabatic.msreservation.model.LoanReturn;
import com.maktabatic.msreservation.proxy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.List;

@RestController
@EnableScheduling
@RequestMapping("api")
@RefreshScope
public class ReservationController {
    @Value("${punished : You cannot Reserve You are Punished}")
    String punished;

    @Value("${valid.reserve : You have two Days to go to the library and loan your reserved book}")
    String valid_reserve;

    @Value("${no.dispo : Sorry, You can not reserve this book now but you will be notified when it is available}")
    String no_dispo;

    @Value("${already.reserve : you had already a reservation}")
    String already_reserve;

    @Value("${borrowed : You have already a borrowed book}")
    String borrowed;

    @Value("${invalid.rr : this RFID is Unknown}")
    String invalid_rr;

    @Value("${expired.reserve.subject : Expired Reservation}")
    String expired_reservation_subject;

    @Value("${expired.reserve.body : Your reservation has been expired}")
    String expired_reservation_body;

    @Value("${day : 86400000}")
    long DAY_MILLIS;

    @Value("${reserve.days : 2}")
    long reserve_days;

    @Value("${available.reserve.subject : Available Reserved Book}")
    String available_reserve_subject;

    @Value("${available.reserve.body : Your reserved book is available, you have ${reserve.days} days to loan it}")
    String available_reserve_body;

    @Autowired
    NotifProxy notifProxy;
    @Autowired
    LateProxy lateProxy;

    @Autowired
    BooksProxy booksProxy;

    @Autowired
    LoanReturnProxy loanReturnProxy;

    @Autowired
    ReaderProxy readerProxy;

    @Autowired
    ReservartionRepository reservartionRepository;

    @GetMapping("/disponible/{idNotice}")
    public Long countDisponible(@PathVariable("idNotice") Long id){
        return booksProxy.countExampTotal(id)-loanReturnProxy.countLoaned(id)-reservartionRepository.countReservationsById_IdNoticeAndDisponibleTrue(id);
    }

    @GetMapping("/waiting/{idNotice}")
    Long countWaiting(@PathVariable("idNotice") Long id){
        return reservartionRepository.countReservationsById_IdNoticeAndDisponibleFalse(id);
    }

    @GetMapping("/verify")
    boolean verifyReservationDisponible(@RequestParam("id") Long idnotice,@RequestParam("rr") String rr){
        //return reservartionRepository.existsById(new KeyReservation(idnotice,rr));
        return reservartionRepository.findById(new KeyReservation(idnotice,rr)).isPresent() && reservartionRepository.findById(new KeyReservation(idnotice,rr)).get().isDisponible();
    }

    @DeleteMapping("/delete")
    boolean deleteReservation( @RequestParam("id") Long idnotice,@RequestParam("rr") String rr){
        Reservation reservation = (reservartionRepository.findById(new KeyReservation(idnotice,rr)).isPresent())?reservartionRepository.findById(new KeyReservation(idnotice,rr)).get():null;
        if (reservation!= null ) {
            reservartionRepository.delete(reservation);
            return true;
        }
        return false;
    }

    @PostMapping("/reservation")
    public String reserve(@Validated @RequestBody KeyReservation key){
        boolean islate = lateProxy.isPunished(key.getRr());
        if (islate) return punished;
        if (readerProxy.verifyRFIDReader(key.getRr(),"toloan")!=null) {
            List<Reservation> reservations_nodispo = reservartionRepository.findReservationsById_RrAndAndDisponibleFalse(key.getRr());
            long nbresv = reservartionRepository.countReservationsById_RrAndDisponibleTrue(key.getRr());
            if (countDisponible(key.getIdNotice()) > 0 && nbresv == 0) {
                Reservation reservation = new Reservation();
                reservation.setId(key);
                reservation.setDisponible(true);
                reservation.setDateResv(new Date());
                if (reservations_nodispo!=null && reservations_nodispo.size()>0) reservartionRepository.delete(reservations_nodispo.get(0));
                reservartionRepository.save(reservation);
                return valid_reserve;
            } else if (countDisponible(key.getIdNotice()) <= 0) {
                if (reservations_nodispo!=null && reservations_nodispo.size()>0) reservartionRepository.delete(reservations_nodispo.get(0));
                Reservation reservation = new Reservation();
                reservation.setDisponible(false);
                reservation.setDateResv(new Date());
                reservartionRepository.save(reservation);
                return no_dispo;
            } else if (nbresv != 0) return already_reserve;
            else return borrowed;
        }else return invalid_rr;
    }
/*
 * "0 0 * * * *" = the top of every hour of every day.
 * "*//*10 * * * * *" = every ten seconds.
*            * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
*            * "0 0 8,10 * * *" = 8 and 10 o'clock of every day.
*            * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
*            * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
* "0 0 0 25 12 ?" = every Christmas Day at midnight

second, minute, hour, day of month, month, day(s) of week
* */
    @Scheduled(cron = "*/10 * * * * *")
    public void scan(){
        for (Reservation reservation : reservartionRepository.findReservationsByDisponibleTrue()){
            if (new Date(reservation.getDateResv().getTime() + reserve_days * DAY_MILLIS).after(new Date())
                    || new Date (reservation.getDateResv().getTime() + reserve_days * DAY_MILLIS).equals(new Date()) ){
                reservartionRepository.delete(reservation);
                EmailTemplate email = new EmailTemplate();
                email.setSendTo(readerProxy.verifyRFIDReader(reservation.getId().getRr(),"").getEmail());
                email.setSubject(expired_reservation_subject);
                email.setBody(expired_reservation_body);
                notifProxy.notify(email);
            }
        }
    }

    @PatchMapping
    boolean updateDispo(@RequestParam("id") Long idNotice){
        List<Reservation> reservationList = reservartionRepository.findReservationsByDisponibleFalseAndId_IdNoticeOrderByDateResvAsc(idNotice);
        if (!reservationList.isEmpty()) {
            Reservation priorReservation = reservationList.get(0);
            priorReservation.setDisponible(true);
            reservartionRepository.save(priorReservation);
            String rr = priorReservation.getId().getRr();
            EmailTemplate email = new EmailTemplate();
            email.setSendTo(readerProxy.verifyRFIDReader(priorReservation.getId().getRr(),"").getEmail());
            email.setSubject(available_reserve_subject);
            email.setBody(available_reserve_body);
            notifProxy.notify(email);
            return true;
        }
        return false;
    }
}
