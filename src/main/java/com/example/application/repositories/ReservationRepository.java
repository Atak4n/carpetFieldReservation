package com.example.application.repositories;

import com.example.application.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation,Long>, JpaRepository<Reservation,Long> {
    List<Reservation> findByCarpetFieldContainingAndSystemUserId(String carpetField, Long systemUserId);
}
