package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 用户工时统计/员工日报
 */
@Entity
@Table(name = "w_user_timesheet")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "usertimesheet")
public class UserTimesheet implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final Integer TYPE_DAY = -1;
    public static final Integer TYPE_AREA = -2;
    public static final Integer TYPE_PUBLIC = 1;
    public static final Integer TYPE_CONTRACT = 2;
    public static final Integer TYPE_PROJECT = 3;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "work_day")
    private Long workDay;		//	日期

    @Column(name = "user_id")
    private Long userId;		//	员工ID

    @Column(name = "user_name")
    private String userName;	//		员工名字
    
    @Column(name = "type_")
    private Integer type;		//	类型（2合同、3项目、1公共成本）

    @Column(name = "obj_id")
    private Long objId;			//	对象ID

    @Column(name = "obj_name")
    private String objName;		//	对象名称

    @Column(name = "real_input")
    private Double realInput;	//	工时投入（实际投入工时，项目成本需要）

    @Column(name = "accept_input")
    private Double acceptInput;	//	工时产出（认可工时，统计员工贡献度需要）

    @Column(name = "status_")
    private Integer status;		//	状态（可用，删除）
    
    @Column(name = "work_area")
    private String workArea;	//		地区
    
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

    public Long getWorkDay() {
        return workDay;
    }

    public UserTimesheet workDay(Long workDay) {
        this.workDay = workDay;
        return this;
    }

    public void setWorkDay(Long workDay) {
        this.workDay = workDay;
    }

    public Long getUserId() {
        return userId;
    }

    public UserTimesheet userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getType() {
        return type;
    }

    public UserTimesheet type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getObjId() {
        return objId;
    }

    public UserTimesheet objId(Long objId) {
        this.objId = objId;
        return this;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getObjName() {
        return objName;
    }

    public UserTimesheet objName(String objName) {
        this.objName = objName;
        return this;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public Double getRealInput() {
        return realInput;
    }

    public UserTimesheet realInput(Double realInput) {
        this.realInput = realInput;
        return this;
    }

    public void setRealInput(Double realInput) {
        this.realInput = realInput;
    }

    public Double getAcceptInput() {
        return acceptInput;
    }

    public UserTimesheet acceptInput(Double acceptInput) {
        this.acceptInput = acceptInput;
        return this;
    }

    public void setAcceptInput(Double acceptInput) {
        this.acceptInput = acceptInput;
    }

    public String getWorkArea() {
		return workArea;
	}

	public void setWorkArea(String workArea) {
		this.workArea = workArea;
	}

	public Integer getStatus() {
        return status;
    }

    public UserTimesheet status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public String getCreator() {
        return creator;
    }

    public UserTimesheet creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public UserTimesheet createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public UserTimesheet updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public UserTimesheet updateTime(ZonedDateTime updateTime) {
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
        UserTimesheet userTimesheet = (UserTimesheet) o;
        if (userTimesheet.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, userTimesheet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserTimesheet{" +
            "id=" + id +
            ", workDay='" + workDay + "'" +
            ", userId='" + userId + "'" +
            ", type='" + type + "'" +
            ", objId='" + objId + "'" +
            ", objName='" + objName + "'" +
            ", realInput='" + realInput + "'" +
            ", acceptInput='" + acceptInput + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
