package com.example.playgroundmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
    private final Integer nonVipThreshold = 3; // Should be stored as a global app property
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

    /**
     * Method checks if a kid that is willing to attend the playsite can do so.
     * Playsite regulations are checked, kid's ticket as well.
     * @param kid Kid the kid that wants to attend the playsite
     */
    public void attendBy(Kid kid) {
        if (kid.getValidTickets(LocalDateTime.now()).size() == 0) {
            System.out.printf("%1$s has no valid ticket so won't be let in playsite %2$s\n", kid.getFirstName(), this.type);
            return;
        }
        if (this.currentKidCount < this.maxAllowedKidCount && this.currentKidCount + 1 >= this.minRequiredKidCount) {
            this.letKidIn(kid);
        } else {
            if (this.lastKidNecessaryToStart()) {
                this.addKidToQueue(kid);
                Kid nextKid = new Kid();
                while ((this.mainQueue.size() + this.vipQueue.size()) > 0) {
                    nextKid = this.getNextKidFromQueue();
                    this.letKidIn(nextKid);
                }
            } else {
                this.addKidToQueue(kid);
            }
        }
    }

    /**
     * Method checks if only one kid is still necessary to get the min kid count for the playsite
     * @return boolean
     */
    public boolean lastKidNecessaryToStart() {
        return this.mainQueue.size() + this.vipQueue.size() == this.minRequiredKidCount - 1 && this.currentKidCount == 0;
    }

    /**
     * Method to create the necessary relationships between kid and playsite instances
     * when the kid attends the playsite.
     * @param kid Kid that attends the playsite
     */
    public void letKidIn(Kid kid) {
        KidInPlaysite kidInPlaysite = new KidInPlaysite();
        kidInPlaysite.setKid(kid);
        kid.getKidInPlaysite().add(kidInPlaysite);
        kidInPlaysite.setPlaysite(this);
        this.kidInPlaysite.add(kidInPlaysite);
        kidInPlaysite.setStartTime(LocalDateTime.now());
        this.currentKidCount += 1;
    }

    /**
     * Method to add a kid to the queue for attending the playsite
     * @param kid Kid that attends the playsite
     */
    public void addKidToQueue(Kid kid) {
        // lastKidNecessaryToStart() here is for one exception: when only one kid is missing to get minRequiredKidCount to let all kids in
        // In this case it is not important if the Kid can or cannot wait -
        // the kid will get in the playsite immediately with the whole group. Only the correct order is maintained
        if (kid.getCanWait() || this.lastKidNecessaryToStart()) { // If kid does not want to wait, he won't join the queue and leave
            if (kid.getValidVipTickets(LocalDateTime.now()).size() > 0) {
                this.addKidToVipQueue(kid);
            } else {
                this.addKidToMainQueue(kid);
            }
        }
    }

    public void addKidToMainQueue(Kid kid) {
        this.mainQueue.add(kid);
    }

    public void addKidToVipQueue(Kid kid) {
        this.vipQueue.add(kid);
    }

    /**
     * Method to remove a kid from the playsite when the kid decides to leave
     * @param kid Kid that is at the playsite
     */
    public void leaveBy(Kid kid) {
        // Kid leaves the playsite
        KidInPlaysite kidInPlaysite = this.getKidInPlaysite().stream()
                .filter(row -> row.getKid() == kid && row.getPlaysite() == this && row.getEndTime() == null)
                .findFirst()
                .orElse(null);
        if (kidInPlaysite != null) {
            kidInPlaysite.setEndTime(LocalDateTime.now());
            this.currentKidCount -= 1;

            // If kid leaving the playsite causes currentKidCount to fall below the minRequiredKidCount
            // and there is no one in the queue who can replace him, all kids in the playsite must leave and join the queue
            if (this.currentKidCount < this.minRequiredKidCount && !this.isAnyKidInAnyQueue()) {
                var setOfKidsWhoMustLeave = this.getKidsWhoMustLeave();
                for (var nextKid : setOfKidsWhoMustLeave) {
                    this.attendBy(nextKid);
                }
            }
            // New kid comes to playsite (if there is a queue)
            else if (this.currentKidCount < this.maxAllowedKidCount && this.isAnyKidInAnyQueue()) {
                Kid nextKid = this.getNextKidFromQueue();
                this.attendBy(nextKid);
            }
        }
    }

    /**
     * Method to get all the kids that are currently at playsite and need to leave
     * (because of one kid leaving and thus the min required kid count rule is violated)
     * @return Set<Kid> set of kids who must leave the playsite
     */
    public Set<Kid> getKidsWhoMustLeave() {
        var kidsInPlaysite = this.getKidInPlaysite().stream()
                .filter(row -> row.getEndTime() == null)
                .toArray(KidInPlaysite[]::new);

        Set<Kid> setOfKidsWhoMustLeave = new HashSet<>();

        for (var kidInPlaysite : kidsInPlaysite) {
            kidInPlaysite.setEndTime(LocalDateTime.now());
            this.currentKidCount--;
            setOfKidsWhoMustLeave.add(kidInPlaysite.getKid());
        }

        return setOfKidsWhoMustLeave;
    }

    public boolean isAnyKidInAnyQueue() {
        return this.mainQueue.size() + this.vipQueue.size() > 0;
    }

    /**
     * Method to get the next kid in the queue to playsite according to playgrounds rules
     * @return Kid - the next kid
     */
    public Kid getNextKidFromQueue() {
        Kid nextKid = new Kid();
        if (this.letVip && !this.vipQueue.isEmpty()) {
            nextKid = this.getNextVipKidFromQueue();
        }
        else if (!this.mainQueue.isEmpty()) {
            if (!this.letVip && !this.vipQueue.isEmpty()) {
                this.nonVipCounter += 1;
                if (this.nonVipCounter >= this.nonVipThreshold) {
                    this.letVip = true;
                }
            }
            nextKid = this.mainQueue.poll();
        } else if (!this.vipQueue.isEmpty()) {
            nextKid = this.getNextVipKidFromQueue();
        }
        return nextKid;
    }

    /**
     * Method to get the next VIP kid in the queue to playsite
     * @return Kid - the next VIP kid
     */
    public Kid getNextVipKidFromQueue() {
        Kid nextKid = new Kid();
        if (!this.vipQueue.isEmpty()) {
            nextKid = this.vipQueue.poll();
            this.updateVipStatus(nextKid);
        }
        return nextKid;
    }

    /**
     * Method to update VIP status of the kid (decrease the amount of times he can pass the queue)
     * @param nextKid - the kid whose ticket needs to be updated
     */
    public void updateVipStatus(Kid nextKid) {
        this.letVip = false;
        this.nonVipCounter = 0;
        nextKid.getValidVipTickets(LocalDateTime.now()).stream()
                .findFirst().ifPresent(ticket -> ticket.setNumberOfTimesVIP(ticket.getNumberOfTimesVIP() - 1));
    }

    /**
     * Method to provide total visitor count per day for the play site
     */
    public void getTotalVisitorCountPerDay() {
        Map<LocalDate, Long> dateToSumMap = this.getKidInPlaysite().stream()
                        .collect(Collectors.groupingBy(d -> d.getStartTime().toLocalDate(), Collectors.counting()));

        // iterate the map to get the output
        System.out.printf("%n%1$s%n", this.getType());
        dateToSumMap.forEach((k, v) -> {
            System.out.printf("Date = %1$s , Total visitor count = %2$s\n", k, v);
        });
    }

    /**
     * Method to provide utilization snapshots taken at defined times of the day for the play site
     * @param date LocalDate - the date of interest
     * @param startTime LocalTime - start taking snapshots from this point in time
     * @param endTime LocalTime - end taking snapshots at this point in time
     * @param timeIntervalInMinutes Integer - every this amount of min have passed since last snapshot (or startTime), a snapshot is taken
     */
    public void getUtilizationSnapshot(LocalDate date, LocalTime startTime, LocalTime endTime, Integer timeIntervalInMinutes) {
        List<LocalTime> timeList = new ArrayList<>();
        for (LocalTime i = startTime; i.isBefore(endTime) || i.equals(endTime); i = i.plusMinutes(timeIntervalInMinutes)) {
            timeList.add(i);
        }

        var kidsInPlaysite = this.getKidInPlaysite().stream()
                .filter(d -> d.getStartTime().toLocalDate().isEqual(date)).toArray(KidInPlaysite[]::new);
        int visitorCount;
        double utilization;
        System.out.printf("\n%1$s utilization on %2$s%n", this.type, date);

        for (var time : timeList) { // k = max 600 if working hours are e.g. 10.00-20.00 and 1 minute is the smallest interval
            visitorCount = 0;
            for (KidInPlaysite kidInPlaysite: kidsInPlaysite) { // n. Worst time complexity = k * n
                var start = kidInPlaysite.getStartTime().toLocalTime();
                var end = kidInPlaysite.getEndTime() == null ? LocalTime.now() :
                        kidInPlaysite.getEndTime().toLocalTime();

                if ((start.isBefore(time) || start.equals(time)) && (end.isAfter(time) || end.equals(time))) {
                    visitorCount++;
                }
            }
            utilization = (double)visitorCount / this.maxAllowedKidCount * 100.00;
            System.out.printf("%1$s: %2$s %%\n", time, utilization);
        }
    }

    @Override
    public String toString() {
        return "Playsite{" +
                "id=" + id +
                ", type=" + type +
                ", maxAllowedKidCount=" + maxAllowedKidCount +
                ", minRequiredKidCount=" + minRequiredKidCount +
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
