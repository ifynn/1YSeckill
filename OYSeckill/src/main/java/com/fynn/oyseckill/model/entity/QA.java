package com.fynn.oyseckill.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/7/22.
 */
public class QA extends BmobObject {

    private String question;
    private String answer;
    private Boolean isVisibleToUser;
    private Integer qaId;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Boolean getVisibleToUser() {
        return isVisibleToUser;
    }

    public void setVisibleToUser(Boolean visibleToUser) {
        isVisibleToUser = visibleToUser;
    }

    public Integer getQaId() {
        return qaId;
    }

    public QA setQaId(Integer qaId) {
        this.qaId = qaId;
        return this;
    }
}
