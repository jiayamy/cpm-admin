package com.wondertek.cpm.web.rest.errors;

public class CpmResponse {
	private boolean success = Boolean.TRUE;
	private boolean isNew = Boolean.FALSE;
	
	private String msgKey;
	private String msgParam;
	
	private Object root;

	public CpmResponse() {
	}
	public CpmResponse(String msgKey) {
		this.msgKey = msgKey;
	}
	
	public static CpmResponse success(String msgKey){
		return new CpmResponse(msgKey).setSuccess(Boolean.TRUE);
	}
	public static CpmResponse error(String msgKey){
		return new CpmResponse(msgKey).setSuccess(Boolean.FALSE);
	}
	
	public boolean isSuccess() {
		return success;
	}
	public CpmResponse setSuccess(boolean success) {
		this.success = success;
		return this;
	}
	public boolean isNew() {
		return isNew;
	}
	public CpmResponse setNew(boolean isNew) {
		this.isNew = isNew;
		return this;
	}
	public String getMsgKey() {
		return msgKey;
	}

	public CpmResponse setMsgKey(String msgKey) {
		this.msgKey = msgKey;
		return this;
	}

	public String getMsgParam() {
		return msgParam;
	}

	public CpmResponse setMsgParam(String msgParam) {
		this.msgParam = msgParam;
		return this;
	}

	public Object getRoot() {
		return root;
	}

	public CpmResponse setRoot(Object root) {
		this.root = root;
		return this;
	}
	@Override
	public String toString() {
		return "CpmResponse [success=" + success + ", isNew=" + isNew + ", msgKey=" + msgKey + ", msgParam=" + msgParam
				+ ", root=" + root + "]";
	}
}
