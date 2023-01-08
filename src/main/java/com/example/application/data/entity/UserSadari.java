package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class UserSadari extends AbstractEntity {

    private String nik;
    private String nama;
    @Email
    private String email;
    private String nomor_HP;
    private LocalDate tanggal_Lahir;
    private String pendidikan;
    private String faskes_Terdekat;
    private String alamat;
    private boolean status;

    public String getNik() {
        return nik;
    }
    public void setNik(String nik) {
        this.nik = nik;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNomor_HP() {
        return nomor_HP;
    }
    public void setNomor_HP(String nomor_HP) {
        this.nomor_HP = nomor_HP;
    }
    public LocalDate getTanggal_Lahir() {
        return tanggal_Lahir;
    }
    public void setTanggal_Lahir(LocalDate tanggal_Lahir) {
        this.tanggal_Lahir = tanggal_Lahir;
    }
    public String getPendidikan() {
        return pendidikan;
    }
    public void setPendidikan(String pendidikan) {
        this.pendidikan = pendidikan;
    }
    public String getFaskes_Terdekat() {
        return faskes_Terdekat;
    }
    public void setFaskes_Terdekat(String faskes_Terdekat) {
        this.faskes_Terdekat = faskes_Terdekat;
    }
    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

}
