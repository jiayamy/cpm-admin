package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 项目完成信息
 */
@Entity
@Table(name = "w_project_finish_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectfinishinfo")
public class ProjectFinishInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 项目主键
     */
    @Column(name = "project_id")
    private Long projectId;
    /**
     * 完成率，单位 %
     */
    @Column(name = "finish_rate")
    private Double finishRate;

    @Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectFinishInfo projectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Double getFinishRate() {
        return finishRate;
    }

    public ProjectFinishInfo finishRate(Double finishRate) {
        this.finishRate = finishRate;
        return this;
    }

    public void setFinishRate(Double finishRate) {
        this.finishRate = finishRate;
    }

    public String getCreator() {
        return creator;
    }

    public ProjectFinishInfo creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ProjectFinishInfo createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectFinishInfo projectFinishInfo = (ProjectFinishInfo) o;
        if (projectFinishInfo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, projectFinishInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProjectFinishInfo{" +
            "id=" + id +
            ", projectId='" + projectId + "'" +
            ", finishRate='" + finishRate + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            '}';
    }
}
