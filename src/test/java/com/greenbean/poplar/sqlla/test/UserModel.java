package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.entity.PrimaryKey;
import com.greenbean.poplar.sqlla.entity.SqllaEntity;

import java.util.Date;

/**
 * Created by chrisding on 2017/6/10.
 * Function: CornerServer
 */
@SqllaEntity("select * from t_user where id=?")
public class UserModel {


    /**
     * id : 1
     * username : 123
     * nickname : 王力宏
     * gender : 0
     * birthdate : null
     * avatar : -1
     * poster : -1
     * love_words : 相爱不易
     * follower_count : 0
     * my_follow_count : 0
     * active_level : 0
     * last_active_time : 2017-06-11 12:40:35
     * total_view_count : 0
     * total_like_count : 0
     * location_pos_x : -1
     * location_pos_y : -1
     * location_str : null
     */

    @PrimaryKey
    private long id;
    private String username;
    private String nickname;
    private int gender;
    private Date birthdate;

    private String love_words;
    private int follower_count;
    private int my_follow_count;
    private int active_level;
    private Date last_active_time;
    private int total_view_count;
    private int total_like_count;
    private double location_pos_x;
    private double location_pos_y;
    private String location_str;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getLove_words() {
        return love_words;
    }

    public void setLove_words(String love_words) {
        this.love_words = love_words;
    }

    public int getFollower_count() {
        return follower_count;
    }

    public void setFollower_count(int follower_count) {
        this.follower_count = follower_count;
    }

    public int getMy_follow_count() {
        return my_follow_count;
    }

    public void setMy_follow_count(int my_follow_count) {
        this.my_follow_count = my_follow_count;
    }

    public int getActive_level() {
        return active_level;
    }

    public void setActive_level(int active_level) {
        this.active_level = active_level;
    }

    public int getTotal_view_count() {
        return total_view_count;
    }

    public void setTotal_view_count(int total_view_count) {
        this.total_view_count = total_view_count;
    }

    public int getTotal_like_count() {
        return total_like_count;
    }

    public void setTotal_like_count(int total_like_count) {
        this.total_like_count = total_like_count;
    }

    public double getLocation_pos_x() {
        return location_pos_x;
    }

    public void setLocation_pos_x(double location_pos_x) {
        this.location_pos_x = location_pos_x;
    }

    public double getLocation_pos_y() {
        return location_pos_y;
    }

    public void setLocation_pos_y(double location_pos_y) {
        this.location_pos_y = location_pos_y;
    }

    public String getLocation_str() {
        return location_str;
    }

    public void setLocation_str(String location_str) {
        this.location_str = location_str;
    }

    public Date getLast_active_time() {
        return last_active_time;
    }

    public void setLast_active_time(Date last_active_time) {
        this.last_active_time = last_active_time;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", birthdate=" + birthdate +
                ", love_words='" + love_words + '\'' +
                ", follower_count=" + follower_count +
                ", my_follow_count=" + my_follow_count +
                ", active_level=" + active_level +
                ", last_active_time=" + last_active_time +
                ", total_view_count=" + total_view_count +
                ", total_like_count=" + total_like_count +
                ", location_pos_x=" + location_pos_x +
                ", location_pos_y=" + location_pos_y +
                ", location_str='" + location_str + '\'' +
                '}';
    }
}
