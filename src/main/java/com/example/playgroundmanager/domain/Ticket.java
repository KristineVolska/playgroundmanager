package com.example.playgroundmanager.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Ticket {

    private final Integer id;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer numberOfTimesVIP = 0;
    private TicketType type = TicketType.INCLUDE_ALL;
    private Set<PlaysiteType> playsiteTypes = new HashSet<>();
    private Kid kid;

    public Ticket() {
        this.id = System.identityHashCode(this);
    }

    public Kid getKid() {
        return kid;
    }

    public void setKid(Kid kid) {
        this.kid = kid;
    }

    public Ticket(LocalDateTime validFrom, LocalDateTime validTo, Integer numberOfTimesVIP, TicketType type) {
        this.id = System.identityHashCode(this);
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.numberOfTimesVIP = numberOfTimesVIP;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public Boolean isValidAt(LocalDateTime time) {
       return this.getValidFrom().isBefore(time) && this.getValidTo().isAfter(time);
    }

    public Integer getNumberOfTimesVIP() {
        return numberOfTimesVIP;
    }

    public void setNumberOfTimesVIP(Integer numberOfTimesVIP) {
        this.numberOfTimesVIP = numberOfTimesVIP;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public Set<PlaysiteType> getPlaysiteTypes() {
        return playsiteTypes;
    }

    public void setPlaysiteTypes(Set<PlaysiteType> playsiteTypes) {
        this.playsiteTypes = playsiteTypes;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", numberOfTimesVIP=" + numberOfTimesVIP +
                ", type=" + type +
                ", playsiteTypes=" + playsiteTypes +
                ", kid=" + kid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        return id != null ? id.equals(ticket.id) : ticket.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}