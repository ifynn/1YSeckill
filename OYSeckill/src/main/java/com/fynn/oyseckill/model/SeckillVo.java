package com.fynn.oyseckill.model;

import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.UserPersonTimes;

/**
 * Created by fynn on 16/7/21.
 */
public class SeckillVo {

    private Issue issue;
    private UserPersonTimes userPersonTimes;

    public Issue getIssue() {
        return issue;
    }

    public SeckillVo setIssue(Issue issue) {
        this.issue = issue;
        return this;
    }

    public UserPersonTimes getUserPersonTimes() {
        return userPersonTimes;
    }

    public SeckillVo setUserPersonTimes(UserPersonTimes userPersonTimes) {
        this.userPersonTimes = userPersonTimes;
        return this;
    }
}
