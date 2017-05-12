package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Fynn on 2016/7/4.
 */
public class Issue extends BmobObject {

    private Long issueNumber;
    private Product product;
    private Seckill succeedSeckill;
    private Long succeedSeckillNo;
    private UserPersonTimes luckUserPt;
    private BmobDate announcedAt;   //揭晓时间
    private BmobDate finishedAt;    //结束秒杀时间
    private Long personTimes;
    private Integer announceState;
    private Long totalPersonTimes;
    private String otherNo;

    public Issue() {

    }

    public Issue(String objectId) {
        setObjectId(objectId);
    }

    public Long getIssueNumber() {
        return issueNumber;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Seckill getSucceedSeckill() {
        return succeedSeckill;
    }

    public void setSucceedSeckill(Seckill succeedSeckill) {
        this.succeedSeckill = succeedSeckill;
    }

    public BmobDate getAnnouncedAt() {
        return announcedAt;
    }

    public void setAnnouncedAt(BmobDate announcedAt) {
        this.announcedAt = announcedAt;
    }

    public Long getSucceedSeckillNo() {
        return succeedSeckillNo;
    }

    public void setSucceedSeckillNo(Long succeedSeckillNo) {
        this.succeedSeckillNo = succeedSeckillNo;
    }

    public Long getPersonTimes() {
        return personTimes;
    }

    public void setPersonTimes(Long personTimes) {
        this.personTimes = personTimes;
    }

    public Integer getAnnounceState() {
        return announceState;
    }

    public void setAnnounceState(Integer announceState) {
        this.announceState = announceState;
    }

    public UserPersonTimes getLuckUserPt() {
        return luckUserPt;
    }

    public Issue setLuckUserPt(UserPersonTimes luckUserPt) {
        this.luckUserPt = luckUserPt;
        return this;
    }

    public Long getTotalPersonTimes() {
        return totalPersonTimes;
    }

    public void setTotalPersonTimes(Long totalPersonTimes) {
        this.totalPersonTimes = totalPersonTimes;
    }

    public String getOtherNo() {
        return otherNo;
    }

    public void setOtherNo(String otherNo) {
        this.otherNo = otherNo;
    }

    public BmobDate getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(BmobDate finishedAt) {
        this.finishedAt = finishedAt;
    }
}
