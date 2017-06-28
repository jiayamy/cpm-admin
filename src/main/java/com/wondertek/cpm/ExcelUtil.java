package com.wondertek.cpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * excel工具类
 * @author lvliuzhong
 */
public class ExcelUtil {
	private final static Logger log = LoggerFactory.getLogger(ExcelUtil.class);
	
	public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
	public static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";
	public static final String EMPTY = "";
	public static final String POINT = ".";
	/**
	 * 获得path的后缀名
	 * @param path
	 * @return
	 */
	public static String getPostfix(String path) {
		if (path == null || EMPTY.equals(path.trim())) {
			return EMPTY;
		}
		if (path.contains(POINT)) {
			return path.substring(path.lastIndexOf(POINT) + 1, path.length());
		}
		return EMPTY;
	}

	/**
	 * 读取2003版本的excel
	 * @param hssfCell
	 * @return
	 */
	public static Object get2003XlsValue(HSSFCell hssfCell) {
		if (hssfCell == null) {
			return null;
		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return hssfCell.getBooleanCellValue();
		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
				return HSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue());
			} else {
				return hssfCell.getNumericCellValue();
			}
		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_ERROR) {
			return null;
		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return null;
		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return hssfCell.getCellFormula();
		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_STRING) {
			return hssfCell.getStringCellValue();
		} else {
			return null;
		}
	}
	/**
	 * 读取2010版本的excel
	 * @param xssfCell
	 * @return
	 */
	public static Object get2010XlsxValue(XSSFCell xssfCell) {
		if (xssfCell == null) {
			return null;
		} else if (xssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return xssfCell.getBooleanCellValue();
		} else if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			if (DateUtil.isCellDateFormatted(xssfCell)) {
				return DateUtil.getJavaDate(xssfCell.getNumericCellValue());
			} else {
				return xssfCell.getNumericCellValue();
			}
		} else if (xssfCell.getCellType() == Cell.CELL_TYPE_ERROR) {
			return null;
		} else if (xssfCell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return null;
		} else if (xssfCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return xssfCell.getCellFormula();
		} else if (xssfCell.getCellType() == Cell.CELL_TYPE_STRING) {
			return xssfCell.getStringCellValue();
		} else {
			return null;
		}
	}
	
	/**
     * 读取 Excel .xlsx,.xls
     * @throws IOException
     */
    public static List<ExcelValue> readExcel(MultipartFile file, int startNum, int maxSheet, int maxCell) throws IOException {
        if(file == null || file.getOriginalFilename() == null
        		|| ExcelUtil.EMPTY.equals(file.getOriginalFilename().trim())){
            return null;
        }else{
            String postfix = ExcelUtil.getPostfix(file.getOriginalFilename());
            if(!ExcelUtil.EMPTY.equals(postfix)){
                if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
                    return readXls(file,startNum,maxSheet,maxCell);
                }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){
                    return readXlsx(file,startNum,maxSheet,maxCell);
                }else{
                    return null;
                }
            }
        }
        return null;
    }
    public static List<ExcelValue> readExcel(File file, int startNum, int maxSheet, int maxCell) throws IOException {
        if(file == null || file.getName() == null
        		|| ExcelUtil.EMPTY.equals(file.getName().trim())){
            return null;
        }else{
            String postfix = ExcelUtil.getPostfix(file.getName());
            if(!ExcelUtil.EMPTY.equals(postfix)){
                if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
                    return readXls(file,startNum,maxSheet,maxCell);
                }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){
                    return readXlsx(file,startNum,maxSheet,maxCell);
                }else{
                    return null;
                }
            }
        }
        return null;
    }
    /**
     * 读取Excel .xlsx
     * @param file
     * @return
     */
    public static List<ExcelValue> readXlsx(MultipartFile file, int startNum, int maxSheet, int maxCell){
    	log.debug("readXlsx start for {}:",file.getOriginalFilename());
        InputStream input = null;
    	try {
			input = file.getInputStream();
		} catch (IOException e) {
			return null;
		}
    	return readXlsx(input,startNum,maxSheet,maxCell);
    }
    public static List<ExcelValue> readXlsx(File file, int startNum, int maxSheet, int maxCell){
    	log.debug("readXlsx start for {}:",file.getName());
    	InputStream input = null;
    	try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
    	return readXlsx(input,startNum,maxSheet,maxCell);
    }
    public static List<ExcelValue> readXlsx(InputStream input, int startNum, int maxSheet, int maxCell){
        List<ExcelValue> list = new ArrayList<ExcelValue>();
        XSSFWorkbook wb = null;
        ArrayList<Object> rowList = null;
        int totalRows = 0;	//总行数
        int totalCells = 0;	//总列数
        try {
            // 创建文档
            wb = new XSSFWorkbook(input);
            //读取sheet(页)
            for(int numSheet = 0; numSheet < wb.getNumberOfSheets() && numSheet < maxSheet; numSheet++){
                XSSFSheet xssfSheet = wb.getSheetAt(numSheet);
                if(xssfSheet == null){
                    continue;
                }
                ExcelValue excelValue = new ExcelValue();
                excelValue.setSheet(numSheet + 1);
                
                totalRows = xssfSheet.getLastRowNum();
                //读取Row
                for(int rowNum = startNum; rowNum <= totalRows; rowNum++){
                    XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                    rowList = null;
                    if(xssfRow != null){
                        rowList = new ArrayList<Object>();
                        totalCells = xssfRow.getLastCellNum();
                        //读取列，从第一列开始
                        for(int c = 0; c < maxCell; c++){
                            XSSFCell cell = xssfRow.getCell(c);
                            if(cell == null){
                                rowList.add(null);
                                continue;
                            }
                            rowList.add(ExcelUtil.get2010XlsxValue(cell));
                        }
                    }
                    excelValue.addVals(rowList);
                }
                list.add(excelValue);
                log.debug("readXlsx sheet:{},rows:{}",xssfSheet.getSheetName(),totalRows);
            }
            log.debug("readXlsx end");
            return list;
        } catch (IOException e) {
            log.error("readXlsx error:", e);
        } finally{
        	try {
				wb.close();
			} catch (IOException e) {
			}
            try {
                input.close();
            } catch (IOException e) {
            }
        }
    	list.clear();
    	list = null;
        return null;
    }
    /**
     * 读取 Excel .xls
     * @param file
     * @return
     */
    public static List<ExcelValue> readXls(MultipartFile file,int startNum, int maxSheet, int maxCell){
    	log.debug("Upload Excel readXls start for {}:",file.getOriginalFilename());
        // IO流读取文件
        InputStream input = null;
		try {
			input = file.getInputStream();
		} catch (IOException e) {
			return null;
		}
        return readXls(input,startNum,maxSheet,maxCell);
    }
    public static List<ExcelValue> readXls(File file,int startNum, int maxSheet, int maxCell){
    	log.debug("Upload Excel readXls start for {}:",file.getName());
    	InputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
    	return readXls(input,startNum,maxSheet,maxCell);
    }
    public static List<ExcelValue> readXls(InputStream input,int startNum, int maxSheet, int maxCell){
        List<ExcelValue> list = new ArrayList<ExcelValue>();
        // IO流读取文件
        HSSFWorkbook wb = null;
        ArrayList<Object> rowList = null;
        int totalRows = 0;	//总行数
        int totalCells = 0;	//总列数
        try {
            // 创建文档
            wb = new HSSFWorkbook(input);
            //读取sheet(页)
            for(int numSheet = 0; numSheet < wb.getNumberOfSheets() && numSheet < maxSheet; numSheet++){
                HSSFSheet hssfSheet = wb.getSheetAt(numSheet);
                if(hssfSheet == null){
                    continue;
                }
                ExcelValue excelValue = new ExcelValue();
                excelValue.setSheet(numSheet + 1);
                
                totalRows = hssfSheet.getLastRowNum();
                //读取Row
                for(int rowNum = startNum;rowNum <= totalRows;rowNum++){
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    rowList = null;
                    if(hssfRow!=null){
                        rowList = new ArrayList<Object>();
                        totalCells = hssfRow.getLastCellNum();
                        //读取列，从第一列开始
                        for(int c = 0; c < maxCell; c++){
                            HSSFCell cell = hssfRow.getCell(c);
                            if(cell==null){
                                rowList.add(null);
                                continue;
                            }
                            Object str = ExcelUtil.get2003XlsValue(cell);
							rowList.add(str);
                        }
                    }
                    excelValue.addVals(rowList);
                }
                list.add(excelValue);
                log.debug("readXlsx sheet:{},rows:{}",hssfSheet.getSheetName(),totalRows);
            }
            log.debug("Upload Excel readXlsx end");
            return list;
        } catch (IOException e) {
        	log.error("Excel Read error:", e);
        } finally{
        	try {
				wb.close();
			} catch (IOException e) {
			}
            try {
                input.close();
            } catch (IOException e) {
            }
        }
        list.clear();
    	list = null;
        return null;
    }
    /**
     * 获取导出的文件名
     * @return
     */
    public static String getExportName(HttpServletRequest request, String name){
    	try {
//			String ua = request.getHeader("User-Agent");
//			boolean isIE = false;
//			if(ua != null && !ua.toLowerCase().contains("opera") && ua.toLowerCase().contains(" msie ")){
//				isIE = true;
//			}
//			System.out.println(isIE);
//			if(isIE){//中文IE需要转
//				return URLEncoder.encode(name, "UTF-8");
//			}else{
//				return new String(name.getBytes(), "ISO8859-1");
//			}
			return new String(name.getBytes("gb2312"),"ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
		}
    	return name;
    }
    /**
     * 获取数据
     * 
     * @param file
     * @param startNum
     * @param maxSheet
     * @param maxCell
     * @return
     * @throws IOException
     */
    public static List<ExcelValue> readExcel(File file) throws IOException {
    	if(file == null || file.getName() == null
        		|| ExcelUtil.EMPTY.equals(file.getName().trim())){
            return null;
        }else{
            String postfix = ExcelUtil.getPostfix(file.getName());
            if(!ExcelUtil.EMPTY.equals(postfix)){
                if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
                    return readXls(file);
                }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){
                    return readXlsx(file);
                }else{
                    return null;
                }
            }
        }
        return null;
    }
    private static List<ExcelValue> readXlsx(File file) {
    	log.debug("readXlsx start for {}:",file.getName());
    	InputStream input = null;
    	try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
    	return readXlsx(input);
    }
	private static List<ExcelValue> readXlsx(InputStream input) {
		List<ExcelValue> list = new ArrayList<ExcelValue>();
        XSSFWorkbook wb = null;
        ArrayList<Object> rowList = null;
        int totalRows = 0;	//总行数
        int totalCells = 0;	//总列数
        try {
            // 创建文档
            wb = new XSSFWorkbook(input);
            //读取sheet(页)
            for(int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++){
                XSSFSheet xssfSheet = wb.getSheetAt(numSheet);
                if(xssfSheet == null){
                    continue;
                }
                ExcelValue excelValue = new ExcelValue();
                excelValue.setSheet(numSheet + 1);
                excelValue.setSheetName(xssfSheet.getSheetName());
                
                totalRows = xssfSheet.getLastRowNum();
                //读取Row
                for(int rowNum = 0; rowNum < totalRows; rowNum++){
                    XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                    rowList = null;
                    if(xssfRow != null){
                        rowList = new ArrayList<Object>();
                        totalCells = xssfRow.getLastCellNum();
                        //读取列，从第一列开始
                        for(int c = 0; c < totalCells; c++){
                            XSSFCell cell = xssfRow.getCell(c);
                            if(cell == null){
                                rowList.add(null);
                                continue;
                            }
                            rowList.add(ExcelUtil.get2010XlsxValue(cell));
                        }
                    }
                    excelValue.addVals(rowList);
                }
                list.add(excelValue);
                log.debug("readXlsx sheet:{},rows:{}",xssfSheet.getSheetName(),totalRows);
            }
            log.debug("readXlsx end");
            return list;
        } catch (IOException e) {
            log.error("readXlsx error:", e);
        } finally{
        	try {
				wb.close();
			} catch (IOException e) {
			}
            try {
                input.close();
            } catch (IOException e) {
            }
        }
    	list.clear();
    	list = null;
        return null;
	}

	private static List<ExcelValue> readXls(File file) {
		return null;
	}
}