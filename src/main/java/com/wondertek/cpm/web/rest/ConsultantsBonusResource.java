package com.wondertek.cpm.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.vo.ConsultantsBonusVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ConsultantsBonusService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ConsultantsBonus.
 */
@RestController
@RequestMapping("/api")
public class ConsultantsBonusResource {

	private final Logger log = LoggerFactory.getLogger(ConsultantsBonusResource.class);
	
	@Inject
	private ConsultantsBonusService consultantsBonusService;
	
	/**
     * GET  /consultant-bonus : get all the ConsultantsBonusVo.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ConsultantsBonusVo in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/consultant-bonus")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONSULTANT_BONUS)
    public ResponseEntity<List<ConsultantsBonusVo>> getAllConsultantsBonus(
    		@RequestParam(value="contractId",required = false) Long contractId,
    		@RequestParam(value="consultantsId",required = false) Long consultantsId,
    		@RequestParam(value="statWeek",required = false) String statWeek,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to get a page of ConsultantsBonus : contractId:{},consultantsId:{},statWeek:{}",contractId,consultantsId,statWeek);
        if(StringUtil.isNullStr(statWeek)){//搜索日期条件为空时，默认截止日期为当前日期的周末
        	Date date = new Date();
        	date = DateUtil.getSundayOfDay(date);
        	statWeek = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, date);
        }else{
        	Date date = DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, statWeek.trim()));
        	statWeek = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, date);
        }
        ConsultantsBonus searchParams = new ConsultantsBonus();
        searchParams.setContractId(contractId);
        searchParams.setConsultantsId(consultantsId);
        searchParams.setStatWeek(StringUtil.nullToLong(statWeek));
        Page<ConsultantsBonusVo> page = consultantsBonusService.getConsultantsBonusPage(searchParams, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/consultant-bonus");
        return Optional.ofNullable(page.getContent()).map(result -> new ResponseEntity<>(result,headers,HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * GET  /consultant-bonus/:id : get the "id" ConsultantsBonus.
     *
     * @param id the id of the consultantsBonus to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the consultantsBonus, or with status 404 (Not Found)
     */
    @GetMapping("/consultant-bonus/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONSULTANT_BONUS)
    public ResponseEntity<ConsultantsBonus> getConsultantsBonus(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to getConsultantsBonus : {}",id);
        ConsultantsBonus consultantsBonus = consultantsBonusService.findOne(id);
        return Optional.ofNullable(consultantsBonus)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/consultant-bonus/queryConsultantRecord")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONSULTANT_BONUS)
    public ResponseEntity<List<ConsultantsBonusVo>> queryConsultantRecord(
    			@RequestParam(value="contId",required = false) Long contractId,
    			@ApiParam Pageable pageable) throws URISyntaxException{
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to queryConsultantRecord : contractId:{}",contractId);
    	if(contractId == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	//设置statWeek默认值
    	Date date = new Date();
    	date = DateUtil.getSundayOfDay(date);
    	Long statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, date));
    	ConsultantsBonus searchParams = new ConsultantsBonus();
    	searchParams.setContractId(contractId);
    	searchParams.setStatWeek(statWeek);
    	Page<ConsultantsBonusVo> page = consultantsBonusService.getConsultantsBonusRecordPage(searchParams, pageable);
    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/consultant-bonus/queryConsultantRecord");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * 下载excel(所有合同的最新记录)
     * @param request
     * @param response
     * @param contractId
     * @param consultantsNameId
     * @param statWeek
     * @throws IOException
     */
    @GetMapping("/consultant-bonus/exportXls")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONSULTANT_BONUS)
    public void exportXls(
	    		HttpServletRequest request, HttpServletResponse response,
	    		@RequestParam(value="contractId",required = false) Long contractId,
	    		@RequestParam(value="consultantsId",required = false) Long consultantsId,
	    		@RequestParam(value="statWeek",required = false) Long statWeek
    		) throws IOException{
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to exportXls : contractId:{},consultantsId:{},statWeek:{}",contractId,consultantsId,statWeek);
    	if(statWeek == null){//搜索日期条件为空时，默认截止日期为当前日期的周末
        	Date date = new Date();
        	date = DateUtil.getSundayOfDay(date);
        	statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, date));
        }else{
        	Date date = DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, statWeek.toString().trim()));
        	statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, date));
        }
    	ConsultantsBonus searchParams = new ConsultantsBonus();
        searchParams.setContractId(contractId);
        searchParams.setConsultantsId(consultantsId);
        searchParams.setStatWeek(statWeek);
    	List<ConsultantsBonusVo> page = consultantsBonusService.getConsultantsBonusData(searchParams);
    	//拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"项目负责人",
    			"合同编号",
    			"金额",
    			"奖金基数",
    			"奖金比例",
    			"项目分润比率",
    			"本期奖金",
    			"累计已计提奖金"
    	};
    	String fileName = "咨询奖金_" + statWeek + ".xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
    	
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("咨询", 1, heads);
    	//写入数据
    	if(page != null){
    		handleSheetData(page,2,excelWrite);
    	}
    	excelWrite.close(outputStream);
    }
    
    /**
     * 下载excel(某合同的详情记录 )
     */
    @GetMapping("/consultant-bonus/contractRecord/exportXls")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONSULTANT_BONUS)
    public void exportRecordDetailXls(
	    		HttpServletRequest request, HttpServletResponse response,
	    		@RequestParam(value="contractId",required = false) Long contractId
    		) throws IOException{
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to exportRecordDetailXls : contractId:{}",contractId);
    	//设置默认截止日期
    	Date date = new Date();
    	date = DateUtil.getSundayOfDay(date);
    	Long statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, date));
    	List<ConsultantsBonusVo> page = consultantsBonusService.getConsultantsBonusDetailList(contractId,statWeek);
    	//拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"项目负责人",
    			"合同编号",
    			"金额",
    			"奖金基数",
    			"奖金比例",
    			"项目分润比率",
    			"本期奖金",
    			"累计已计提奖金"
    	};
    	String fileName = "咨询详情.xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + fileName);
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
    	
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("咨询详情", 1, heads);
    	//写入数据
    	if(page != null){
    		handleSheetData(page,2,excelWrite);
    	}
    	excelWrite.close(outputStream);
    }
    
    /**
     * 处理sheet数据
     */
	private void handleSheetData(List<ConsultantsBonusVo> page,int startRow,ExcelWrite excelWrite) {
		//除表头外的其他数据单元格格式
    	Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING
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
		for(ConsultantsBonusVo vo : page){
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if(vo.getConsultantsName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getConsultantsName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getSerialNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getAmount() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getAmount());
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
			if(vo.getConsultantsShareRate() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getConsultantsShareRate() / 100);
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
			if(vo.getAccumulationBonus() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getAccumulationBonus());
			}
			j++;
		}
	}
}
