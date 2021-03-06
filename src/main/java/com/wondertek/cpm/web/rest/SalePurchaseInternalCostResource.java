package com.wondertek.cpm.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import com.wondertek.cpm.domain.ProjectSupportCost;
import com.wondertek.cpm.domain.vo.ProjectSupportCostVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.SalePurchaseInternalCostService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing SalePurchaseInternalCost.
 */
@RestController
@RequestMapping("/api")
public class SalePurchaseInternalCostResource {

	private final Logger log = LoggerFactory.getLogger(SalePurchaseInternalCostResource.class);
	
	@Inject
	private SalePurchaseInternalCostService salePurchaseInternalCostService;
	
	/**
     * GET  /sale-purchase-internalCost : get all the ProjectSupportCostVo.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ProjectSupportCostVo in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
	@RequestMapping("/sale-purchase-internalCost")
	@Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_INTERNAL_COST)
	public ResponseEntity<List<ProjectSupportCostVo>> getAllSalePurchaseInternalCost(
				@RequestParam(name="contractId",required=false) Long contractId,
				@RequestParam(name="userId",required=false) Long userId,
				@RequestParam(name="statWeek",required=false) Long statWeek,
				@RequestParam(name="deptType",required=false) Long deptType
				) throws URISyntaxException{
		log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to get a page of getAllSalePurchaseInternalCost : contractId:{},userId:{},statWeek:{},deptType:{}",contractId,userId,statWeek,deptType);
		if(contractId == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if(statWeek != null){		//转换截止日期至周末
			statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN,
					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, statWeek.toString().trim()))));
		}else{
			Date now = new Date();	//截止日期默认值
			statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.getSundayOfDay(now)));
		}
		ProjectSupportCost projectSupportCost = new ProjectSupportCost();
		projectSupportCost.setContractId(contractId);
		projectSupportCost.setUserId(userId);
		projectSupportCost.setStatWeek(statWeek);
		projectSupportCost.setDeptType(deptType);
		List<ProjectSupportCostVo> page = salePurchaseInternalCostService.getAllSalePurchaseInternalPage(projectSupportCost);
		for (ProjectSupportCostVo projectSupportCostVo : page) {
			projectSupportCostVo.setSettlementCost(StringUtil.getScaleDouble(projectSupportCostVo.getSettlementCost(), 10000d, 2));
			projectSupportCostVo.setInternalBudgetCost(StringUtil.getScaleDouble(projectSupportCostVo.getInternalBudgetCost(), 10000d, 2));
			projectSupportCostVo.setSal(StringUtil.getScaleDouble(projectSupportCostVo.getSal(), 10000d, 2));
			projectSupportCostVo.setSocialSecurityFund(StringUtil.getScaleDouble(projectSupportCostVo.getSocialSecurityFund(), 10000d, 2));
			projectSupportCostVo.setOtherExpense(StringUtil.getScaleDouble(projectSupportCostVo.getOtherExpense(), 10000d, 2));
			projectSupportCostVo.setUserMonthCost(StringUtil.getScaleDouble(projectSupportCostVo.getUserMonthCost(), 10000d, 2));
			projectSupportCostVo.setUserHourCost(StringUtil.getScaleDouble(projectSupportCostVo.getUserHourCost(), 10000d, 2));
			projectSupportCostVo.setProductCost(StringUtil.getScaleDouble(projectSupportCostVo.getProductCost(), 10000d, 2));
			projectSupportCostVo.setGrossProfit(StringUtil.getScaleDouble(projectSupportCostVo.getGrossProfit(), 10000d, 2));
		}
		return new ResponseEntity<>(page,new HttpHeaders(),HttpStatus.OK);
	}
	
	@RequestMapping("/sale-purchase-internalCost/queryInternalCostDetail")
	@Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_INTERNAL_COST)
	public ResponseEntity<List<ProjectSupportCostVo>> queryInternalCostDetail(
			@RequestParam(name="id",required=false) Long id,
			@ApiParam Pageable pageable
			) throws URISyntaxException{
		log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to get a page of queryInternalCostDetail : {}",id);
		Page<ProjectSupportCostVo> page = salePurchaseInternalCostService.getAllSalePurchaseInternalDetailPage(id,pageable);
		for (ProjectSupportCostVo projectSupportCostVo : page.getContent()) {
			projectSupportCostVo.setSettlementCost(StringUtil.getScaleDouble(projectSupportCostVo.getSettlementCost(), 10000d, 2));
			projectSupportCostVo.setInternalBudgetCost(StringUtil.getScaleDouble(projectSupportCostVo.getInternalBudgetCost(), 10000d, 2));
			projectSupportCostVo.setSal(StringUtil.getScaleDouble(projectSupportCostVo.getSal(), 10000d, 2));
			projectSupportCostVo.setSocialSecurityFund(StringUtil.getScaleDouble(projectSupportCostVo.getSocialSecurityFund(), 10000d, 2));
			projectSupportCostVo.setOtherExpense(StringUtil.getScaleDouble(projectSupportCostVo.getOtherExpense(), 10000d, 2));
			projectSupportCostVo.setUserMonthCost(StringUtil.getScaleDouble(projectSupportCostVo.getUserMonthCost(), 10000d, 2));
			projectSupportCostVo.setUserHourCost(StringUtil.getScaleDouble(projectSupportCostVo.getUserHourCost(), 10000d, 2));
			projectSupportCostVo.setProductCost(StringUtil.getScaleDouble(projectSupportCostVo.getProductCost(), 10000d, 2));
			projectSupportCostVo.setGrossProfit(StringUtil.getScaleDouble(projectSupportCostVo.getGrossProfit(), 10000d, 2));
		}
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sale-purchase-internalCost/queryInternalCostDetail");
		return Optional.ofNullable(page.getContent()).map(result -> new ResponseEntity<>(result,headers,HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@RequestMapping("/sale-purchase-internalCost/exportXls")
	@Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_INTERNAL_COST)
	public void exportXls(
			HttpServletRequest request,HttpServletResponse response,
			@RequestParam(name="contractId",required=false) Long contractId,
			@RequestParam(name="userId",required=false) Long userId,
			@RequestParam(name="statWeek",required=false) Long statWeek,
			@RequestParam(name="deptType",required=false) Long deptType) throws IOException{
		log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to exportXls : contractId:{},userId:{},statWeek:{},deptType:{}",contractId,userId,statWeek,deptType);
		if(statWeek != null){		//转换截止日期至周末
			statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN,
					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, statWeek.toString().trim()))));
		}else{
			Date now = new Date();	//截止日期默认值
			statWeek = StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.getSundayOfDay(now)));
		}
		List<ProjectSupportCostVo> pageList;
		if (contractId != null) {
			ProjectSupportCost projectSupportCost = new ProjectSupportCost();
			projectSupportCost.setContractId(contractId);
			projectSupportCost.setUserId(userId);
			projectSupportCost.setStatWeek(statWeek);
			projectSupportCost.setDeptType(deptType);
			
			pageList = salePurchaseInternalCostService.getAllSalePurchaseInternalPage(projectSupportCost);
		}else{
			pageList = new ArrayList<ProjectSupportCostVo>();
		}
		//拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"合同编号",
    			"部门类型",
    			"员工编号",
    			"员工姓名",
    			"级别",
    			"结算成本",
    			"项目工时",
    			"内部采购成本",
    			"工资",
    			"社保公积金",
    			"其它费用",
    			"单人月成本小计",
    			"工时成本",
    			"生产成本合计",
    			"生产毛利"
    	};
    	String fileName = "销售内部采购成本_" + statWeek +".xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
    	
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("销售内部采购成本", 1, heads);
    	//写入数据
    	if(pageList != null){
    		handleSheetData(pageList,2,excelWrite);
    	}
    	excelWrite.close(outputStream);
	}
	
	/**
     * 处理sheet数据
     */
	private void handleSheetData(List<ProjectSupportCostVo> pageList,int startRow,ExcelWrite excelWrite) {
		//除表头外的其他数据单元格格式
    	Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
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
    			Cell.CELL_TYPE_NUMERIC
    	};
    	XSSFSheet sheet = excelWrite.getCurrentSheet();
		XSSFRow row = null;
		XSSFCell cell = null;
		int i = -1;
		int j = 0;
		//数据
		for(ProjectSupportCostVo vo : pageList){
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractSerialNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getDeptName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getDeptName());
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
			if(vo.getUserName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getUserName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getGrade() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getGrade());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getSettlementCost() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getSettlementCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getProjectHourCost() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getProjectHourCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getInternalBudgetCost() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getInternalBudgetCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getSal() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getSal());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getSocialSecurityFund() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getSocialSecurityFund());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getOtherExpense() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getOtherExpense());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getUserMonthCost() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getUserMonthCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getUserHourCost() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getUserHourCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getProductCost() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getProductCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getGrossProfit() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getGrossProfit());
			}
			j++;
		}
	}
}
