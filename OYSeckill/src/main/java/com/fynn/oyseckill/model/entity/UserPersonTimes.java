package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by fynn on 16/7/17.
 */
public class UserPersonTimes extends BmobObject {

    private Issue issue;
    private OysUser user;
    private Long personTimes;
    private String issueId;

    public Issue getIssue() {
        return issue;
    }

    public UserPersonTimes setIssue(Issue issue) {
        this.issue = issue;
        return this;
    }

    public OysUser getUser() {
        return user;
    }

    public UserPersonTimes setUser(OysUser user) {
        this.user = user;
        return this;
    }

    public Long getPersonTimes() {
        return personTimes;
    }

    public UserPersonTimes setPersonTimes(Long personTimes) {
        this.personTimes = personTimes;
        return this;
    }

    public String getIssueId() {
        return issueId;
    }

    public UserPersonTimes setIssueId(String issueId) {
        this.issueId = issueId;
        return this;
    }
}
