package com.wondertek.cpm.domain.vo;

public class ParticipateInfo {
	private int type;
	private Long objId;
	private Long joinDay;
	private Long leaveDay;
	
	public ParticipateInfo(Long objId, Integer type, Long joinDay, Long leaveDay) {
		this.type = type;
		this.objId = objId;
		this.joinDay = joinDay;
		this.leaveDay = leaveDay;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Long getObjId() {
		return objId;
	}
	public void setObjId(Long objId) {
		this.objId = objId;
	}
	public Long getJoinDay() {
		return joinDay;
	}
	public void setJoinDay(Long joinDay) {
		this.joinDay = joinDay;
	}
	public Long getLeaveDay() {
		return leaveDay;
	}
	public void setLeaveDay(Long leaveDay) {
		this.leaveDay = leaveDay;
	}
}
