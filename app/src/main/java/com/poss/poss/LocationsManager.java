package com.poss.poss;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import android.database.sqlite.*;

public class LocationsManager {
    private final String _INITLOCS = "CREATE TABLE IF NOT EXISTS Locs(id INTEGER, tripID INTEGER, y DOUBLE, x DOUBLE, minute INTEGER, hour INTEGER, day INTEGER, month INTEGER, year INTEGER);";
    private String pathString;
    private int tripID;
    private File db;
    private SQLiteDatabase htdb;
    public LocationsManager(Context context) {
        pathString = context.getFilesDir().getPath() + "/locs.sqlite";
        db = new File(pathString);
    }
    public void delete() {
        htdb.deleteDatabase(db);
    }
    public void start() {
        htdb = SQLiteDatabase.openOrCreateDatabase(db, null);
        htdb.execSQL(_INITLOCS);
    }
    public void startTrip(Context context) {
        tripID++;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double y = location.getLatitude();
                double x = location.getLongitude();
                Calendar c = Calendar.getInstance();
                int minute = c.get(Calendar.MINUTE);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                int id = newLocation(y,x,minute,hour,day,month,year);
            }
            @Override
            public void onStatusChanged(String lols,int lol, Bundle lolol) {};

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }
    public void stopTrip() {

    }
    public int newLocation(double y, double x, int minute, int hour, int day, int month, int year) {
        int newid = 0;
        Cursor checkidExists = null;
        do {
            checkidExists = htdb.rawQuery("Select * from Laws where id>" + newid++, null);
        } while (checkidExists != null);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues cvLaws = new ContentValues();
        cvLaws.put("id", newid);
        cvLaws.put("tripID", tripID);
        cvLaws.put("y", y);
        cvLaws.put("x", x);
        cvLaws.put("minute", minute);
        cvLaws.put("hour", hour);
        cvLaws.put("day", day);
        cvLaws.put("month", month);
        cvLaws.put("year", year);
        htdb.insert("Locs", null, cvLaws);
        return newid;
    }
    public ArrayList<Location> searchTrip (int tID) {
        ArrayList<Location> locs = new ArrayList<>();
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
                    Location loc = new Location(id, y, x, minute, hour, day, month, year);
                    locs.add(loc);
                } while (c.moveToNext());
            c.close();
        }
        return locs;
    }
    public void close() {
        htdb.close();
    }
}