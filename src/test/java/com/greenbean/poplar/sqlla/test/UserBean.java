package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.entity.SqllaEntity;

import java.util.Date;

/**
 * Created by chrisding on 2017/5/10.
 * Function: NULL
 */
@SqllaEntity
public class UserBean {

    private String uid;
    private String phone;
    private String name;
    private int gender;
    private Date birthdate;
    private float score;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "{uid:'" + uid + "', name:'" + name + "', phone:'" + phone + "', gender:" + gender
                + ", birthdate:'" + String.valueOf(birthdate) + "', score:" + score + "}";
    }
}
