package com.wondertek.cpm.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

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
    /**
     * @deprecated
     */
    public static final Integer TYPE_PUBLIC = 1;
    public static final Integer TYPE_CONTRACT = 2;
    public static final Integer TYPE_PROJECT = 3;
    
    public static final Integer CHARACTER_ABLE = 0;
    public static final Integer CHARACTER_UNABLE = 1;
    
    public static final String TYPE_INPUT_NORMAL = "正常工时";
    public static final String TYPE_INPUT_EXTRA = "加班工时";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 日期
     */
    @Column(name = "work_day")
    private Long workDay;
    /**
     * 员工ID
     */
    @Column(name = "user_id")
    private Long userId;
    /**
     * 员工名字
     */
    @Column(name = "user_name")
    private String userName;
    /**
     * 类型（1公共成本/2合同/3项目）
     */
    @Column(name = "type_")
    private Integer type;
    /**
     * 对象ID,项目或者合同ID，类型为公共成本时为空
     */
    @Column(name = "obj_id")
    private Long objId;
    /**
     * 对象名称
     */
    @Column(name = "obj_name")
    private String objName;
    /**
     * 工时投入（实际投入工时，项目成本需要）
     */
    @Column(name = "real_input")
    private Double realInput;
    /**
     * 工时产出（认可工时，统计员工贡献度需要）
     */
    @Column(name = "accept_input")
    private Double acceptInput;
    /**
     * 状态（1可用，2删除）
     */
    @Column(name = "status_")
    private Integer status;
    /**
     * 工作地点
     */
    @Column(name = "work_area")
    private String workArea;
    
    @Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @Column(name = "updator_")
    private String updator;

    @Column(name = "update_time")
    private ZonedDateTime updateTime;
    
    /**
     * 加班工时
     */
    @Column(name = "extra_input")
    private Double extraInput;
    
    /**
     * 认可加班工时
     */
    @Column(name = "accept_extra_input")
    private Double acceptExtraInput;
    
    /**
     * 统计状态值，0统计前,1统计后(0-可修改，1-不可以修改)
     */
    @Column(name = "character_")
    private Integer character;

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
    

    public Double getExtraInput() {
		return extraInput;
	}

	public void setExtraInput(Double extraInput) {
		this.extraInput = extraInput;
	}

	public Double getAcceptExtraInput() {
		return acceptExtraInput;
	}

	public void setAcceptExtraInput(Double acceptExtraInput) {
		this.acceptExtraInput = acceptExtraInput;
	}

	public Integer getCharacter() {
		return character;
	}

	public void setCharacter(Integer character) {
		this.character = character;
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
		return "UserTimesheet [id=" + id + ", workDay=" + workDay + ", userId=" + userId + ", userName=" + userName
				+ ", type=" + type + ", objId=" + objId + ", objName=" + objName + ", realInput=" + realInput
				+ ", acceptInput=" + acceptInput + ", status=" + status + ", workArea=" + workArea + ", creator="
				+ creator + ", createTime=" + createTime + ", updator=" + updator + ", updateTime=" + updateTime
				+ ", extraInput=" + extraInput + ", acceptExtraInput=" + acceptExtraInput + ", character=" + character + "]";
	}

}
