package org.mainPackage;

import java.util.ArrayList;

public class Class {
    private final String sln;
    private final String section;
    private final String type;
    private final ArrayList<String> timeSlot;
    private final String status;
    private final int enrolled;
    private final int capacity;
    private final int fee;
    private final String additionalInfo;

    Class(String sln, String section, String type, ArrayList<String> timeSlot, String status, int enrolled, int capacity, int fee, String additionalInfo) {
        if (sln.contains(">")) {
            this.sln = sln.substring(1);
        } else {
            this.sln = sln;
        }
        this.section = section;
        this.type = type;
        this.timeSlot = timeSlot;
        this.status = status;
        this.enrolled = enrolled;
        this.capacity = capacity;
        this.fee = fee;
        this.additionalInfo = additionalInfo;
    }

    // Getters
    public String getSln() { return sln; }
    public String getSection() { return section; }
    public String getType() { return type; }
    public ArrayList<String> getTimeSlot() { return timeSlot; }
    public String getStatus() { return status; }
    public int getEnrolled() { return enrolled; }
    public int getCapacity() { return capacity; }
    public int getFee() { return fee; }
    public String getAdditionalInfo() { return additionalInfo; }

    public boolean isNotFull() {
        return enrolled != capacity;
    }

    public String toString() {
        return "sln='" + sln + '\'' +
                ", section='" + section + '\'' +
                ", type='" + type + '\'' +
                ", dayTime='" + timeSlot.toString() + '\'' +
                ", status='" + status + '\'' +
                ", enrolled=" + enrolled +
                ", capacity=" + capacity +
                ", fee=" + fee;
    }
}