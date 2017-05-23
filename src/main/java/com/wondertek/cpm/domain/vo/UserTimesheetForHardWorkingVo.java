package com.wondertek.cpm.domain.vo;

public class UserTimesheetForHardWorkingVo {
	private Long userId;
	private Double sumRealInput;
	private Double sumAcceptRealInput;
	private Double sumExtraInput;
	private Double sumAcceptExtraInput;
	public UserTimesheetForHardWorkingVo(){
		
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getSumRealInput() {
		return sumRealInput;
	}

	public void setSumRealInput(Double sumRealInput) {
		this.sumRealInput = sumRealInput;
	}

	public Double getSumAcceptRealInput() {
		return sumAcceptRealInput;
	}

	public void setSumAcceptRealInput(Double sumAcceptRealInput) {
		this.sumAcceptRealInput = sumAcceptRealInput;
	}

	public Double getSumExtraInput() {
		return sumExtraInput;
	}

	public void setSumExtraInput(Double sumExtraInput) {
		this.sumExtraInput = sumExtraInput;
	}

	public Double getSumAcceptExtraInput() {
		return sumAcceptExtraInput;
	}

	public void setSumAcceptExtraInput(Double sumAcceptExtraInput) {
		this.sumAcceptExtraInput = sumAcceptExtraInput;
	}

	
	
}
