package com.dmwm.tunitrip.models;

import android.net.Uri;

public class ModelUser {

    String name,Region,bio,Uid,home,phone,email,type,gender,
     photoProfile,
            photoCover;

    public ModelUser() {

    }

    public ModelUser(String name, String region, String bio, String uid, String home, String phone, String email,String type,String gender, String photoProfile, String photoCover) {
        this.name = name;
        Region = region;
        this.bio = bio;
        Uid = uid;
        this.home = home;
        this.phone = phone;
        this.email = email;
        this.photoProfile = photoProfile;
        this.photoCover = photoCover;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getPhotoCover() {
        return photoCover;
    }

    public void setPhotoCover(String photoCover) {
        this.photoCover = photoCover;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }




}
