package com.wondertek.cpm.domain.vo;

import java.util.List;

public class DeptTree {
	public static final int SELECTTYPE_NONE = 0;
	public static final int SELECTTYPE_ALL = 1;
	public static final int SELECTTYPE_DEPT = 2;
	public static final int SELECTTYPE_USER = 3;
	/**
	 * 主键ID
	 */
	private Long objId;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 是否部门
	 */
	private Boolean isDept = Boolean.TRUE;
	/**
	 * 支持选择
	 */
	private Boolean supportSelect = Boolean.FALSE;
	/**
	 * 展开子节点
	 */
	private Boolean showChild = Boolean.TRUE;
	/**
	 * 子
	 */
	private List<DeptTree> children;
	
	public DeptTree() {
	}
	public DeptTree(Long objId, String name, Boolean isDept, Boolean supportSelect, Boolean showChild) {
		this.objId = objId;
		this.name = name;
		this.isDept = isDept;
		this.supportSelect = supportSelect;
		this.showChild = showChild;
	}
	public Long getObjId() {
		return objId;
	}
	public void setObjId(Long objId) {
		this.objId = objId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsDept() {
		return isDept;
	}
	public void setIsDept(Boolean isDept) {
		this.isDept = isDept;
	}
	public Boolean getSupportSelect() {
		return supportSelect;
	}
	public void setSupportSelect(Boolean supportSelect) {
		this.supportSelect = supportSelect;
	}
	public Boolean getShowChild() {
		return showChild;
	}
	public void setShowChild(Boolean showChild) {
		this.showChild = showChild;
	}
	public List<DeptTree> getChildren() {
		return children;
	}
	public void setChildren(List<DeptTree> children) {
		this.children = children;
	}
}
