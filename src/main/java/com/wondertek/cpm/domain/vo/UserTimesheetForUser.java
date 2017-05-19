package com.wondertek.cpm.domain.vo;

public class UserTimesheetForUser {
	private Long userId;		//	员工ID
	private Integer type;		//	类型（合同、项目、公共成本、日期）
	private Long objId;			//	对象ID
	private String objName;		//	对象名称
	private String inputType;	//		工时类型名称
	private String data1;		//	可能为工时或者地区
	private Long id1;			//		对应的ID
	private String data2;		//	
	private Long id2;			//		对应的ID
	private String data3;		//	
	private Long id3;			//		对应的ID
	private String data4;		//	
	private Long id4;			//		对应的ID
	private String data5;		//	
	private Long id5;			//		对应的ID
	private String data6;		//	
	private Long id6;			//		对应的ID
	private String data7;		//	
	private Long id7;			//		对应的ID
	
	public UserTimesheetForUser(Integer type, String data1, String data2, String data3, String data4, String data5, String data6,String data7) {
		this.type = type;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;
		this.data5 = data5;
		this.data6 = data6;
		this.data7 = data7;
	}
	
	public UserTimesheetForUser() {
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getObjId() {
		return objId;
	}
	public void setObjId(Long objId) {
		this.objId = objId;
	}
	public String getObjName() {
		return objName;
	}
	public void setObjName(String objName) {
		this.objName = objName;
	}
	public String getData1() {
		return data1;
	}
	public void setData1(String data1) {
		this.data1 = data1;
	}
	public String getData2() {
		return data2;
	}
	public void setData2(String data2) {
		this.data2 = data2;
	}
	public String getData3() {
		return data3;
	}
	public void setData3(String data3) {
		this.data3 = data3;
	}
	public String getData4() {
		return data4;
	}
	public void setData4(String data4) {
		this.data4 = data4;
	}
	public String getData5() {
		return data5;
	}
	public void setData5(String data5) {
		this.data5 = data5;
	}
	public String getData6() {
		return data6;
	}
	public void setData6(String data6) {
		this.data6 = data6;
	}
	public String getData7() {
		return data7;
	}
	public void setData7(String data7) {
		this.data7 = data7;
	}
	public Long getId1() {
		return id1;
	}
	public void setId1(Long id1) {
		this.id1 = id1;
	}
	public Long getId2() {
		return id2;
	}
	public void setId2(Long id2) {
		this.id2 = id2;
	}
	public Long getId3() {
		return id3;
	}
	public void setId3(Long id3) {
		this.id3 = id3;
	}
	public Long getId4() {
		return id4;
	}
	public void setId4(Long id4) {
		this.id4 = id4;
	}
	public Long getId5() {
		return id5;
	}
	public void setId5(Long id5) {
		this.id5 = id5;
	}
	public Long getId6() {
		return id6;
	}
	public void setId6(Long id6) {
		this.id6 = id6;
	}
	public Long getId7() {
		return id7;
	}
	public void setId7(Long id7) {
		this.id7 = id7;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
}
