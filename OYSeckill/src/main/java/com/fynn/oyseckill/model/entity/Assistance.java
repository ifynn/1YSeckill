package com.fynn.oyseckill.model.entity;

import com.fynn.oyseckill.model.Link;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Fynn on 2016/9/6.
 */
public class Assistance extends BmobObject {

    private String name;
    private AssistanceGroup group;
    private List<Link> links;
    private Integer id;
    private Integer groupTypeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssistanceGroup getGroup() {
        return group;
    }

    public void setGroup(AssistanceGroup group) {
        this.group = group;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(Integer groupTypeId) {
        this.groupTypeId = groupTypeId;
    }
}
