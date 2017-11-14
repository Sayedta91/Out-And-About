package com.example.tariq.outandabout.Model;


public class GetFavourites {

    private int id;
    private String name, tit;
    private Double srclat, srclng,deslng,deslat;


    public GetFavourites(int id, String name, String tit, double srclat,
                         double srclng, double deslat, double deslan) {
        super();
        this.id = id;
        this.tit = tit;
        this.name = name;
        this.srclat = srclat;
        this.srclng = srclng;

        this.deslat = deslat;
        this.deslng = deslan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return tit;
    }

    public double getsrclat() {
        return srclat;
    }

    public double getsrclng() {
        return srclng;
    }

    public double getdeslat() {
        return deslat;
    }

    public double getdeslng() {
        return deslng;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GetFavourites other = (GetFavourites) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Favourite [id=" + id + ", name=" + name + "]";
    }
}

