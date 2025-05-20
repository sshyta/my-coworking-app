package services.interfaces;

import domains.reserve.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> getAllReservations();
    Optional<Reservation> getReservationById(Integer id);
    Reservation createReservation(Reservation reservation);
    Reservation updateReservation(Integer id, Reservation reservation);
    boolean deleteReservation(Integer id);
}
