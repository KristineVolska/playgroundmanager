package com.example.playgroundmanager.domain;

import java.time.LocalDateTime;
import java.util.*;

public class Playsite {

    public Integer id;
    private PlaysiteType type;
    private Integer maxAllowedKidCount;
    private Integer minRequiredKidCount;
    private Integer currentKidCount;
    private Queue<Kid> mainQueue = new LinkedList<>();
    private Queue<Kid> vipQueue = new LinkedList<>();
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

    public void attendBy(Kid kid){
        if (this.currentKidCount < this.maxAllowedKidCount) {
            KidInPlaysite kidInPlaysite = new KidInPlaysite();
            kidInPlaysite.setKid(kid);
            kid.getKidInPlaysite().add(kidInPlaysite);
            kidInPlaysite.setPlaysite(this);
            this.kidInPlaysite.add(kidInPlaysite);
            kidInPlaysite.setStartTime(LocalDateTime.now());
            this.currentKidCount += 1;
        } else {
            this.addKidToMainQueue(kid);
        }
    }

    public void addKidToMainQueue(Kid kid) {
        this.mainQueue.add(kid);
    }

    public void leaveBy(Kid kid){
        // Kid leaves the playsite
        KidInPlaysite kidInPlaysite = this.getKidInPlaysite().stream()
                .filter(row -> row.getKid() == kid && row.getPlaysite() == this && row.getEndTime() == null)
                .findFirst()
                .get(); //// TODO optional get!!!!
        kidInPlaysite.setEndTime(LocalDateTime.now());
        this.currentKidCount -= 1;

        // New kid comes to playsite (if there is a queue)
        if (this.currentKidCount < this.maxAllowedKidCount) {
            if (!mainQueue.isEmpty()) {
                Kid nextKid = mainQueue.poll();
                this.attendBy(nextKid);
            }
        }
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
