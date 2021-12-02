package com.example.playgroundmanager.bootstrap;

import com.example.playgroundmanager.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BootStrapData implements CommandLineRunner {

    public BootStrapData() {
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Started in Bootstrap");
        //todo test with can wait false???


        //TEST case 1

        // Kids
        var kidList =  new ArrayList<>(List.of(
            new Kid("Eric", "Evans", true),
            new Kid("Anna", "Smith", true),
            new Kid("John", "Doe", true),
            new Kid("Jonathan", "Smith", true),
            new Kid("Katherine", "Alison", true),
            new Kid("Peter", "Luis", true),
            new Kid("Lucy", "Anthony", true),
            new Kid("Bob", "Miller", true)));

        Playsite slide = new Playsite(PlaysiteType.SLIDE, 1, 1);

        // Eric tries to attend SLIDE without a ticket
        slide.attendBy(kidList.get(0));

        // Ticket validity time
        LocalDateTime timeValidFrom = LocalDateTime.now();
        LocalDateTime timeValidTo = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1).minusMinutes(1);

        var ticketList = new ArrayList<Ticket>();
        for (int i = 0; i < kidList.size(); i++) {
            ticketList.add(new Ticket(timeValidFrom, timeValidTo, 0, TicketType.INCLUDE_ALL));
            if (i >= kidList.size() - 2) { // Last two kids will have VIP tickets
                ticketList.get(i).setNumberOfTimesVIP(5);
            }

            // Create kid-to-ticket relationship
            kidList.get(i).getTickets().add(ticketList.get(i));
            ticketList.get(i).setKid(kidList.get(i));

            //  Kids go to SLIDE
            slide.attendBy(kidList.get(i));//TODO need to put some WAITS?
        }

        var correctOrderOfKidsInQueue = new ArrayList<>(
                List.of("Lucy", "Anna", "John", "Jonathan", "Bob", "Katherine", "Peter")); // NVNNNVNN

        var actualOrderOfKidsInQueue = new ArrayList<>();

        for (int i = 0; i < kidList.size() - 1; i++) {
            actualOrderOfKidsInQueue.add(slide.getNextKidFromQueue().getFirstName());
        }
        System.out.println(correctOrderOfKidsInQueue);
        System.out.println(actualOrderOfKidsInQueue);
        //TODO assert


        //TEST case 2


        Playsite carousel = new Playsite(PlaysiteType.CAROUSEL, 20, 5);
        Playsite ballPit = new Playsite(PlaysiteType.BALL_PIT, 8, 1);
        Playsite doubleSwings = new Playsite(PlaysiteType.DOUBLE_SWINGS, 2, 2);
        slide = new Playsite(PlaysiteType.SLIDE, 1, 1);
        List<Playsite> playsites = Arrays.asList(carousel, ballPit, doubleSwings, slide);

        // Kids initialized before

        var theDate = LocalDate.of(2021, 11, 1);
        timeValidFrom = LocalDateTime.of(theDate, LocalTime.of(10,0));
        timeValidTo = LocalDateTime.of(theDate, LocalTime.of(20,0));

        ticketList = new ArrayList<Ticket>();
        for (int i = 0; i < kidList.size(); i++) {
            ticketList.add(new Ticket(timeValidFrom, timeValidTo, 0, TicketType.INCLUDE_ALL));
            if (i >= kidList.size() - 2) { // Last two kids will have VIP tickets
                ticketList.get(i).setNumberOfTimesVIP(5);
            }

            // Create kid-to-ticket relationship
            kidList.get(i).getTickets().add(ticketList.get(i));
            ticketList.get(i).setKid(kidList.get(i));

            // Create kid-to-playsite relationship
            for (int j = 0; j < playsites.size(); j++) {
                KidInPlaysite kidInPlaysite = new KidInPlaysite();
                kidInPlaysite.setKid(kidList.get(i));
                kidList.get(i).getKidInPlaysite().add(kidInPlaysite);
                kidInPlaysite.setPlaysite(playsites.get(j));
                playsites.get(j).getKidInPlaysite().add(kidInPlaysite);
                kidInPlaysite.setStartTime(timeValidFrom.plusMinutes((long)j * i * 5));
                kidInPlaysite.setEndTime(timeValidFrom.plusMinutes((long)j * i * 5 + (long)5 * j));
            }
        }

        for (Kid kid : kidList) {
            kid.getHistory(); //todo add date
        }

        for (Playsite playsite : playsites) {
            playsite.getTotalVisitorCountPerDay();
        }

        // working hours of the playground

        Integer timeIntervalInMinutes = 60;
        LocalTime startTime = LocalTime.of(10,0);
        LocalTime endTime = LocalTime.of(20,0);

        for (Playsite playsite : playsites) {
            playsite.getUtilizationSnapshot(theDate, startTime, endTime, timeIntervalInMinutes);
        }
    }
}