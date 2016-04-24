package com.poss.poss;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import android.database.sqlite.*;

public class DBManager {
    private final String _INITLAWS = "CREATE TABLE IF NOT EXISTS Laws(id INTEGER, province TEXT, municipality TEXT, barangay TEXT, monthStart INTEGER, monthEnd INTEGER, dayStart INTEGER, dayEnd INTEGER, yearStart INTEGER, yearEnd INTEGER, practice TEXT, fine INTEGER);";
    private final String _INITLOCS = "CREATE TABLE IF NOT EXISTS Locs(tripID INTEGER, y DOUBLE, x DOUBLE, minute INTEGER, hour INTEGER, day INTEGER, month INTEGER, year INTEGER);";
    private String pathString;
    private File db;
    private SQLiteDatabase htdb;
    public DBManager(Context context) {
        pathString = context.getFilesDir().getPath() + "/poss.sqlite";
        db = new File(pathString);
    }
    public void delete() {
        SQLiteDatabase.deleteDatabase(db);
    }
    public void clearTrips() {
        htdb.delete("Locs", null, null);
    }
    public void start() {
        htdb = SQLiteDatabase.openOrCreateDatabase(db, null);
        htdb.execSQL(_INITLAWS);
        htdb.execSQL(_INITLOCS);
    }
    public int newLaw(String province, String municipality, String barangay, int m1, int m2, int d1, int d2, int y1, int y2, String practice, int fine) {
        Random rand = new Random();
        int newid = rand.nextInt(10000000);
        Cursor checkidExists = null;
        while (checkidExists == null) {
            newid = rand.nextInt(10000000);
            checkidExists = htdb.rawQuery("Select * from Laws where id=" + newid, null);
        }
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues cvLaws = new ContentValues();
        cvLaws.put("id", newid);
        cvLaws.put("province", province);
        cvLaws.put("municipality", municipality);
        cvLaws.put("barangay", barangay);
        cvLaws.put("monthStart", m1);
        cvLaws.put("monthEnd", m2);
        cvLaws.put("dayStart", d1);
        cvLaws.put("dayEnd", d2);
        cvLaws.put("yearStart", y1);
        cvLaws.put("yearEnd", y2);
        cvLaws.put("practice", practice);
        cvLaws.put("fine", fine);
        htdb.insert("Laws", null, cvLaws);
        System.out.println("law recorded with id " + newid);
        return newid;
    }
    public void updateLaw(int lawid, String province, String municipality, String barangay, int m1, int m2, int d1, int d2, int y1, int y2, String practice, int fine) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        ContentValues cvLaws = new ContentValues();
        cvLaws.put("id", lawid);
        cvLaws.put("province", province);
        cvLaws.put("municipality", municipality);
        cvLaws.put("barangay", barangay);
        cvLaws.put("monthEnd", m2);
        cvLaws.put("dayStart", d1);
        cvLaws.put("dayEnd", d2);
        cvLaws.put("yearStart", y1);
        cvLaws.put("yearEnd", y2);
        cvLaws.put("practice", practice);
        cvLaws.put("fine", fine);
        htdb.insert("Laws", null, cvLaws);
    }
    public ArrayList<Law> searchLaws (String province, String municipality, String barangay, String date, String query) {
        ArrayList<Law> Laws = new ArrayList<>();
        String where = "";

        if (province != null) {
            where = where + "province like '" + province + "' ";
        }

        if (municipality != null) {
            if (province!=null) {
                where += "or ";
            }
            where = where + "municipality like '" + municipality + "' ";
        }

        if (barangay != null) {
            if (province!=null || municipality!=null) {
                where += "or ";
            }
            where = where + "barangay like '" + barangay + "' ";
        }
        if (date == null) {
            Calendar c = Calendar.getInstance();
            date = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
        }
        String[] pieces = date.split("/");
        if (province!=null || municipality!=null || barangay!=null) {
            where += "or ";
        }
        int d = Integer.parseInt(pieces[0]);
        int m = Integer.parseInt(pieces[1]);
        int y = Integer.parseInt(pieces[2]);
        where = where + "yearStart <= '" + y + "' ";
        where = where + "and " + "yearEnd >= '" + y + "' ";

        if (query != null) {
            if (province!=null || municipality!=null || barangay!=null || date!=null) {
                where += "or ";
            }
            where = where + "practice like '" + query + "' ";
        }
        Cursor c = htdb.query("Laws", null, where, null, null, null, null, null);
        DatabaseUtils.dumpCursor(c);
        Log.d("columnns ", String.valueOf(c.getColumnCount()));
        c.moveToFirst();
        if (c!=null) {
                do {
                    String provincec = c.getString(c.getColumnIndex("province"));
                    String municipalityc = c.getString(c.getColumnIndex("municipality"));
                    String barangayc = c.getString(c.getColumnIndex("barangay"));
                    int dayStart = c.getInt(c.getColumnIndex("dayStart"));
                    int dayEnd = c.getInt(c.getColumnIndex("dayEnd"));
                    int monthStart = c.getInt(c.getColumnIndex("monthStart"));
                    int monthEnd = c.getInt(c.getColumnIndex("monthEnd"));
                    int yearStart = c.getInt(c.getColumnIndex("yearStart"));
                    int yearEnd = c.getInt(c.getColumnIndex("yearEnd"));
                    int fine = c.getInt(c.getColumnIndex("fine"));
                    String practice = c.getString(c.getColumnIndex("practice"));
                    int id = c.getInt(c.getColumnIndex("id"));

                    if ((barangay!=null && barangay==barangayc) || (barangay==null && municipality!=null && municipalityc==municipality) || (barangay==null && municipality==null && province!=null && province==provincec) || (province==null && municipality==null && barangay==null)) {
                        if ((y==yearStart && m==monthStart && d>=dayStart) || (y==yearStart && m>monthStart) || (y>yearStart && y<yearEnd) || (y==yearEnd && m==monthEnd && d<=dayEnd) || (y==yearEnd && m<monthEnd)) {
                            Law law = new Law(id, province, municipality, barangay, practice, fine);
                            Laws.add(law);
                        }
                    }
                } while (c.moveToNext());
            c.close();
        }
        return Laws;
    }
    public int newTrip() {
        int newid = 0;
        Cursor checkidExists = null;
        do {
            checkidExists = htdb.rawQuery("Select * from Locs where tripID>" + newid++, null);
            Log.d("newid value:", String.valueOf(newid));
        } while (checkidExists.getCount()>0);
        return newid;
    }

    public void newLocation(int tripID, double y, double x, int minute, int hour, int day, int month, int year) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues cvLaws = new ContentValues();
        cvLaws.put("tripID", tripID);
        cvLaws.put("y", y);
        cvLaws.put("x", x);
        cvLaws.put("minute", minute);
        cvLaws.put("hour", hour);
        cvLaws.put("day", day);
        cvLaws.put("month", month);
        cvLaws.put("year", year);
        htdb.insert("Locs", null, cvLaws);
    }
    public ArrayList<TripPoint> searchTrip (int tID) {
        ArrayList<TripPoint> locs = new ArrayList<>();
        String where = "";
        where += "tripID like '" + tID + "'";
        Cursor c = htdb.query("Locs", null, where, null, null, null, null, null);
        DatabaseUtils.dumpCursor(c);
        Log.d("columnns ", String.valueOf(c.getColumnCount()));
        c.moveToFirst();
        if (c!=null) {
            do {
                int id = c.getInt(c.getColumnIndex("id"));
                double y = c.getDouble(c.getColumnIndex("y"));
                double x = c.getDouble(c.getColumnIndex("x"));
                int minute = c.getInt(c.getColumnIndex("minute"));
                int hour = c.getInt(c.getColumnIndex("hour"));
                int day = c.getInt(c.getColumnIndex("day"));
                int month = c.getInt(c.getColumnIndex("month"));
                int year = c.getInt(c.getColumnIndex("year"));
                TripPoint loc = new TripPoint(id, y, x, minute, hour, day, month, year);
                locs.add(loc);
            } while (c.moveToNext());
            c.close();
        }
        return locs;
    }
    public void close() {
        htdb.close();
    }

    public String[] shareTrip(int tripID) {
        Cursor c = htdb.query("Locs", null, "tripID like '" + tripID + "'", null, null, null, null, null);
        ArrayList<TripPoint> trip = searchTrip(tripID);
        double y0 = 0;
        double x0 = 0;
        double y = 0;
        double x = 0;
        double total = 0;
        int hourStart = 24;
        int minuteStart = 0;
        int hourEnd = 0;
        int minuteEnd = 0;
        if (c!=null) {
            do {
                y0 = y;
                x0 = x;
                y = c.getDouble(c.getColumnIndex("y"));
                x = c.getDouble(c.getColumnIndex("x"));
                int hour = c.getInt(c.getColumnIndex("hour"));
                int minute = c.getInt(c.getColumnIndex("minute"));
                if (hour <= hourStart) {
                    if (hour == hourStart && minute > minuteStart) {
                        minuteStart = minute;
                    }
                    hourStart = hour;
                }
                if (hour >= hourEnd) {
                    if (hour == hourEnd && minute < minuteStart) {
                        minuteStart = minute;
                    }
                    hourStart = hour;
                }
                if (x0!=0 || y0!=0){
                    double dy = y-y0;
                    double dx = x-x0;
                    total += Math.sqrt(dy*dy +dx*dx)*111.325;
                }
            } while (c.moveToNext());
            c.close();
        }
        int hours = hourEnd-hourStart;
        String minutes = "";
        if (minuteEnd==minuteStart) {
            minutes = "00";
        } else if (minuteEnd<minuteStart){
            hours=hours-1;
            minutes = 60 - (minuteStart-minuteEnd) + "";
        } else {
            if ((minuteEnd-minuteStart)<10) {
                minutes = "0" + (minuteEnd-minuteStart);
            } else {
                minutes = minuteEnd-minuteStart + "";
            }
        }
        String points = "";
        for (TripPoint point : trip) {
            points += "[" + point.getY() + "," + point.getX() + "," + point.getHour() + "," + point.getMinute() + "]";
        }
        String[] toReturn = new String[3];
        toReturn[0] = "Trip Length: " + total;
        toReturn[1] = "Trip Duration: " + hours + ":" + minutes;
        toReturn[2] = "Points: " + points;


        return toReturn;
    }

}