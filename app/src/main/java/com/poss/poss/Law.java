package com.poss.poss;

public class Law {
    private int id;
    private String p;
    private String m;
    private String b;
    private String practice;
    private int fine;
    public Law( int newid, String province, String municipality, String barangay, String newPractice, int newFine) {
        id = newid;
        p = province;
        m = municipality;
        b = barangay;
        practice = newPractice;
        fine = newFine;
    }

    public String getProvidence() {
        return p;
    }

    public String getMunicipality() {
        return m;
    }

    public String getBarangay() {
        return b;
    }

    public int getId() {
        return id;
    }

    public String getProvince() {
        return p;
    }

    public String getPractice() {
        return practice;
    }

    public int getFine() {
        return fine;
    }
}