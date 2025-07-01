package com.naufal222102523.login;

import com.google.gson.annotations.SerializedName;

public class CuacaCityModel {
    private long sunrise, sunset;
    private String name;
    @SerializedName("coord")
    private CuacaCoordModel cuacaCoordModel;

    public CuacaCityModel(){
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CuacaCoordModel getCoordModel() {
        return cuacaCoordModel;
    }

    public void setCuacaCoordModel(CuacaCoordModel cuacaCoordModel) {
        this.cuacaCoordModel = cuacaCoordModel;
    }
}
