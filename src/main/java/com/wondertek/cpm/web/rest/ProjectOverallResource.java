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
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ProjectOverallService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

/**
 * REST controller for managing ProductPrice.
 */
@RestController
@RequestMapping("/api")
public class ProjectOverallResource {

    private final Logger log = LoggerFactory.getLogger(ProjectOverallResource.class);
    private final DecimalFormat doubleFormat = new DecimalFormat("#0.00");   
    @Inject
    private ProjectOverallService projectOverallService;
    
	/**
	 * 列表页
	 * @author sunshine
	 * @Description :
	 */
    @GetMapping("/project-overall-controller")
    @Timed
    public ResponseEntity<List<ProjectOverallVo>> getAllProjectOverallByParams(
    		@RequestParam(value = "statWeek",required=false) Long statWeek,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@RequestParam(value = "userId",required=false) Long userId,
    		@ApiParam Pageable pageable)
		throws URISyntaxException {
		log.debug("REST request to get a page of ProjectOverallVo");
		Date now = new Date();
		ProjectOverall projectOverall = new ProjectOverall();
		projectOverall.setStatWeek(statWeek);
		projectOverall.setContractId(contractId);
		projectOverall.setContractResponse(userId);
		if(projectOverall.getStatWeek() == null){//默认当前天对的的周日
			projectOverall.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
         }else {//更改为对应日期的周日
        	 projectOverall.setStatWeek(StringUtil.nullToLong(
         			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
         					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+projectOverall.getStatWeek())))));
		}
		Page<ProjectOverallVo> page = projectOverallService.searchPage(projectOverall,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(statWeek.toString(), page,"/api/project-projectOverall");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);    	
    }

    @GetMapping("/project-overall-controller/queryDetail")
    @Timed
    public ResponseEntity<List<ProjectOverallVo>> getProjectOverallDetail(
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@ApiParam Pageable pageable) 
    	throws URISyntaxException {
        log.debug("REST request to get ProductPrice : {}", contractId);
        Page<ProjectOverallVo> page = projectOverallService.searchPageDetail(contractId,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(contractId.toString(), page,"/api/project-projectOverall");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK); 
    }
    
    @GetMapping("/project-overall-controller/{id}")
    @Timed
    public ResponseEntity<ProjectOverall> getProjectOverall(@PathVariable Long id) {
        log.debug("REST request to get ProductPrice : {}", id);
        ProjectOverall projectOverall = projectOverallService.findOne(id);
        return Optional.ofNullable(projectOverall)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @RequestMapping("/project-overall/exportXls")
    @Timed
    public void exportXls(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value = "statWeek",required=false) Long statWeek,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@RequestParam(value = "userId",required=false) Long userId,
    		@ApiParam Pageable pageable)
		throws URISyntaxException, IOException {
    	log.debug("REST request to get a page of exportXls");
    	Date now = new Date();
    	ProjectOverall projectOverall = new ProjectOverall();
		projectOverall.setStatWeek(statWeek);
		projectOverall.setContractId(contractId);
		projectOverall.setContractResponse(userId);
    	String currentDay = StringUtil.null2Str(
    			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, now));
    	 
    	if(projectOverall.getStatWeek() == null){//默认当前天对的的周日
			projectOverall.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
         }else {//更改为对应日期的周日
        	 projectOverall.setStatWeek(StringUtil.nullToLong(
         			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
         					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+projectOverall.getStatWeek())))));
		}
    	 Page<ProjectOverallVo> page = projectOverallService.searchPage(projectOverall,pageable);
         List<ProjectOverallVo> list = new ArrayList<ProjectOverallVo>();
         for (ProjectOverallVo projectOverallVo : page.getContent()) {
 			list.add(projectOverallVo);
 		}
//         List<ProjectOverallVo> list = projectOverallService.getProjectOverallList(fromDate,toDate,contractId,userId);

         //拼接sheet数据
     	//标题
     	String[] heads = new String[]{
     			"合同负责人",
     			"合同编号",
     			"合同金额",
     			"税率",
     			"可确认收入",
     			"合同完成节点",
     			"收入确认",
     			"收款金额",
     			"应收账款",
     			"公摊成本",
     			"第三方采购",
     			"实施成本",
     			"中央研究院",
     			"奖金",
     			"毛利",
     			"毛利率"
     	};
     	//设置文件名
     	String fileName = "projectOverall_" + currentDay + ".xlsx";
     	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + fileName);
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
    private void handleSheetData(List<ProjectOverallVo> list, int startRow,
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
		for (ProjectOverallVo vo : list) {
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if (vo.getSalesman() == null && vo.getConsultants() == null) {
				 cell.setCellValue("");
			}else {
				cell.setCellValue(!StringUtil.isNullStr(vo.getSalesman()) ? vo.getSalesman() : vo.getConsultants());
			}
			j++;
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
			if (vo.getTaxRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getTaxRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getIdentifiableIncome() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getIdentifiableIncome());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getContractFinishRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getContractFinishRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getAcceptanceIncome() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getAcceptanceIncome());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getReceiveTotal() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getReceiveTotal());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getReceivableAccount() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getReceivableAccount());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getShareCost() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getShareCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getThirdPartyPurchase() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getThirdPartyPurchase());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getImplementationCost() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getImplementationCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getAcademicCost() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getAcademicCost());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getBonus() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getBonus());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getGrossProfit() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getGrossProfit());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if (vo.getGrossProfitRate() == null) {
				cell.setCellValue("");
			}else {
				cell.setCellValue(vo.getGrossProfitRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
		}
	}

}
