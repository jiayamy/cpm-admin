package com.wondertek.cpm.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 部门类型
 */
@Entity
@Table(name = "w_dept_type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "depttype")
public class DeptType implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 中央研究院
     */
    public static final Long PRODUCT_DEVELOPMENT = 4L;
    /**
     * 产品研发中心
     */
    public static final Long PROJECT_IMPLEMENTATION = 5L;
    
    public static final int TYPE_DEPT_SALE = 2;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 类型名称 (1管理/2销售/3产品咨询/4产品研发中心/5项目实施/6采购/7行政/8财务/9质量管理/10人力资源）
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name_", length = 100, nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public DeptType name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeptType deptType = (DeptType) o;
        if (deptType.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, deptType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DeptType{" +
            "id=" + id +
            ", name='" + name + "'" +
            '}';
    }
}
