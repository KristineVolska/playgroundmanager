package com.example.playgroundmanager.bootstrap;

import com.example.playgroundmanager.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Component
public class BootStrapData implements CommandLineRunner {

    public BootStrapData() {
    }

    @Override
    public void run(String... args) throws Exception {

        Kid eric = new Kid("Eric", "Evans", true);
        LocalDateTime timeValidFrom = LocalDateTime.now();
        LocalDateTime timeValidTo = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1).minusMinutes(1);

        Ticket ticket = new Ticket(timeValidFrom, timeValidTo, 5, TicketType.INCLUDE_ALL);


        eric.getTickets().add(ticket);
        ticket.setKid(eric);

        Playsite slide = new Playsite(PlaysiteType.SLIDE, 1, 1); //where do i change the current value?
        Playsite carousel = new Playsite(PlaysiteType.CAROUSEL, 20, 1); //where do i change the current value?
        Playsite ballPit = new Playsite(PlaysiteType.BALL_PIT, 8, 1); //where do i change the current value?
        //List<Playsite> playsites = Arrays.asList(slide, carousel, ballPit);
        List<Playsite> playsites = Arrays.asList(carousel, ballPit);

        //eric
        for (int i = 0; i < playsites.size(); i++) {
            KidInPlaysite kinInPlaysite = new KidInPlaysite();
            kinInPlaysite.setKid(eric);
            eric.getKidInPlaysite().add(kinInPlaysite);
            kinInPlaysite.setPlaysite(playsites.get(i));
            playsites.get(i).getKidInPlaysite().add(kinInPlaysite);
            kinInPlaysite.setStartTime(LocalDateTime.now().plusMinutes((long)i * 5));
            kinInPlaysite.setEndTime(LocalDateTime.now().plusMinutes((long)i * 5 + 5));
            //carousel.setCurrentKidCount(0);
            Integer currentCount = playsites.get(i).getCurrentKidCount();
            playsites.get(i).setCurrentKidCount(++currentCount); //this logic wont work!!!!!!!!!!!!!!!!!!!!
        }



        Kid anna = new Kid("Anna", "Smith", true);
        timeValidFrom = LocalDateTime.now();
        timeValidTo = LocalDateTime.now().plusDays(1).minusSeconds(1);
        ticket = new Ticket(timeValidFrom, timeValidTo, 0, TicketType.INCLUDE_ALL);
        anna.getTickets().add(ticket);
        ticket.setKid(anna);

        Kid john = new Kid("John", "Doe", false);
        timeValidFrom = LocalDateTime.now();
        timeValidTo = LocalDateTime.now().plusDays(1).minusSeconds(1);
        ticket = new Ticket(timeValidFrom, timeValidTo, 5, TicketType.INCLUDE_ALL);
        john.getTickets().add(ticket);
        ticket.setKid(john);

        slide.attendBy(eric);
        slide.attendBy(anna);
        slide.attendBy(john);

        var q = slide.getMainQueue();
        for(Kid kid : q) {
            System.out.println("I'm in queue: " + kid.getFirstName());
        }

        slide.leaveBy(eric);
        q = slide.getMainQueue();
        for(Kid kid : q) {
            System.out.println("I'm in queue: " + kid.getFirstName());
        }

        slide.leaveBy(anna);
        q = slide.getMainQueue();
        for(Kid kid : q) {
            System.out.println("I'm in queue: " + kid.getFirstName());
        }


        System.out.println("Started in Bootstrap");
        eric.getHistory();
        anna.getHistory();
        john.getHistory();
    }
}