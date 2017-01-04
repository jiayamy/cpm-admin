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
 * 部门信息
 */
@Entity
@Table(name = "w_dept_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "deptinfo")
public class DeptInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 部门名称
     */
    @Column(name = "name_")
    private String name;
    /**
     * 上级部门主键
     */
    @Column(name = "parent_id")
    private Long parentId;
    /**
     * 父级ID路径，到“父IDPATH/父ID/”。。顶层默认是“/”
     */
    @Column(name = "id_path")
    private String idPath;
    /**
     * 部门类型
     */
    @Column(name = "type_")
    private Long type;
    /**
     * 状态（1可用，2删除），删除时必须部门下面都没人员
     */
    @Column(name = "status_")
    private Integer status;
    
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

    public String getName() {
        return name;
    }

    public DeptInfo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public DeptInfo parentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}

	public Long getType() {
        return type;
    }

    public DeptInfo type(Long type) {
        this.type = type;
        return this;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public DeptInfo status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public DeptInfo creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public DeptInfo createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public DeptInfo updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public DeptInfo updateTime(ZonedDateTime updateTime) {
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
        DeptInfo deptInfo = (DeptInfo) o;
        if (deptInfo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, deptInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DeptInfo{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", parentId='" + parentId + "'" +
            ", type='" + type + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
