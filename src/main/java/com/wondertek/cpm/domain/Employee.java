package com.wondertek.cpm.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 员工信息
 * 
 * */
@Entity
@Table(name = "employee")
public class Employee {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
	private Integer id;                       //主键
	
	@Column(name = "serial_number")
	private Integer serialNumber;   //员工工号
	
	@Column(name = "name_")
	private String name;                  //员工姓名
	
	@Column(name = "department_")
	private String department;       //所属部门
	
	@Column(name = "station_")
	private String station;               //岗位
	
	@Column(name = "rank_")
	private String rank;					  //级别
	
	@Column(name = "gender_")
	private String gender;              //性别
	
	@Column(name = "birthday_")
	private Date birthday;             //生日
	
	@Column(name = "telephone_")
	private String telephone;         //电话
	
	@Column(name = "email_")
	private String email;                //邮箱
	
	@Column(name = "creator_")
	private String creator;            //创建者
	
	@Column(name = "creator_time")
	private Date creatorTime;      //创建时间
	
	@Column(name = "updator_")
	private String updator;           //修改者
	
	@Column(name = "updator_time")
	private Date updatorTime;	//修改时间
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}	
	public Integer getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreatorTime() {
		return creatorTime;
	}
	public void setCreatorTime(Date creatorTime) {
		this.creatorTime = creatorTime;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	public Date getUpdatorTime() {
		return updatorTime;
	}
	public void setUpdatorTime(Date updatorTime) {
		this.updatorTime = updatorTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result + ((creatorTime == null) ? 0 : creatorTime.hashCode());
		result = prime * result + ((department == null) ? 0 : department.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((station == null) ? 0 : station.hashCode());
		result = prime * result + ((telephone == null) ? 0 : telephone.hashCode());
		result = prime * result + ((updator == null) ? 0 : updator.hashCode());
		result = prime * result + ((updatorTime == null) ? 0 : updatorTime.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (creatorTime == null) {
			if (other.creatorTime != null)
				return false;
		} else if (!creatorTime.equals(other.creatorTime))
			return false;
		if (department == null) {
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		if (telephone == null) {
			if (other.telephone != null)
				return false;
		} else if (!telephone.equals(other.telephone))
			return false;
		if (updator == null) {
			if (other.updator != null)
				return false;
		} else if (!updator.equals(other.updator))
			return false;
		if (updatorTime == null) {
			if (other.updatorTime != null)
				return false;
		} else if (!updatorTime.equals(other.updatorTime))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", serialNumber=" + serialNumber + ", name=" + name + ", department=" + department
				+ ", station=" + station + ", rank=" + rank + ", gender=" + gender + ", birthday=" + birthday
				+ ", telephone=" + telephone + ", email=" + email + ", creator=" + creator + ", creatorTime="
				+ creatorTime + ", updator=" + updator + ", updatorTime=" + updatorTime + "]";
	}	
}
