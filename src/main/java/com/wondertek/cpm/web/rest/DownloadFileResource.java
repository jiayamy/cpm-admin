package com.wondertek.cpm.web.rest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * REST controller for managing ContractBudget.
 */
/**
 * @author sunshine
 * @Description : 
 * 
 */
@RestController
@RequestMapping("/api")
public class DownloadFileResource {

    private final Logger log = LoggerFactory.getLogger(DownloadFileResource.class);
        
    @RequestMapping("/download-file/downloadXlsxTpl")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public void downloadXlsxTpl(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value = "filePath",required=true) String filePath
    		) throws URISyntaxException, IOException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to downloadXlsxTpl FilePath : {}", filePath);
        if(filePath == null || !filePath.startsWith("importTpl/")){
        	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to downloadXlsxTpl error request");
        	return;
        }
        File f = new File(request.getServletContext().getRealPath(filePath));
        
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to downloadXlsxTpl RealPath : {}", f.getPath());
        
        if(!f.exists()){
        	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to downloadXlsxTpl file not exist");
        	return;
        }
        //写入sheet
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, f.getName()));
    	response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    	response.setCharacterEncoding("UTF-8");
    	response.setContentLengthLong(f.length());
    	
    	OutputStream os = response.getOutputStream(); 
    	BufferedOutputStream bos = new BufferedOutputStream(os);
        InputStream is = null;  
        BufferedInputStream bis = null;
		try {
			is = new FileInputStream(f.getPath());  
			bis = new BufferedInputStream(is);
			
			int length = 0;  
			byte[] temp = new byte[1 * 1024];
			while ((length = bis.read(temp)) != -1) {  
				bos.write(temp, 0, length);  
			    log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to downloadXlsxTpl length : {}", length);
			}
			bos.flush();
		} catch (Exception e) {
		} finally{
			try {
				if(bis != null){
					bis.close();
				}
			} catch (Exception e) {
			}
			try {
				if(is != null){
					is.close();
				}
			} catch (Exception e) {
			}
			try {
				if(bos != null){
					bos.close();
				}
			} catch (Exception e) {
			}
		}
    }
}
