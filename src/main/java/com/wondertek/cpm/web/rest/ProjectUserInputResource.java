package com.wondertek.cpm.web.rest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.wondertek.cpm.domain.vo.ProjectUserInputVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.UserTimesheetService;

@RestController
@RequestMapping("/api")
public class ProjectUserInputResource {

	private final Logger log = LoggerFactory.getLogger(ProjectUserInputResource.class);
	
	@Inject
	private UserTimesheetService userTimesheetService;
	
	@GetMapping("/project-user-input")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_STAT_PROJECT_USER_INPUT)
	public ResponseEntity<List<ProjectUserInputVo>> getAllProjectUserInputs(
			@RequestParam(value="startTime",required = false) Long startTime,
			@RequestParam(value="endTime",required = false) Long endTime,
			@RequestParam(value="userId",required = false) List<Long> userIds,
			@RequestParam(value="showTotal",required = false) Boolean showTotal){
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get datas of ProjectUserInputs by startTime : {}, endTime : {}, "
        		+ "userIds : {},showTotal : {}", startTime, endTime, userIds, showTotal);
		Date now = new Date();
		if(startTime == null){//默认开始时间为本月初
			startTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.getFirstDayOfMonth(now)));
		}
		if(endTime == null){//默认结束时间为当天
			endTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, now));
		}
		if(showTotal == null){
			showTotal = Boolean.FALSE;
		}
		List<ProjectUserInputVo> inputVos = userTimesheetService.getProjectUserInputsByParam(startTime, endTime, userIds, showTotal);
		return new ResponseEntity<List<ProjectUserInputVo>>(inputVos, HttpStatus.OK);
	}
	
	@RequestMapping("/project-user-input/exportXls")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_STAT_PROJECT_USER_INPUT)
	public void exportXls(
			HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="startTime",required=false) Long startTime,
			@RequestParam(value="endTime",required=false) Long endTime,
			@RequestParam(value="userId",required=false) List<Long> userIds,
			@RequestParam(value="showTotal",required = false) Boolean showTotal) throws IOException{
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to export ProjectUserInputs by startTime : {}, endTime : {}, "
        		+ "userIds : {}, showTotal: {}", startTime, endTime, userIds, showTotal);
		Date now = new Date();
		if(startTime == null){//默认开始时间为本月初
			startTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.getFirstDayOfMonth(now)));
		}
		if(endTime == null){//默认结束时间为当天
			endTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, now));
		}
		if(showTotal == null){
			showTotal = Boolean.FALSE;
		}
		List<ProjectUserInputVo> userInputVos = userTimesheetService.getProjectUserInputsByParam(startTime, endTime, userIds, showTotal);
		//拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"项目编号",
    			"项目名称",
    			"合同编号",
    			"合同名称",
    			"项目经理工号",
    			"项目经理",
    			"项目经理部门",
    			"员工工号",
    			"员工姓名",
    			"正常工时",
    			"认可正常工时",
    			"加班工时",
    			"认可加班工时"
    	};
    	String fileName = "项目人员工时.xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
		
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("销售", 1, heads);
    	//写入数据
    	if(userInputVos != null){
    		handleSheetData(userInputVos,2,excelWrite);
    	}
    	excelWrite.close(outputStream);
	}
	
	/**
     * 处理sheet数据
     */
	private void handleSheetData(List<ProjectUserInputVo> userInputVos, int startRow, ExcelWrite excelWrite) {
		//除表头外的其他数据单元格格式
    	Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC
    	};
    	XSSFSheet sheet = excelWrite.getCurrentSheet();
    	sheet.setColumnWidth(0, 3745);
    	sheet.setColumnWidth(1, 6420);
    	sheet.setColumnWidth(2, 3745);
    	sheet.setColumnWidth(3, 6420);
    	sheet.setColumnWidth(4, 3345);
    	sheet.setColumnWidth(6, 4020);
    	sheet.setColumnWidth(10, 3210);
    	sheet.setColumnWidth(12, 3210);
		XSSFRow row = null;
		XSSFCell cell = null;
		int i = -1;
		int j = 0;
		//数据
		for(ProjectUserInputVo vo : userInputVos){
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if(vo.getProjectSerialNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getProjectSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getProjectName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getProjectName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractSerialNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getPmSerialNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getPmSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getPmName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getPmName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getPmDeptType() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getPmDeptType());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getUserSerialNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getUserSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getUserName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getUserName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getRealInput() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getRealInput());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getAcceptInput() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getAcceptInput());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getExtraInput() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getExtraInput());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getAcceptExtraInput() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getAcceptExtraInput());
			}
			j++;
		}
	}
}
