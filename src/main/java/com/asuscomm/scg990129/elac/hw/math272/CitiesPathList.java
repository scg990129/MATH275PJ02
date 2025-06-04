package com.asuscomm.scg990129.elac.hw.math272;

import java.util.Collection;
import java.util.LinkedList;

public class CitiesPathList extends LinkedList<City> {
    private static final long serialVersionUID = 1L;

    protected City returnCity;

    public CitiesPathList() {super();}

    public CitiesPathList(Collection<? extends City> c) {
        super(c);
    }

    public CitiesPathList addCity(City city) {
        this.add(city);
        return this;
    }

    public double getTotalDistance() {
        double totalDistance = 0.0;
        City firstCity = null;
        for(City c: this) {
            if(firstCity == null){firstCity = c; continue;}
            totalDistance += City.distanceTo(firstCity, c);
            firstCity = c;
        }
        return totalDistance;
    }

    public double getTotalDistanceWithReturn() {
        return this.getTotalDistance() + City.distanceTo(this.getLast(), this.getFirst());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (City city : this) {
            sb.append(city.getLabel()).append(" ");
        }

        return sb.toString().trim();
    }
}
