package com.example.application.data.entity;

import javax.persistence.Entity;

@Entity
public class DaftarKeluhan extends AbstractEntity {

    private String keluhan;

    public String getKeluhan() {
        return keluhan;
    }
    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

}
