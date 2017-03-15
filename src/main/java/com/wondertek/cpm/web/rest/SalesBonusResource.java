package com.wondertek.cpm.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.SalesBonusVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.service.SalesBonusService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing SalesBonus.
 */
@RestController
@RequestMapping("/api")
public class SalesBonusResource {

    private final Logger log = LoggerFactory.getLogger(SalesBonusResource.class);
    @Inject
    private SalesBonusService salesBonusService;
    @Inject
    private ContractInfoService contractInfoService;
    @Inject
    private UserService userService;
    
    @GetMapping("/sales-bonus/queryDetail")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_SALES_BONUS)
    public ResponseEntity<List<SalesBonusVo>> getSalesBonusDetailPage(
    		@RequestParam(value = "id",required=false) Long id, //主键
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get SalesBonus Detail Page by id : {}", id);
    	SalesBonusVo salesBonusVo = salesBonusService.getUserSalesBonus(id);
    	if(salesBonusVo == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
        SalesBonus salesBonus = new SalesBonus();
        salesBonus.setContractId(salesBonusVo.getContractId());
        
        Page<SalesBonusVo> page = salesBonusService.getUserDetailPage(salesBonus,pageable);
        for (SalesBonusVo salesBonusVo2 : page.getContent()) {
			salesBonusVo2.setAnnualIndex(StringUtil.getScaleDouble(salesBonusVo2.getAnnualIndex(), 10000d, 2));
			salesBonusVo2.setFinishTotal(StringUtil.getScaleDouble(salesBonusVo2.getFinishTotal(), 10000d, 2));
			salesBonusVo2.setContractAmount(StringUtil.getScaleDouble(salesBonusVo2.getContractAmount(), 10000d, 2));
			salesBonusVo2.setReceiveTotal(StringUtil.getScaleDouble(salesBonusVo2.getReceiveTotal(), 10000d, 2));
			salesBonusVo2.setTaxes(StringUtil.getScaleDouble(salesBonusVo2.getTaxes(), 10000d, 2));
			salesBonusVo2.setShareCost(StringUtil.getScaleDouble(salesBonusVo2.getShareCost(), 10000d, 2));
			salesBonusVo2.setThirdPartyPurchase(StringUtil.getScaleDouble(salesBonusVo2.getThirdPartyPurchase(), 10000d, 2));
			salesBonusVo2.setBonusBasis(StringUtil.getScaleDouble(salesBonusVo2.getBonusBasis(), 10000d, 2));
			salesBonusVo2.setCurrentBonus(StringUtil.getScaleDouble(salesBonusVo2.getCurrentBonus(), 10000d, 2));
			salesBonusVo2.setTotalBonus(StringUtil.getScaleDouble(salesBonusVo2.getTotalBonus(), 10000d, 2));
			salesBonusVo2.setPayBonus(StringUtil.getScaleDouble(salesBonusVo2.getPayBonus(), 10000d, 2));
		}
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sales-bonus/queryDetail");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * 列表页
     */
    @GetMapping("/sales-bonus")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_SALES_BONUS)
    public ResponseEntity<List<SalesBonusVo>> getSalesBonusPage(
    		@RequestParam(value = "originYear",required=false) Long originYear, //年份，默认当前年份
    		@RequestParam(value = "statWeek",required=false) Long statWeek, 	//统计日期，默认当前
    		@RequestParam(value = "contractId",required=false) Long contractId, //合同主键
    		@RequestParam(value = "salesManId",required=false) Long salesManId //销售
    		)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of getSalesBonusPage by originYear : {}, statWeek : {}, "
        		+ "contractId : {}, salesManId : {}", originYear, statWeek, contractId, salesManId);
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
        for (SalesBonusVo salesBonusVo : page) {
			salesBonusVo.setAnnualIndex(StringUtil.getScaleDouble(salesBonusVo.getAnnualIndex(), 10000d, 2));
			salesBonusVo.setFinishTotal(StringUtil.getScaleDouble(salesBonusVo.getFinishTotal(), 10000d, 2));
			salesBonusVo.setContractAmount(StringUtil.getScaleDouble(salesBonusVo.getContractAmount(), 10000d, 2));
			salesBonusVo.setReceiveTotal(StringUtil.getScaleDouble(salesBonusVo.getReceiveTotal(), 10000d, 2));
			salesBonusVo.setTaxes(StringUtil.getScaleDouble(salesBonusVo.getTaxes(), 10000d, 2));
			salesBonusVo.setShareCost(StringUtil.getScaleDouble(salesBonusVo.getShareCost(), 10000d, 2));
			salesBonusVo.setThirdPartyPurchase(StringUtil.getScaleDouble(salesBonusVo.getThirdPartyPurchase(), 10000d, 2));
			salesBonusVo.setBonusBasis(StringUtil.getScaleDouble(salesBonusVo.getBonusBasis(), 10000d, 2));
			salesBonusVo.setCurrentBonus(StringUtil.getScaleDouble(salesBonusVo.getCurrentBonus(), 10000d, 2));
			salesBonusVo.setTotalBonus(StringUtil.getScaleDouble(salesBonusVo.getTotalBonus(), 10000d, 2));
			salesBonusVo.setPayBonus(StringUtil.getScaleDouble(salesBonusVo.getPayBonus(), 10000d, 2));
		}
        
        return new ResponseEntity<>(page, new HttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping("/sales-bonus/exportXls")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_SALES_BONUS)
    public void exportXls(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value = "originYear",required=false) Long originYear, //年份，默认当前年份
    		@RequestParam(value = "statWeek",required=false) Long statWeek, 	//统计日期，默认当前
    		@RequestParam(value = "contractId",required=false) Long contractId, //合同主键
    		@RequestParam(value = "salesManId",required=false) Long salesManId //销售
    	)throws URISyntaxException, IOException {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of exportXls by originYear : {}, statWeek : {}, "
    			+ "contractId : {}, salesManId : {}", originYear, statWeek, contractId, salesManId);
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
    	String fileName = "销售_" + salesBonus.getOriginYear() + "_" + salesBonus.getStatWeek() + ".xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
		
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("销售", 1, heads);
    	//写入数据
    	if(page != null){
    		handleSheetData(page,2,excelWrite,salesBonus);
    	}
    	excelWrite.close(outputStream);
    }
    /**
     * 处理sheet数据
     * @param salesBonus 
     */
	private void handleSheetData(List<SalesBonusVo> page, int startRow, ExcelWrite excelWrite, SalesBonus salesBonus) {
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
		//导出条件
		i++;
		createFoot(wb,sheet,i+startRow-1,salesBonus);
	}
	/**
	 * 导出条件
	 * @param wb 
	 */
	private void createFoot(XSSFWorkbook wb, XSSFSheet sheet, int rownum, SalesBonus salesBonus) {
		//空白行
		sheet.createRow(rownum);
		rownum ++;
		
		XSSFRow row = sheet.createRow(rownum);
		rownum ++;
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("导出条件");
				
		row = sheet.createRow(rownum);
		rownum ++;
		cell = row.createCell(1);
		cell.setCellValue("所属年份:");
		cell = row.createCell(2);
		cell.setCellValue(salesBonus.getOriginYear());
		
		row = sheet.createRow(rownum);
		rownum ++;
		cell = row.createCell(1);
		cell.setCellValue("截止日期:");
		cell = row.createCell(2);
		cell.setCellValue(salesBonus.getStatWeek());
		
		row = sheet.createRow(rownum);
		rownum ++;
		cell = row.createCell(1);
		cell.setCellValue("合同信息:");
		cell = row.createCell(2);
		if(salesBonus.getContractId() != null){
			ContractInfo info = contractInfoService.findOne(salesBonus.getContractId());
			if(info != null){
				cell.setCellValue(info.getSerialNum());
			}else{
				cell.setCellValue("");
			}
		}else{
			cell.setCellValue("");
		}
		
		row = sheet.createRow(rownum);
		rownum ++;
		cell = row.createCell(1);
		cell.setCellValue("销售:");
		cell = row.createCell(2);
		if(salesBonus.getSalesManId() != null){
			User info = userService.getUserWithAuthorities(salesBonus.getSalesManId());
			if(info != null){
				cell.setCellValue(info.getLastName());
			}else{
				cell.setCellValue("");
			}
		}else{
			cell.setCellValue("");
		}
		
		row = sheet.createRow(rownum);
		rownum ++;
		cell = row.createCell(1);
		cell.setCellValue("导出时间:");
		cell = row.createCell(2);
		cell.setCellValue(new Date());
		//时间格式
		XSSFCellStyle cellStyle = wb.createCellStyle();  
		XSSFDataFormat format= wb.createDataFormat();  
        cellStyle.setDataFormat(format.getFormat("yyyy/m/d h:mm"));
        cell.setCellStyle(cellStyle);
        
	}
}
