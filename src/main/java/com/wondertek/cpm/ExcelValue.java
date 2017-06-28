package com.wondertek.cpm;

import java.util.ArrayList;
import java.util.List;

/**
 * 读取的excel值
 * 有sheet，方便后面解析出错给前台返回消息
 * @author lvliuzhong
 */
public class ExcelValue {
	private int sheet;//sheet编号
	private List<List<Object>> vals;	//一个sheet里面的所有行里面的值
	private String sheetName;	//sheet名称
	
	public int getSheet() {
		return sheet;
	}
	public void setSheet(int sheet) {
		this.sheet = sheet;
	}
	public List<List<Object>> getVals() {
		return vals;
	}
	public void setVals(List<List<Object>> vals) {
		this.vals = vals;
	}
	public void addVals(List<Object> val) {
		if(vals == null){
			vals = new ArrayList<List<Object>>();
		}
		vals.add(val);
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}