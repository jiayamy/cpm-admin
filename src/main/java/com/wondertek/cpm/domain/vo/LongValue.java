package com.wondertek.cpm.domain.vo;

public class LongValue {
	private Long key;
	private Integer type;
	private String val;
	private Long p1;
	private Double p2;
	
	public LongValue(Long key, Integer type, String val) {
		this.key = key;
		this.type = type;
		this.val = val;
	}
	public LongValue(Long key, Integer type, String val,Long p1) {
		this.key = key;
		this.type = type;
		this.val = val;
		this.p1 = p1;
	}
	public LongValue(Long key,String val) {
		this.key = key;
		this.val = val;
	}
	
	public LongValue(Long key, String val, Long p1, Double p2) {
		super();
		this.key = key;
		this.val = val;
		this.p1 = p1;
		this.p2 = p2;
	}
	public Long getKey() {
		return key;
	}
	public void setKey(Long key) {
		this.key = key;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public Long getP1() {
		return p1;
	}
	public void setP1(Long p1) {
		this.p1 = p1;
	}
	public Double getP2() {
		return p2;
	}
	public void setP2(Double p2) {
		this.p2 = p2;
	}
	@Override
	public String toString() {
		return "LongValue [key=" + key + ", type=" + type + ", val=" + val + ", p1=" + p1 + ", p2=" + p2 + "]";
	}
	
}
