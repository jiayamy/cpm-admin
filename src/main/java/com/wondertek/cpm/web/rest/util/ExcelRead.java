package com.wondertek.cpm.web.rest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.wondertek.cpm.web.rest.UserCostResource;

/**
 * 读取Excel
 * @author Administrator
 *
 */
public class ExcelRead {
	
	private final Logger log = LoggerFactory.getLogger(UserCostResource.class);

	public int totalRows; //sheet中总行数  
    public static int totalCells; //每一行总单元格数  
    /**
     * 读取 Excel .xlsx,.xls 
     * @param file
     * @return
     * @throws IOException
     */
    public List<ArrayList<String>> readExcel(MultipartFile file) throws IOException {  
        if(file==null||ExcelUtil.EMPTY.equals(file.getOriginalFilename().trim())){  
            return null;  
        }else{  
            String postfix = ExcelUtil.getPostfix(file.getOriginalFilename());  
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
    
    /**
     * 读取Excel .xlsx
     * @param file
     * @return
     */
    public List<ArrayList<String>> readXlsx(MultipartFile file){ 
    	log.debug("Upload Excel readXlsx start for {}:",file.getOriginalFilename());
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();  
        // IO流读取文件  
        InputStream input = null;  
        XSSFWorkbook wb = null;  
        ArrayList<String> rowList = null;  
        try {  
            input = file.getInputStream();  
            // 创建文档  
            wb = new XSSFWorkbook(input);                         
            //读取sheet(页)  
            for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){  
                XSSFSheet xssfSheet = wb.getSheetAt(numSheet);  
                if(xssfSheet == null){  
                    continue;  
                }  
                totalRows = xssfSheet.getLastRowNum();                
                //读取Row,从第二行开始  
                for(int rowNum = 1;rowNum <= totalRows;rowNum++){  
                    XSSFRow xssfRow = xssfSheet.getRow(rowNum);  
                    if(xssfRow!=null){  
                        rowList = new ArrayList<String>();  
                        totalCells = xssfRow.getLastCellNum();  
                        //读取列，从第一列开始  
                        for(int c=0;c<=totalCells+1;c++){  
                            XSSFCell cell = xssfRow.getCell(c);  
                            if(cell==null){  
                                rowList.add(ExcelUtil.EMPTY);  
                                continue;  
                            }                             
//                            rowList.add(ExcelUtil.getXValue(cell).trim());  
                            String str = ExcelUtil.getXValue(cell);
                            if (str != null) {
								rowList.add(str.trim());
							}else{
								rowList.add(str);
							}
                        }                                                 
                    }
                    log.debug("Uploaded Excel Per Row:",rowList);
                }  
            }
            log.debug("Upload Excel readXlsx end");
            return list;  
        } catch (IOException e) {             
            log.error("Excel Read error:", e);  
        } finally{  
            try {  
                input.close();  
            } catch (IOException e) {  
                log.error("InputStream close error:", e);  
            }  
        }
        return null;  
    }
    
    /** 
     * 读取 Excel .xls 
     * @param file 
     * @return 
     */  
    public List<ArrayList<String>> readXls(MultipartFile file){
    	log.debug("Upload Excel readXls start for {}:",file.getOriginalFilename());
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();  
        // IO流读取文件  
        InputStream input = null;  
        HSSFWorkbook wb = null;  
        ArrayList<String> rowList = null;  
        try {  
            input = file.getInputStream();  
            // 创建文档  
            wb = new HSSFWorkbook(input);                         
            //读取sheet(页)  
            for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){  
                HSSFSheet hssfSheet = wb.getSheetAt(numSheet);  
                if(hssfSheet == null){  
                    continue;  
                }  
                totalRows = hssfSheet.getLastRowNum(); 
                //读取Row,从第二行开始  
                for(int rowNum = 1;rowNum < totalRows+1;rowNum++){  
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);  
                    if(hssfRow!=null){  
                        rowList = new ArrayList<String>();  
                        totalCells = hssfRow.getLastCellNum();  
                        //读取列，从第一列开始  
                        for(short c=0;c<totalCells;c++){  
                            HSSFCell cell = hssfRow.getCell(c);  
                            if(cell==null){  
                                rowList.add(ExcelUtil.EMPTY);  
                                continue;  
                            }                             
//                            rowList.add(ExcelUtil.getHValue(cell).trim());  
                            String str = ExcelUtil.getHValue(cell);
                            if (str != null) {
								rowList.add(str.trim());
							}else{
								rowList.add(str);
							}
                        }    
                        list.add(rowList);  
                    }    
                    log.debug("Uploaded Excel Per Row:"+rowList);
                }
            } 
            log.debug("Upload Excel readXlsx end");
            return list;  
        } catch (IOException e) {             
        	log.error("Excel Read error:", e);  
        } finally{  
            try {  
                input.close();  
            } catch (IOException e) {  
            	log.error("InputStream close error:", e);  
            }  
        }  
        return null;  
    }
}
