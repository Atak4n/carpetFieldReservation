package com.example.application.bootstrap;

import com.example.application.models.Reservation;
import com.example.application.models.SystemUser;
import com.example.application.services.ReservationService;
import com.example.application.services.SystemUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootStrapData implements CommandLineRunner {


    private final ReservationService reservationService;
    private final SystemUserService systemUserService;

    public BootStrapData(ReservationService reservationService, SystemUserService systemUserService) {
        this.reservationService = reservationService;
        this.systemUserService=systemUserService;
    }


    @Override
    public void run(String... args) throws Exception {

        SystemUser systemUser=new SystemUser();
        systemUser.setEmail("admin");
        systemUser.setPassword("123");
        systemUserService.save(systemUser);

        SystemUser systemUser1=new SystemUser();
        systemUser1.setEmail("test");
        systemUser1.setPassword("123");
        systemUserService.save(systemUser1);

        Reservation reservation =new Reservation();
        reservation.setName("Lionel");
        reservation.setSurname("Messi");
        reservation.setEmail("messi@gmail.com");
        reservation.setSystemUser(systemUser);
        reservationService.save(reservation);

        Reservation reservation1 =new Reservation();
        reservation1.setName("Cristiano");
        reservation1.setSurname("Ronaldo");
        reservation1.setEmail("cristiano@hotmail.com");
        reservation1.setSystemUser(systemUser);
        reservationService.save(reservation1);

        Reservation reservation2 =new Reservation();
        reservation2.setName("Neymar");
        reservation2.setSurname("JR");
        reservation2.setEmail("neymar@gmail.com");
        reservation2.setSystemUser(systemUser);
        reservationService.save(reservation2);


    }
}