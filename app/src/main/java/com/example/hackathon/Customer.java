package com.example.hackathon;
import com.google.firebase.Timestamp;


public class Customer {
    private String fullName;
    private String phoneNumber; //Unique ID
    private String area;
    private String city;
    private String state;
    private String pinCode;
    private String landmark;
    private String uid;
    private Timestamp registrationDate;
    private String email;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Customer() {
    }

    public Customer(String phoneNumber, String fullName, String area, String city, String state, String pinCode, String landmark, String uid, String email,Timestamp registrationDate) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.area = area;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
        this.landmark = landmark;
        this.uid = uid;
        this.email = email;
        this.registrationDate=registrationDate;
    }
}


