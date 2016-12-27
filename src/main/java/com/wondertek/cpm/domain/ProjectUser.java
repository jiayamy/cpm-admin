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
 * 项目人员管理
 */
@Entity
@Table(name = "w_project_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectuser")
public class ProjectUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;			//	项目主键

    @Column(name = "user_id")
    private Long userId;			//	项目人员ID

    @Column(name = "user_name")
    private String userName;		//	项目人员名称

    @Column(name = "user_role")
    private String userRole;		//	人员角色（需求、开发、测试、研发、项目经理）

    @Column(name = "join_day")
    private Long joinDay;			//	加盟日，比如20161227

    @Column(name = "leave_day")
    private Long leaveDay;			//	离开日，比如20161227

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

    public Long getProjectId() {
        return projectId;
    }

    public ProjectUser projectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public ProjectUser userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public ProjectUser userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public ProjectUser userRole(String userRole) {
        this.userRole = userRole;
        return this;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Long getJoinDay() {
        return joinDay;
    }

    public ProjectUser joinDay(Long joinDay) {
        this.joinDay = joinDay;
        return this;
    }

    public void setJoinDay(Long joinDay) {
        this.joinDay = joinDay;
    }
    public ProjectUser leaveDay(Long joinDay) {
    	 this.joinDay = joinDay;
    	 return this;
	}
    public Long getLeaveDay() {
		return leaveDay;
	}

	public void setLeaveDay(Long leaveDay) {
		this.leaveDay = leaveDay;
	}

	public String getCreator() {
        return creator;
    }

    public ProjectUser creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ProjectUser createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ProjectUser updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ProjectUser updateTime(ZonedDateTime updateTime) {
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
        ProjectUser projectUser = (ProjectUser) o;
        if (projectUser.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, projectUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProjectUser{" +
            "id=" + id +
            ", projectId='" + projectId + "'" +
            ", userId='" + userId + "'" +
            ", userName='" + userName + "'" +
            ", userRole='" + userRole + "'" +
            ", joinDay='" + joinDay + "'" +
            ", leaveDay='" + leaveDay + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
