package com.example.application.services;

import com.example.application.models.Reservation;
import java.util.Set;

public interface ReservationService {
    Set<Reservation> getList();
    Set<Reservation> getList(String filter, Long systemUserId);
    Reservation save (Reservation r);
    void delete(Reservation r);
}
