package com.spmf.tv;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class IPTVChannel extends PanacheEntity {

    public Long sourceId;

    public String name;

    @Column(length=4096)
    public String logo;

    @Column(length=4096)
    public String groupTitle;

    public String tvgId;

    @Column(length=4096)
    public String streamUrl;

    public Boolean enabled = true;

    @Column(length=4096)
    public String groupName;

    public String source;

}