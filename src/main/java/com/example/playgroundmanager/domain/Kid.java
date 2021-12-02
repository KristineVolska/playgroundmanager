package com.example.playgroundmanager.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class Kid {

    private final Integer id;
    private String firstName;
    private String lastName;
    private Boolean canWait;
    private Set<Ticket> tickets = new HashSet<>();
    Set<KidInPlaysite> kidInPlaysite = new HashSet<>();

    public Kid() {
        this.id = System.identityHashCode(this);
    }

    public Kid(String firstName, String lastName, Boolean canWait) {
        this.id = System.identityHashCode(this);
        this.firstName = firstName;
        this.lastName = lastName;
        this.canWait = canWait;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public Set<Ticket> getValidTickets(LocalDateTime time) {
        Set<Ticket> allTickets = this.getTickets();
        Set<Ticket> validTickets = new HashSet<>();
        for (Ticket ticket : allTickets) {
            if (ticket.isValidAt(time)) {
                validTickets.add(ticket);
            }
        }
        return validTickets;
    }

    public Set<Ticket> getValidVipTickets(LocalDateTime time) {
        Set<Ticket> validTickets = this.getValidTickets(time);
        Set<Ticket> vipTickets = new HashSet<>();
        for (Ticket ticket : validTickets) {
            if (ticket.getNumberOfTimesVIP() > 0) {
                vipTickets.add(ticket);
            }
        }
        return vipTickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Boolean getCanWait() {
        return canWait;
    }

    public void setCanWait(Boolean canWait) {
        this.canWait = canWait;
    }

    public Set<KidInPlaysite> getKidInPlaysite() {
        return kidInPlaysite;
    }

    public void setKidInPlaysite(Set<KidInPlaysite> kidInPlaysite) {
        this.kidInPlaysite = kidInPlaysite;
    }

    public void getHistory() {
        Set<KidInPlaysite> kidInPlaysite = this.getKidInPlaysite();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        System.out.println(this.firstName);
        System.out.println("Start time\tEnd time\tTime spent");
        for (KidInPlaysite playsite : kidInPlaysite) {
            if (playsite.getStartTime() != null) {
                var startTime = playsite.getStartTime();

                if (playsite.getEndTime() == null) {
                    // If the kid has not left the playsite, kick him out at midnight TODO!!!
                    playsite.setEndTime(startTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusMinutes(1));
                }
                var timeSpent = playsite.getTimeSpent();
                System.out.println(playsite.getPlaysite().getType());
                System.out.print(startTime.format(myFormatObj));
                System.out.print('\t');
                System.out.print(playsite.getEndTime().format(myFormatObj));
                System.out.print('\t');
                System.out.print(timeSpent == null ? "" : timeSpent.toMinutes() + " min");
                System.out.print('\n');
            }
        }
    }

    @Override
    public String toString() {
        return "Kid{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kid kid = (Kid) o;

        return id != null ? id.equals(kid.id) : kid.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}