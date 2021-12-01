package com.example.playgroundmanager.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Playsite {

    public Integer id;
    private PlaysiteType type;
    private Integer maxAllowedKidCount;
    private Integer minRequiredKidCount;
    private Integer currentKidCount;
    private Queue<Kid> mainQueue = new LinkedList<>();
    private Queue<Kid> vipQueue = new LinkedList<>();
    private Integer nonVipCounter = 0;
    private final Integer nonVipThreshold = 3;
    private Boolean letVip = true;
    Set<KidInPlaysite> kidInPlaysite = new HashSet<>();

    public Playsite() {
        this.id = System.identityHashCode(this);
    }

    public Playsite(PlaysiteType type, Integer maxAllowedKidCount, Integer minRequiredKidCount) {
        this.type = type;
        this.maxAllowedKidCount = maxAllowedKidCount;
        this.minRequiredKidCount = minRequiredKidCount;
        this.currentKidCount = 0;
    }

    public PlaysiteType getType() {
        return type;
    }

    public void setType(PlaysiteType type) {
        this.type = type;
    }

    public Integer getMaxAllowedKidCount() {
        return maxAllowedKidCount;
    }

    public void setMaxAllowedKidCount(Integer maxAllowedKidCount) {
        this.maxAllowedKidCount = maxAllowedKidCount;
    }

    public Integer getCurrentKidCount() {
        return currentKidCount;
    }

    public void setCurrentKidCount(Integer currentKidCount) {
        this.currentKidCount = currentKidCount;
    }

    public Integer getMinRequiredKidCount() {
        return minRequiredKidCount;
    }

    public void setMinRequiredKidCount(Integer minRequiredKidCount) {
        this.minRequiredKidCount = minRequiredKidCount;
    }

    public Queue<Kid> getMainQueue() {
        return mainQueue;
    }

    public void setMainQueue(Queue<Kid> mainQueue) {
        this.mainQueue = mainQueue;
    }

    public Queue<Kid> getVipQueue() {
        return vipQueue;
    }

    public void setVipQueue(Queue<Kid> vipQueue) {
        this.vipQueue = vipQueue;
    }

    public Set<KidInPlaysite> getKidInPlaysite() {
        return kidInPlaysite;
    }

    public void setKidInPlaysite(Set<KidInPlaysite> kidInPlaysite) {
        this.kidInPlaysite = kidInPlaysite;
    }

    public void attendBy(Kid kid) throws InterruptedException { // TODO: different times can be used!!!
        if (kid.getValidTickets(LocalDateTime.now()).size() == 0) {
            System.out.println(kid.getFirstName() + " has no valid ticket so won't be let in playsite " + this.type);
            return;
        }
        TimeUnit.SECONDS.sleep(1); //TODO fix this to remove wait?
        if (this.currentKidCount < this.maxAllowedKidCount) {
            KidInPlaysite kidInPlaysite = new KidInPlaysite();
            kidInPlaysite.setKid(kid);
            kid.getKidInPlaysite().add(kidInPlaysite);
            kidInPlaysite.setPlaysite(this);
            this.kidInPlaysite.add(kidInPlaysite);
            kidInPlaysite.setStartTime(LocalDateTime.now());
            this.currentKidCount += 1;
        } else {
            if (kid.getCanWait()) {
                if (kid.getValidVipTickets(LocalDateTime.now()).size() > 0) {
                    this.addKidToVipQueue(kid);
                } else {
                    this.addKidToMainQueue(kid);
                }
            }
        }
    }

    public void addKidToMainQueue(Kid kid) {
        this.mainQueue.add(kid);
    }

    public void addKidToVipQueue(Kid kid) {
        this.vipQueue.add(kid);
    }

    public void leaveBy(Kid kid) throws InterruptedException { //TODO error messige when trying to leave with a kid that is not in playsite!!
        TimeUnit.SECONDS.sleep(1);
        // Kid leaves the playsite
        KidInPlaysite kidInPlaysite = this.getKidInPlaysite().stream()
                .filter(row -> row.getKid() == kid && row.getPlaysite() == this && row.getEndTime() == null)
                .findFirst()
                .get(); //// TODO optional get!!!!
        kidInPlaysite.setEndTime(LocalDateTime.now());
        this.currentKidCount -= 1;

        // New kid comes to playsite (if there is a queue)
        if (this.currentKidCount < this.maxAllowedKidCount) {
            if (letVip && !vipQueue.isEmpty()) {
                Kid nextKid = vipQueue.poll();
                this.attendBy(nextKid);
                this.letVip = false;
                this.nonVipCounter = 0;
                var ticket = nextKid.getValidVipTickets(LocalDateTime.now()).stream()
                        .findFirst()
                        .get(); // TODO: different times can be used!!!
                ticket.setNumberOfTimesVIP(ticket.getNumberOfTimesVIP() - 1);
            }
            else if (!mainQueue.isEmpty()) {
                if (!this.letVip && !vipQueue.isEmpty()) {
                    this.nonVipCounter += 1;
                    if (this.nonVipCounter >= this.nonVipThreshold) {
                        this.letVip = true;
                    }
                }
                Kid nextKid = mainQueue.poll();
                this.attendBy(nextKid);
            }
        }
    }

    public void getTotalVisitorCountPerDay() {
        Map<LocalDate, Long> dateToSumMap = this.getKidInPlaysite().stream()
                        .collect(Collectors.groupingBy(d -> d.getStartTime().toLocalDate(), Collectors.counting()));

        // iterate the map to get the output
        System.out.println(this.getType());
        dateToSumMap.forEach((k, v) -> {
            System.out.println("Date = " + k + " , Total visitor count = " + v);
        });
    }

    @Override
    public String toString() { //TODO update toString methods!!
        return "Playsite{" +
                "id=" + id +
                ", type=" + type +
                ", maxAllowedKidCount=" + maxAllowedKidCount +
                ", kidInPlaysite=" + kidInPlaysite +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Playsite playsite = (Playsite) o;

        return id != null ? id.equals(playsite.id) : playsite.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
