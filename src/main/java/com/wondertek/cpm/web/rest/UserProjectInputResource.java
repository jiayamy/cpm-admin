package com.wondertek.cpm.web.rest;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.vo.UserProjectInputVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.UserProjectInputService;

@RestController
@RequestMapping("/api")
public class UserProjectInputResource {
	
	private Logger log = LoggerFactory.getLogger(UserProjectInputResource.class);
	
	@Inject
	private UserProjectInputService userProjectInputService;

	@RequestMapping("/user-project-input")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_STAT_USER_PROJECT_INPUT)
	public ResponseEntity<List<UserProjectInputVo>> getUserProjectInputs(
			@RequestParam(value="startTime",required = false) Long startTime,
			@RequestParam(value="endTime",required = false) Long endTime,
			@RequestParam(value="userId",required = false) List<Long> userIds,
			@RequestParam(value="projectId",required = false) List<Long> projectIds,
			@RequestParam(value="showTotal",required = false) Boolean showTotal){
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get datas of ProjectUserInputs by startTime : {}, endTime : {}, "
        		+ "userIds : {},projectId : {},showTotal : {}", startTime, endTime, userIds, projectIds, showTotal);
		Date now = new Date();
		if(startTime == null){//默认本月初
			startTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.getFirstDayOfMonth(now)));
		}
		if(endTime == null){//默认当天
			endTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, now));
		}
		if(startTime > endTime){
			return new ResponseEntity<List<UserProjectInputVo>>(new ArrayList<UserProjectInputVo>(), HttpStatus.OK);
		}
		if(showTotal == null){//默认不显示合计
			showTotal = Boolean.FALSE;
		}
		List<UserProjectInputVo> result = userProjectInputService.getUserProjectInputs(startTime,endTime,userIds,projectIds,showTotal);
		return new ResponseEntity<List<UserProjectInputVo>>(result, HttpStatus.OK);
	}
	
	@RequestMapping("/user-project-input/exportXls")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_STAT_USER_PROJECT_INPUT)
	public void exportXls(
			HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="startTime",required = false) Long startTime,
			@RequestParam(value="endTime",required = false) Long endTime,
			@RequestParam(value="userId",required = false) List<Long> userIds,
			@RequestParam(value="projectId",required = false) List<Long> projectIds,
			@RequestParam(value="showTotal",required = false) Boolean showTotal) throws IOException{
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to exprot ProjectUserInputs by startTime : {}, endTime : {}, "
        		+ "userIds : {},projectId : {},showTotal : {}", startTime, endTime, userIds, projectIds, showTotal);
		Date now = new Date();
		if(startTime == null){//默认本月初
			startTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.getFirstDayOfMonth(now)));
		}
		if(endTime == null){//默认当天
			endTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, now));
		}
		if(showTotal == null){//默认不显示合计
			showTotal = Boolean.FALSE;
		}
		long start = System.currentTimeMillis();
		List<UserProjectInputVo> inputVos = userProjectInputService.getUserProjectInputs(startTime,endTime,userIds,projectIds,showTotal);
		long end1 = System.currentTimeMillis();
		//拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"员工工号",
    			"员工姓名",
    			"项目编号",
    			"项目名称",
    			"合同编号",
    			"合同名称",
    			"项目经理工号",
    			"项目经理",
    			"项目经理部门",
    			"正常工时",
    			"认可正常工时",
    			"加班工时",
    			"认可加班工时"
    	};
    	String fileName = "人员项目工时_" + startTime + "-" + endTime + ".xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
		
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("人员项目工时_" + startTime + "-" + endTime, 1, heads);
    	//写入数据
    	long end2= System.currentTimeMillis();
    	if(inputVos != null){
    		handleSheetData(inputVos,2,excelWrite);
    	}
    	long end3 = System.currentTimeMillis();
    	excelWrite.close(outputStream);
    	long end4 = System.currentTimeMillis();
    	log.debug("导出人员项目工时，总耗时：" + (end4 - start) + "|" + (end4 - end3)  + "|" + (end3 - end2)  + "|" + (end2 - end1) 
    			 + "|" + (end1 - start) );
	}
	
	/**
     * 处理sheet数据
     */
	private void handleSheetData(List<UserProjectInputVo> userInputVos, int startRow, ExcelWrite excelWrite) {
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
    	sheet.setColumnWidth(2, 3745);
    	sheet.setColumnWidth(3, 6420);
    	sheet.setColumnWidth(4, 3745);
    	sheet.setColumnWidth(5, 6420);
    	sheet.setColumnWidth(6, 3345);
    	sheet.setColumnWidth(8, 4020);
    	sheet.setColumnWidth(10, 3210);
    	sheet.setColumnWidth(12, 3210);
		XSSFRow row = null;
		XSSFCell cell = null;
		int i = -1;
		int j = 0;
		//数据
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		for(UserProjectInputVo vo : userInputVos){
			start = System.currentTimeMillis();
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
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
			end = System.currentTimeMillis();
			log.debug("添加人员项目工时第" + i + "行数据，耗时:" + (end-start) + "ms");
		}
	}
}
