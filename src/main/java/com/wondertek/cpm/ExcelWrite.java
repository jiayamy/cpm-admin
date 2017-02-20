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
 * 
 *	第一种：日期格式  
        cell.setCellValue(new Date(2008,5,5));  
        //set date format  
        HSSFCellStyle cellStyle = demoWorkBook.createCellStyle();  
        HSSFDataFormat format= demoWorkBook.createDataFormat();  
        cellStyle.setDataFormat(format.getFormat("yyyy年m月d日"));  
        cell.setCellStyle(cellStyle);  
    第二种：保留两位小数格式  
        cell.setCellValue(1.2);  
        HSSFCellStyle cellStyle = demoWorkBook.createCellStyle();  
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));  
        cell.setCellStyle(cellStyle);  
       
    这里与上面有所不同，用的是HSSFDataFormat.getBuiltinFormat()方法，之所以用这个，是因为0.00是Excel内嵌的格式，完整的Excel内嵌格式列表大家可以看这个窗口中的自定义列表：  
      
    这里就不一一列出了  
    第三种：货币格式  
                cell.setCellValue(20000);  
                HSSFCellStyle cellStyle = demoWorkBook.createCellStyle();  
                HSSFDataFormat format= demoWorkBook.createDataFormat();  
                cellStyle.setDataFormat(format.getFormat("¥#,##0"));  
                cell.setCellStyle(cellStyle);  
    第四种：百分比格式  
                cell.setCellValue(20);  
                HSSFCellStyle cellStyle = demoWorkBook.createCellStyle();  
                cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));  
                cell.setCellStyle(cellStyle);  
      此种情况跟第二种一样         
    第五种：中文大写格式  
                cell.setCellValue(20000);  
                HSSFCellStyle cellStyle = demoWorkBook.createCellStyle();  
                HSSFDataFormat format= demoWorkBook.createDataFormat();  
                cellStyle.setDataFormat(format.getFormat("[DbNum2][$-804]0"));  
                cell.setCellStyle(cellStyle);  
       
    第六种：科学计数法格式  
                cell.setCellValue(20000);  
                HSSFCellStyle cellStyle = demoWorkBook.createCellStyle();  
                cellStyle.setDataFormat( HSSFDataFormat.getBuiltinFormat("0.00E+00"));  
                cell.setCellStyle(cellStyle);  
    此种情况也与第二种情况一样  
    HSSFCellStyle cellStyle = wb.createCellStyle();    
     一、设置背景色:  
    cellStyle.setFillForegroundColor((short) 13);// 设置背景色    
    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);    
    二、设置边框:        
    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
    cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
    cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框    
    三、设置居中:  
    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中    
    四、设置字体:  
    HSSFFont font = wb.createFont();    
    font.setFontName("黑体");    
    font.setFontHeightInPoints((short) 16);//设置字体大小    
    HSSFFont font2 = wb.createFont();    
    font2.setFontName("仿宋_GB2312");    
    font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示    
    font2.setFontHeightInPoints((short) 12);    
    cellStyle.setFont(font);//选择需要用到的字体格式    
    五、设置列宽:  
    sheet.setColumnWidth(0, 3766);   
    //第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500    
    六、设置自动换行:      
    cellStyle.setWrapText(true);//设置自动换行    
    七、合并单元格:  
    Region region1 = new Region(0, (short) 0, 0, (short) 6);//参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号    
    //此方法在POI3.8中已经被废弃，建议使用下面一个    
    或者用  
    CellRangeAddress region1 = new CellRangeAddress(rowNumber, rowNumber, (short) 0, (short) 11);   
    //参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列      
    但应注意两个构造方法的参数不是一样的，具体使用哪个取决于POI的不同版本。
    sheet.addMergedRegion(region1);  
 * @author lvliuzhong
 */
public class ExcelWrite {
	
	private XSSFWorkbook xwb;
	private XSSFSheet currentSheet;
	
	public ExcelWrite() throws IOException {
		xwb = new XSSFWorkbook();
	}
	
	public XSSFWorkbook getXSSFWorkbook() {
		return xwb;
	}
	
	public XSSFSheet getCurrentSheet() {
		return currentSheet;
	}

	/**
	 * 创建一个工作表
	 * @param sheetName 工作表标题
	 * @param startRow 工作表开始写入行，从1开始
	 * @param rows	需要写入的对象
	 */
	public void createSheet(String sheetName,int startRow,List<String[]> rows) {
		//建立sheet
		currentSheet = xwb.getSheet(sheetName);
		if(currentSheet == null){
			currentSheet = xwb.createSheet(sheetName);
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
	    	row = currentSheet.createRow(i+startRow-1);
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
	 * @param cellType 参考  Cell.CELL_TYPE_*
	 * @param heads		头部
	 * @param rows		数据
	 */
	public void createSheet(String sheetName,int startRow,Integer[] cellType, String[] heads, List<String[]> rows) {
		//建立sheet
		currentSheet = xwb.getSheet(sheetName);
		if(currentSheet == null){
			currentSheet = xwb.createSheet(sheetName);
		}
		if(startRow < 1){
			startRow = 1;
		}
		String[] cells = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		//写入标题
		row = currentSheet.createRow(startRow-1);
		for (int i = 0; i < heads.length; i++) {
	    	cell = row.createCell(i);
    		cell.setCellValue(heads[i]);
		}
		//写入数据
	    for (int i = 0; i < rows.size(); i++) {
	    	cells = rows.get(i);
	    	row = currentSheet.createRow(i + startRow);
	    	for (int j = 0; j < cells.length; j++) {
	    		if(cellType.length > j){
	    			cell = row.createCell(j,cellType[j]);
	    		}else{
	    			cell = row.createCell(j);
	    		}
	    		cell.setCellValue(cells[j]);
	    	}
	    }
	}
	/**
	 * 建立sheet标题以及对应的sheet
	 * @param sheetName  sheet名称
	 * @param startRow	标题栏写的开始行
	 * @param heads	标题栏
	 */
	public void createSheetTitle(String sheetName,int startRow,String[] heads){
		//建立sheet
		currentSheet = xwb.getSheet(sheetName);
		if(currentSheet == null){
			currentSheet = xwb.createSheet(sheetName);
		}
		if(startRow < 1){
			startRow = 1;
		}
		XSSFRow row = null;
		XSSFCell cell = null;
		//写入标题
		row = currentSheet.createRow(startRow-1);
		for (int i = 0; i < heads.length; i++) {
	    	cell = row.createCell(i);
    		cell.setCellValue(heads[i]);
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
