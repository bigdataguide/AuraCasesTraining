package com.aura.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Wd implements Serializable {

    @JsonProperty("t") private String t;

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }
}
