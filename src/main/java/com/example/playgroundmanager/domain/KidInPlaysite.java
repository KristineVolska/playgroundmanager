package com.example.playgroundmanager.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public class KidInPlaysite {

    private final Integer id;
    private Kid kid;
    private Playsite playsite;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration timeSpent;

    public KidInPlaysite() {
        this.id = System.identityHashCode(this);
    }

    public Kid getKid() {
        return kid;
    }

    public void setKid(Kid kid) {
        this.kid = kid;
    }

    public Playsite getPlaysite() {
        return playsite;
    }

    public void setPlaysite(Playsite playsite) {
        this.playsite = playsite;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        if(this.endTime != null) {
            this._setTimeSpent(Duration.between(this.startTime, this.endTime));
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if(this.startTime != null) {
            this._setTimeSpent(Duration.between(this.startTime, this.endTime));
        }
    }

    public Duration getTimeSpent() {
        return timeSpent;
    }

    private void _setTimeSpent(Duration timeSpent) {
        this.timeSpent = timeSpent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KidInPlaysite that = (KidInPlaysite) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
