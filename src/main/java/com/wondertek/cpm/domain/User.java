package com.wondertek.cpm.domain;

import com.wondertek.cpm.config.Constants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import org.springframework.data.elasticsearch.annotations.Document;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.time.ZonedDateTime;

/**
 * 员工信息
 */
@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "user")
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 登录用户名
     */
    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;
    /**
     * 密码
     */
    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash",length = 60)
    private String password;
    
    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    @Deprecated
    private String firstName;					//这个干掉，用lastName作为员工的真实姓名
    /**
     * 员工真实姓名
     */
    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;
    /**
     * EMAIL
     */
    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String email;
    /**
     * 是否激活
     */
    @NotNull
    @Column(nullable = false)
    private boolean activated = false;
    /**
     * 语言
     */
    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;
    /**
     * 
     */
    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @Column(name = "reset_date", nullable = true)
    private ZonedDateTime resetDate = null;
    /**
     * 工号，唯一
     */
    @Column(name = "serial_num")
    private String serialNum;
    /**
     * 所属部门
     */
    @Column(name = "dept_id")
    private Long deptId;
    
    @Transient
    private String dept;
    /**
     * 管理人员（true是/false否，默认否）
     */
    @Column(name = "is_manager")
    private Boolean isManager;
    /**
     * 岗位
     */
    @Column(name = "duty_")
    private String duty;
    /**
     * 级别
     */
    @Column(name = "grade_")
    private String grade;
    /**
     * 性别(1男/2女)
     */
    @Column(name = "gender_")
    private Integer gender;
    /**
     * 出生年,如1990
     */
    @Column(name = "birth_year")
    private String birthYear;
    /**
     * 生日，如1212
     */
    @Column(name = "birth_day")
    private String birthDay;
    /**
     * 电话
     */
    @Column(name = "telephone_")
    private String telephone;
    /**
     * 工作地点
     */
    @Column(name = "work_area")
    private String workArea;
	
    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "jhi_user_authority",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PersistentToken> persistentTokens = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }
    public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	//Lowercase the login before saving it in database
    public void setLogin(String login) {
    	if(login != null)
    		this.login = login.toLowerCase(Locale.ENGLISH);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Deprecated
    public String getFirstName() {
        return firstName;
    }
    @Deprecated
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public ZonedDateTime getResetDate() {
       return resetDate;
    }

    public void setResetDate(ZonedDateTime resetDate) {
       this.resetDate = resetDate;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<PersistentToken> getPersistentTokens() {
        return persistentTokens;
    }

    public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
        this.persistentTokens = persistentTokens;
    }

    public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Boolean getIsManager() {
		return isManager;
	}

	public void setIsManager(Boolean isManager) {
		this.isManager = isManager;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(String birthYear) {
		this.birthYear = birthYear;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getWorkArea() {
		return workArea;
	}

	public void setWorkArea(String workArea) {
		this.workArea = workArea;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!login.equals(user.login)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", password=" + password
				+ ", lastName=" + lastName + ", email=" + email + ", activated=" + activated + ", langKey=" + langKey
				+ ", activationKey=" + activationKey + ", resetKey=" + resetKey + ", resetDate=" + resetDate
				+ ", serialNum=" + serialNum + ", deptId=" + deptId + ", dept=" + dept + ", isManager=" + isManager
				+ ", duty=" + duty + ", grade=" + grade + ", gender=" + gender + ", birthYear=" + birthYear
				+ ", birthDay=" + birthDay + ", telephone=" + telephone + ", workArea=" + workArea + "]";
	}    
}
