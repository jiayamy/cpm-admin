package com.wondertek.cpm.web.rest;

import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.Bonus;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.vo.BonusVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.service.OverallBonusService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

/**
 * REST controller for managing ProductPrice.
 */
@RestController
@RequestMapping("/api")
public class OverallBonusResource {

    private final Logger log = LoggerFactory.getLogger(OverallBonusResource.class);
    private final DecimalFormat doubleFormat = new DecimalFormat("#0.00");   
    @Inject
    private OverallBonusService overallBonusService;
    
	/**
	 * 列表页
	 * @author sunshine
	 * @Description :
	 */
    @GetMapping("/overall-bonus")
    @Timed
    public ResponseEntity<List<BonusVo>> getAllBonusByParams(
    		@RequestParam(value = "statWeek",required=false) Long statWeek,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@ApiParam Pageable pageable)
		throws URISyntaxException {
		log.debug("REST request to get a page of ProjectOverallVo");
		Date now = new Date();
		Bonus bonus = new Bonus();
		bonus.setStatWeek(statWeek);
		bonus.setContractId(contractId);
		if(bonus.getStatWeek() == null){//默认当前天对的的周日
			bonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
         }else {//更改为对应日期的周日
        	 bonus.setStatWeek(StringUtil.nullToLong(
         			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
         					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+bonus.getStatWeek())))));
		}
		Page<BonusVo> page = overallBonusService.searchPage(bonus,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(statWeek.toString(), page,"/api/project-projectOverall");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);    	
    }
    
    @GetMapping("/overall-bonus/queryDetail")
    @Timed
    public ResponseEntity<List<BonusVo>> getBonusVoDetail(
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@ApiParam Pageable pageable) 
    	throws URISyntaxException {
        log.debug("REST request to get ProjectOverall : {}", contractId);
        Page<BonusVo> page = overallBonusService.searchPageDetail(contractId,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(contractId.toString(), page,"/api/overall-bonus");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK); 
    }
    
    @GetMapping("/overall-bonus/{id}")
    @Timed
    public ResponseEntity<Bonus> getBonus(@PathVariable Long id) {
        log.debug("REST request to get ProjectOverall : {}", id);
        Bonus bonus = overallBonusService.findOne(id);
        return Optional.ofNullable(bonus)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @RequestMapping("/overall-bonus/exportXls")
    @Timed
    public void exportXls(
    		HttpServletRequest request,HttpServletResponse response,
    		@RequestParam(value = "statWeek",required=false) Long statWeek,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@ApiParam Pageable pageable)
    	throws URISyntaxException, IOException  {
    	log.debug("REST request to get a page of exportXls");
    	Date now = new Date();
    	Bonus bonus = new Bonus();
    	bonus.setStatWeek(statWeek);
    	bonus.setContractId(contractId);
    	String currentDay = StringUtil.null2Str(
    			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN,now));
    	
    	if(bonus.getStatWeek() == null){//默认当前天对的的周日
    		bonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
         }else {//更改为对应日期的周日
        	 bonus.setStatWeek(StringUtil.nullToLong(
         			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
         					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+bonus.getStatWeek())))));
		}
		Page<BonusVo> page = overallBonusService.searchPage(bonus,pageable);
		List<BonusVo> list = new ArrayList<BonusVo>();
		for (BonusVo bonusVo : page.getContent()) {
			list.add(bonusVo);
		}
		
		//拼接sheet数据
		//标题
		String[] heads = new String[]{
				"合同编号",
				"合同金额",
				"当期销售奖金",
				"当期项目实施奖金",
				"当期研发奖金",
				"当期业务咨询奖金",
				"奖金合计"
		};
		//设置文件名
		String fileName = "总体奖金" + "_" + currentDay + ".xlsx";
		//写入sheet
		ServletOutputStream outputStream = response.getOutputStream();
		response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes("gb2312"),"ISO8859-1"));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
    	
    	ExcelWrite excelWrite = new ExcelWrite();
    	excelWrite.createSheetTitle("奖金总览", 1, heads);
    	//写入数据
    	if (list != null) {
    		handleSheetData(list,2,excelWrite);
		}
    	excelWrite.close(outputStream);
    }
    
   /*
    * 处理sheet数据
    */
    private void handleSheetData(List<BonusVo> list, int startRow,
			ExcelWrite excelWrite) {
    	//除表头外的其他数据单元格格式
    	Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
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
		//写入后台数据
		for (BonusVo vo : list) {
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if (vo.getSerialNum() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getContractAmount() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getContractAmount());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getSalesBonus() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getSalesBonus());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getImplemtationBonus() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getImplemtationBonus());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getAcademicBonus() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getAcademicBonus());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getConsultantsBonus() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getConsultantsBonus());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getBonusTotal() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getBonusTotal());
			}
			j++;
		}
	}

}
