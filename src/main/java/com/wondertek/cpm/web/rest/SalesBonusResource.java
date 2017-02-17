package com.wondertek.cpm.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private final DecimalFormat doubleFormat = new DecimalFormat("#0.00");
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
    	//数据
    	List<String[]> rows = new ArrayList<String[]>();
    	if(page != null){
    		handleSheetData(rows,page);
    	}
    	String fileName = "salesBonus_" + salesBonus.getOriginYear() + "_" + salesBonus.getStatWeek() + ".xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + fileName);
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
		
    	ExcelWrite excelWrite = new ExcelWrite();
    	excelWrite.createSheet("销售项目", 1, heads, rows);
    	excelWrite.close(outputStream);
    }
    /**
     * 处理sheet数据
     */
	private void handleSheetData(List<String[]> rows, List<SalesBonusVo> page) {
		for(SalesBonusVo vo : page){
			String[] data = new String[]{
					vo.getSalesMan(),
					handleDouble2String(vo.getAnnualIndex(),null),
					handleDouble2String(vo.getFinishTotal(),null),
					vo.getContractNum(),
					handleDouble2String(vo.getContractAmount(),null),
					handleDouble2String(vo.getTaxRate(),"%"),
					handleDouble2String(vo.getReceiveTotal(),null),
					handleDouble2String(vo.getTaxes(),null),
					handleDouble2String(vo.getShareCost(),null),
					handleDouble2String(vo.getThirdPartyPurchase(),null),
					handleDouble2String(vo.getBonusBasis(),null),
					handleDouble2String(vo.getBonusRate(),"%"),
					handleDouble2String(vo.getCurrentBonus(),null),
					handleDouble2String(vo.getTotalBonus(),null),
					handleDouble2String(vo.getFinishRate(),"%"),
					handleDouble2String(vo.getPayBonus(),null)
			};
			rows.add(data);
		}
	}
	/**
	 * 保留2位小数
	 * @param val
	 * @param postfix
	 * @return
	 */
	private String handleDouble2String(Double val, String postfix) {
		if(val == null){
			return "";
		}
		if(postfix != null){
			return doubleFormat.format(val) + postfix;
		}else{
			return doubleFormat.format(val);
		}
	}
}
