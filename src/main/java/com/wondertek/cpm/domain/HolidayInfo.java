package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 工作日信息
 */
@Entity
@Table(name = "w_holiday_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "holidayinfo")
public class HolidayInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "curr_day")
    private Long currDay;			//当前天，比如20161227

    @Column(name = "type_")
    private Integer type;			//类型（1正常工作日/2正常假日/3年假/4国家假日）

    @Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @Column(name = "updator_")
    private String updator;

    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCurrDay() {
        return currDay;
    }

    public HolidayInfo currDay(Long currDay) {
        this.currDay = currDay;
        return this;
    }

    public void setCurrDay(Long currDay) {
        this.currDay = currDay;
    }

    public Integer getType() {
        return type;
    }

    public HolidayInfo type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCreator() {
        return creator;
    }

    public HolidayInfo creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public HolidayInfo createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public HolidayInfo updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public HolidayInfo updateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HolidayInfo holidayInfo = (HolidayInfo) o;
        if (holidayInfo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, holidayInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "HolidayInfo{" +
            "id=" + id +
            ", currDay='" + currDay + "'" +
            ", type='" + type + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
