package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.Sql;
import com.greenbean.poplar.sqlla.SqllarException;

import java.util.Date;
import java.util.List;

/**
 * Created by chrisding on 2017/6/17.
 * Function: NULL
 */
public interface ModelApi {

    @Sql("select * from t_like_user_record where target_user=? and operate_time<? order by operate_time desc limit ?")
    List<LikeUserModel> getLikeRecordListByTargetUser(long targetUserId, Date backwardsDatetime, int limit) throws SqllarException;

}
