package com.example.smarthomeapp.Module;

public class ResDHT11 {
    float nhietdo, doam;

    public ResDHT11(float doam, float nhietdo) {
        this.nhietdo = nhietdo;
        this.doam = doam;
    }

    public ResDHT11() {
        this.nhietdo = 0;
        this.doam = 0;
    }

    public float getNhietdo() {
        return nhietdo;
    }

    public void setNhietdo(float nhietdo) {
        this.nhietdo = nhietdo;
    }

    public float getDoam() {
        return doam;
    }

    public void setDoam(float doam) {
        this.doam = doam;
    }
}
