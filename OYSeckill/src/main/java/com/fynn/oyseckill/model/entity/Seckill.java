package com.fynn.oyseckill.model.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/7/4.
 */
public class Seckill extends BmobObject {

    private OysUser user;
    private Integer personTimes;
    private String ip;
    private List<Long> seckillNo;
    private Issue issue;
    private Long seckillAt;

    public OysUser getUser() {
        return user;
    }

    public void setUser(OysUser user) {
        this.user = user;
    }

    public Integer getPersonTimes() {
        return personTimes;
    }

    public void setPersonTimes(Integer personTimes) {
        this.personTimes = personTimes;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Long> getSeckillNo() {
        return seckillNo;
    }

    public void setSeckillNo(List<Long> seckillNo) {
        this.seckillNo = seckillNo;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Long getSeckillAt() {
        return seckillAt;
    }

    public void setSeckillAt(Long seckillAt) {
        this.seckillAt = seckillAt;
    }
}
