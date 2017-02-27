package com.wondertek.cpm.web.rest;

import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.ProjectSupportBonus;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.domain.vo.ProjectSupportBonusVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ProjectOverallService;
import com.wondertek.cpm.service.ProjectSupportBonusService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

/**
 * REST controller for managing ProductPrice.
 */
@RestController
@RequestMapping("/api")
public class ProjectSupportBonusResource {

    private final Logger log = LoggerFactory.getLogger(ProjectSupportBonusResource.class);
    private final DecimalFormat doubleFormat = new DecimalFormat("#0.00");   
    @Inject
    private ProjectSupportBonusService projectSupportBonusService;
    
	/**
	 * 列表页
	 * @author sunshine
	 * @Description :
	 */
    @GetMapping("/project-support-bonus")
    @Timed
    public ResponseEntity<List<ProjectSupportBonusVo>> getAllProjectSupportBonusByParams(
    		@RequestParam(value = "statWeek",required=false) Long statWeek,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@RequestParam(value = "deptType",required=false) Long deptType,
    		@ApiParam Pageable pageable)
		throws URISyntaxException {
		log.debug("REST request to get a page of ProjectSupportBonus");
		Date now = new Date();
		ProjectSupportBonus projectSupportBonus = new ProjectSupportBonus();
		projectSupportBonus.setStatWeek(statWeek);
		projectSupportBonus.setContractId(contractId);
		projectSupportBonus.setDeptType(deptType);
		if(projectSupportBonus.getStatWeek() == null){//默认当前天对的的周日
			projectSupportBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
         }else {//更改为对应日期的周日
        	 projectSupportBonus.setStatWeek(StringUtil.nullToLong(
         			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
         					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+projectSupportBonus.getStatWeek())))));
		}
		Page<ProjectSupportBonusVo> page = projectSupportBonusService.searchPage(projectSupportBonus,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(statWeek.toString(), page,"/api/project-support-bonus");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);    	
    }

    @GetMapping("/project-support-bonus/queryDetail")
    @Timed
    public ResponseEntity<List<ProjectSupportBonusVo>> getProjectSupportBonusDetail(
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@ApiParam Pageable pageable) 
    	throws URISyntaxException {
        log.debug("REST request to get ProjectOverall : {}", contractId);
        Page<ProjectSupportBonusVo> page = projectSupportBonusService.searchPageDetail(contractId,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(contractId.toString(), page,"/api/project-projectOverall");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK); 
    }
    
    @GetMapping("/project-support-bonus/{id}")
    @Timed
    public ResponseEntity<ProjectSupportBonus> getProjectSupportBonusById(@PathVariable Long id) {
        log.debug("REST request to get ProjectSupportBonus : {}", id);
        ProjectSupportBonus projectSupportBonus = projectSupportBonusService.findOne(id);
        return Optional.ofNullable(projectSupportBonus)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @RequestMapping("/project-support-bonus/exportXls")
    @Timed
    public void exportXls(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value = "statWeek",required=false) Long statWeek,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@RequestParam(value = "deptType",required=false) Long deptType,
    		@ApiParam Pageable pageable)
		throws URISyntaxException, IOException {
    	log.debug("REST request to get a page of exportXls");
    	Date now = new Date();
    	ProjectSupportBonus projectSupportBonus = new ProjectSupportBonus();
    	projectSupportBonus.setStatWeek(statWeek);
    	projectSupportBonus.setContractId(contractId);
    	projectSupportBonus.setDeptType(deptType);
    	String currentDay = StringUtil.null2Str(
    			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, now));
    	 
    	if(projectSupportBonus.getStatWeek() == null){//默认当前天对的的周日
    		projectSupportBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
         }else {//更改为对应日期的周日
        	 projectSupportBonus.setStatWeek(StringUtil.nullToLong(
         			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
         					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+projectSupportBonus.getStatWeek())))));
		}
    	 Page<ProjectSupportBonusVo> page = projectSupportBonusService.searchPage(projectSupportBonus,pageable);
         List<ProjectSupportBonusVo> list = new ArrayList<ProjectSupportBonusVo>();
         for (ProjectSupportBonusVo projectSupportBonusVo : page.getContent()) {
 			list.add(projectSupportBonusVo);
 		}

         //拼接sheet数据
     	//标题
     	String[] heads = new String[]{
     			"合同编号",
     			"部门类型",
     			"合同金额",
     			"税率",
     			"验收节点",
     			"合同确认交付时间",
     			"计划天数",
     			"实际使用天数",
     			"奖金调节率",
     			"奖金比率",
     			"奖金确认比例",
     			"奖金基数",
     			"当期奖金"
     	};
     	//设置文件名
     	String fileName = "项目支撑奖金" +"_" + currentDay + ".xlsx";
     	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
    	
    	ExcelWrite excelWrite = new ExcelWrite();
    	excelWrite.createSheetTitle("项目总体控制", 1, heads);
    	//写入数据
    	if(list != null){
    		handleSheetData(list,2,excelWrite);
    	}
    	excelWrite.close(outputStream);
    }
    
	/**
     * 处理sheet数据
     */
    private void handleSheetData(List<ProjectSupportBonusVo> list, int startRow,
			ExcelWrite excelWrite) {
    	//除表头外的其他数据单元格格式
    	Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
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
		for (ProjectSupportBonusVo vo : list) {
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
			if (vo.getDeptTypeName() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getDeptTypeName());
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
			if (vo.getTaxRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getTaxRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getAcceptanceRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getAcceptanceRate() / 100);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getDeliveryTime() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getDeliveryTime());
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getPlanDays() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getPlanDays());
			}
			j++;
			
			cell = row.createCell(j,cellType[j]);
			if (vo.getRealDays() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getRealDays());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getBonusAdjustRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getBonusAdjustRate() / 100);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getBonusRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getBonusRate() / 100);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getBonusAcceptanceRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getBonusAcceptanceRate() / 100);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getBonusBasis() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getBonusBasis());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getCurrentBonus() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getCurrentBonus());
			}
			j++;
		}
	}

}
