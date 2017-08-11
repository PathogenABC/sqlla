package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.entity.ColumnAlias;
import com.greenbean.poplar.sqlla.entity.Include;
import com.greenbean.poplar.sqlla.entity.SqllaEntity;

import java.util.Date;

/**
 * Created by chrisding on 2017/6/13.
 * Function: corner
 */
@SqllaEntity
public class LikeUserModel {

    private long id;

    @Include
    @ColumnAlias("operate_user")
    private UserModel operateUser;

    @Include
    @ColumnAlias("target_user")
    private UserModel targetUser;

    @ColumnAlias("operate_time")
    private Date operateTime;

    private boolean revoked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public UserModel getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(UserModel operateUser) {
        this.operateUser = operateUser;
    }

    public UserModel getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserModel targetUser) {
        this.targetUser = targetUser;
    }

    @Override
    public String toString() {
        return "LikeUserModel{" +
                "id=" + id +
                ", operateUser=" + operateUser +
                ", targetUser=" + targetUser +
                ", operateTime=" + operateTime +
                ", revoked=" + revoked +
                '}';
    }
}
