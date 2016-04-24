package com.poss.poss;
class TripPoint {
    private int id;
    private double y;
    private double x;
    private int minute;
    private int hour;
    private int day;
    private int month;
    private int year;
    public TripPoint(int newID, double newY, double newX, int newMinute, int newHour, int newDay, int newMonth, int newYear) {
        id = newID;
        y = newY;
        x = newX;
        minute = newMinute;
        hour = newHour;
        day = newDay;
        month = newMonth;
        year = newYear;
    }

    public int getID() {
        return id;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}