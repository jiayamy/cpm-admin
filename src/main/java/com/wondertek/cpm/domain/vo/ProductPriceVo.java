package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ProductPrice;

public class ProductPriceVo {
	private Long id;
	
	private String name;
	private Integer type;
	private Integer source;
	private String units;
	private Double price;
	
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;
	
	private String creator;
	private String updator;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public ZonedDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}
	public ZonedDateTime getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	
	public ProductPriceVo (ProductPrice productPrice ,Integer key){
		this.id = productPrice.getId();
		this.name = productPrice.getName();
		this.price = productPrice.getPrice();
		this.type = productPrice.getType();
		this.source = productPrice.getSource();
		this.createTime = productPrice.getCreateTime();
		this.creator = productPrice.getCreator();
		this.updateTime = productPrice.getUpdateTime();
		this.updator = productPrice.getUpdator();
	}
}
