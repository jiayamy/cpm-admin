package com.wondertek.cpm.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.vo.SalesBonusVo;
import com.wondertek.cpm.service.SalesBonusService;

/**
 * REST controller for managing SalesBonus.
 */
@RestController
@RequestMapping("/api")
public class SalesBonusResource {

    private final Logger log = LoggerFactory.getLogger(SalesBonusResource.class);
    @Inject
    private SalesBonusService salesBonusService;
    /**
     * 列表页
     */
    @GetMapping("/sales-bonus")
    @Timed
    public ResponseEntity<List<SalesBonusVo>> getSalesBonusPage(
    		@RequestParam(value = "originYear",required=false) Long originYear, //年份，默认当前年份
    		@RequestParam(value = "statWeek",required=false) Long statWeek, 	//统计日期，默认当前
    		@RequestParam(value = "contractId",required=false) Long contractId, //合同主键
    		@RequestParam(value = "salesManId",required=false) Long salesManId //销售
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of getSalesBonusPage");
        SalesBonus salesBonus = new SalesBonus();
        salesBonus.setOriginYear(originYear);
        salesBonus.setStatWeek(statWeek);
        salesBonus.setContractId(contractId);
        salesBonus.setSalesManId(salesManId);
        Date now = new Date();
        if(salesBonus.getOriginYear() == null){//默认当前年份
        	salesBonus.setOriginYear(StringUtil.nullToLong(DateUtil.formatDate("yyyy", now)));
        }
        if(salesBonus.getStatWeek() == null){//默认当前天
        	salesBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
        }else{//更改为对应日期的周日
        	salesBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+salesBonus.getStatWeek())))));
        }
        
        List<SalesBonusVo> page = salesBonusService.getUserPage(salesBonus);
        
        return new ResponseEntity<>(page, new HttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping("/sales-bonus/exportXls")
    @Timed
    public void exportXls(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value = "originYear",required=false) Long originYear, //年份，默认当前年份
    		@RequestParam(value = "statWeek",required=false) Long statWeek, 	//统计日期，默认当前
    		@RequestParam(value = "contractId",required=false) Long contractId, //合同主键
    		@RequestParam(value = "salesManId",required=false) Long salesManId //销售
    	)throws URISyntaxException, IOException {
    	log.debug("REST request to get a page of exportXls");
    	SalesBonus salesBonus = new SalesBonus();
        salesBonus.setOriginYear(originYear);
        salesBonus.setStatWeek(statWeek);
        salesBonus.setContractId(contractId);
        salesBonus.setSalesManId(salesManId);
        Date now = new Date();
        if(salesBonus.getOriginYear() == null){//默认当前年份
        	salesBonus.setOriginYear(StringUtil.nullToLong(DateUtil.formatDate("yyyy", now)));
        }
        if(salesBonus.getStatWeek() == null){//默认当前天
        	salesBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
        }else{//更改为对应日期的周日
        	salesBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+salesBonus.getStatWeek())))));
        }
    	List<SalesBonusVo> page = salesBonusService.getUserPage(salesBonus);
    	//拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"销售",
    			"合同年指标",
    			"合同累计完成金额",
    			"合同编号",
    			"合同金额",
    			"税率",
    			"收款金额",
    			"税收",
    			"公摊成本",
    			"第三方采购",
    			"奖金基数",
    			"奖金比例",
    			"本期奖金",
    			"累计已计提奖金",
    			"合同累计完成率",
    			"可发放奖金"
    	};
    	String fileName = "salesBonus_" + salesBonus.getOriginYear() + "_" + salesBonus.getStatWeek() + ".xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + fileName);
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
		
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("销售项目", 1, heads);
    	//写入数据
    	if(page != null){
    		handleSheetData(page,2,excelWrite);
    	}
    	excelWrite.close(outputStream);
    }
    /**
     * 处理sheet数据
     */
	private void handleSheetData(List<SalesBonusVo> page, int startRow, ExcelWrite excelWrite) {
		//除表头外的其他数据单元格格式
    	Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC
    	};
    	XSSFSheet sheet = excelWrite.getCurrentSheet();
    	XSSFWorkbook wb = excelWrite.getXSSFWorkbook();
		XSSFRow row = null;
		XSSFCell cell = null;
		int i = -1;
		int j = 0;
		//百分比格式
		XSSFCellStyle cellStyle = wb.createCellStyle();  
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%")); 
		//数据
		for(SalesBonusVo vo : page){
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if(vo.getSalesMan() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getSalesMan());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getAnnualIndex() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getAnnualIndex());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getFinishTotal() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getFinishTotal());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractAmount() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractAmount());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getTaxRate() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getTaxRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getReceiveTotal() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getReceiveTotal());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getTaxes() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getTaxes());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getShareCost() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getShareCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getThirdPartyPurchase() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getThirdPartyPurchase());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getBonusBasis() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getBonusBasis());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getBonusRate() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getBonusRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getCurrentBonus() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getCurrentBonus());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getTotalBonus() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getTotalBonus());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getFinishRate() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getFinishRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getPayBonus() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getPayBonus());
			}
			j++;
		}
	}
}
