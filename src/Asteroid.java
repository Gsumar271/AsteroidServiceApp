package com.asteroidserviceapp;


public class Asteroid {

    private String name;
    private int magnitude;
    private double diameter;
    private int miss_distance;

    public Asteroid(String _name){
        name = _name;
        magnitude = 0;
        diameter = 0.0;
        miss_distance = 0;

    }

    public Asteroid(String _name, int _mag, double _diameter, int _miss_dist){

        name = _name;
        magnitude = _mag;
        diameter = _diameter;
        miss_distance = _miss_dist;

    }

    public int getMiss_distance() {
        return miss_distance;
    }

    public void setMiss_distance(int miss_distance) {
        this.miss_distance = miss_distance;
    }

    public double getDiameter() {

        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public int getMagnitude() {

        return magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
