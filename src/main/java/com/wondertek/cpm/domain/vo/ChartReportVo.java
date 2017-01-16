package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChartReportVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<String> legend = new ArrayList<>(); //数据分组
	
	private List<String> category = new ArrayList<String>();//横坐标  
	
    private List<ChartReportDataVo> series = new ArrayList<ChartReportDataVo>();//纵坐标  
    
    private String title;

	public List<String> getLegend() {
		return legend;
	}

	public void setLegend(List<String> legend) {
		this.legend = legend;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public List<ChartReportDataVo> getSeries() {
		return series;
	}

	public void setSeries(List<ChartReportDataVo> series) {
		this.series = series;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
    
    
}
