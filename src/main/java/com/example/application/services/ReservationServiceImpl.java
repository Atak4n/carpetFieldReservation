package com.example.application.services;

import com.example.application.models.Reservation;
import com.example.application.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Set<Reservation> getList() {
        Set<Reservation> reservationSet =new HashSet<>();
        reservationRepository.findAll().iterator().forEachRemaining(reservationSet::add);
        return reservationSet;
    }

    @Override
    public Set<Reservation> getList(String filter, Long systemUserId) {
        Set<Reservation> reservationSet =new HashSet<>();
        reservationRepository.findByCarpetFieldContainingAndSystemUserId(filter,systemUserId).iterator().forEachRemaining(reservationSet::add);
        return reservationSet;
    }

    @Override
    public Reservation save(Reservation r) {
        return reservationRepository.save(r);
    }

    @Override
    public void delete(Reservation r) {
        reservationRepository.delete(r);
    }
}
