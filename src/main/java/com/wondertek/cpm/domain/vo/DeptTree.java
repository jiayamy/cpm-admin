package com.wondertek.cpm.domain.vo;

import java.util.List;

public class DeptTree {
	public static final int SELECTTYPE_NONE = 0;
	public static final int SELECTTYPE_ALL = 1;
	public static final int SELECTTYPE_DEPT = 2;
	public static final int SELECTTYPE_USER = 3;
	/**
	 * 主键ID(员工或者部门ID)
	 */
	private Long objId;
	/**
	 * 名称（员工或者部门名称）
	 */
	private String name;
	/**
	 * 父节点ID（员工对应的是员工所属部门ID，部门对应的就是部门的上级部门ID）
	 */
	private Long parentId;
	/**
	 * 父节点名称（员工对应的是员工所属部门，部门对应的就是部门的上级部门）
	 */
	private String parentName;
	
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
	public DeptTree(Long objId, String name,Long parentId,String parentName, Boolean isDept, Boolean supportSelect, Boolean showChild) {
		this.objId = objId;
		this.name = name;
		this.parentId = parentId;
		this.parentName = parentName;
		this.isDept = isDept;
		this.supportSelect = supportSelect;
		this.showChild = showChild;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
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
	@Override
	public String toString() {
		return "DeptTree [objId=" + objId + ", name=" + name + ", parentId=" + parentId + ", parentName=" + parentName
				+ ", isDept=" + isDept + ", supportSelect=" + supportSelect + ", showChild=" + showChild + ", children="
				+ children + "]";
	}
	
}
