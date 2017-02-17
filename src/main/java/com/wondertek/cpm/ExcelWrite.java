package com.wondertek.cpm;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 写入Excel
 * @author lvliuzhong
 */
public class ExcelWrite {
	
	private XSSFWorkbook xwb;
	
	public ExcelWrite() throws IOException {
		xwb = new XSSFWorkbook();
	}
	
	public XSSFWorkbook getXSSFWorkbook() {
		return xwb;
	}
	
	/**
	 * 创建一个工作表
	 * @param sheetName 工作表标题
	 * @param startRow 工作表开始写入行，从1开始
	 * @param rows	需要写入的对象
	 */
	public void createSheet(String sheetName,int startRow,List<String[]> rows) {
		//建立sheet
		XSSFSheet sheet = xwb.getSheet(sheetName);
		if(sheet == null){
			sheet = xwb.createSheet(sheetName);
		}
		
		if(startRow < 1){
			startRow = 1;
		}
		
		String[] cells = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		//写入数据
	    for (int i = 0; i < rows.size(); i++) {
	    	cells = rows.get(i);
	    	row = sheet.createRow(i+startRow-1);
	    	for (int j = 0; j < cells.length; j++) {
	    		cell = row.createCell(j);
	    		cell.setCellValue(cells[j]);
	    	}
	    }
	}
	
	/**
	 * 创建一个工作表
	 * @param sheetName	sheet名称
	 * @param startRow 工作表开始写入行，从1开始
	 * @param heads		头部
	 * @param rows		数据
	 */
	public void createSheet(String sheetName,int startRow, String[] heads, List<String[]> rows) {
		//建立sheet
		XSSFSheet sheet = xwb.getSheet(sheetName);
		if(sheet == null){
			sheet = xwb.createSheet(sheetName);
		}
		if(startRow < 1){
			startRow = 1;
		}
		String[] cells = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		//写入标题
		row = sheet.createRow(startRow-1);
		for (int i = 0; i < heads.length; i++) {
	    	cell = row.createCell(i);
    		cell.setCellValue(heads[i]);
		}
		//写入数据
	    for (int i = 0; i < rows.size(); i++) {
	    	cells = rows.get(i);
	    	row = sheet.createRow(i + startRow);
	    	for (int j = 0; j < cells.length; j++) {
	    		cell = row.createCell(j);
	    		cell.setCellValue(cells[j]);
	    	}
	    }
	}
	/**
	 * 写入后关闭
	 * @throws IOException
	 */
	public void close(OutputStream outputStream) throws IOException {
		xwb.write(outputStream);
	    // 关闭 Excel 工作薄对象
	    xwb.close();
	}
	/**
	 * 写入后关闭
	 * @throws IOException
	 */
	public void close(String path) throws IOException {
		OutputStream outputStream = new FileOutputStream(path);  
		xwb.write(outputStream);
	    // 关闭 Excel 工作薄对象
	    xwb.close();
	    try {
			outputStream.close();
		} catch (Exception e) {
		}
	}
}
