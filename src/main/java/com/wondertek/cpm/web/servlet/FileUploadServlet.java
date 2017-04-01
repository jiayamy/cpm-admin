package com.wondertek.cpm.web.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.FilePathHelper;
import com.wondertek.cpm.config.FileUtil;
import com.wondertek.cpm.config.Md5Util;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.security.SecurityUtils;

@WebServlet(name = "fileUploadServlet", urlPatterns = { "/cpmservlet/uploadFile"})
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = -8244073279641189889L;
    private final Logger log = Logger.getLogger(FileUploadServlet.class.getName());
    class SizeEntry {
        public long size;
        public LocalDateTime time;
    }
    static Map<String, SizeEntry> sizeMap = new ConcurrentHashMap<>();
    int counter;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
        	//必须要登录
        	String login = SecurityUtils.getCurrentUserLogin();
        	if(StringUtil.isNullStr(login)){
        		res.sendError(404, "Not Found");
        		log.info("upload request,RemoteAddr:"+req.getRemoteAddr()+",Method:"+req.getMethod()+",user not login");
                return;
        	}
        	//清除历史记录
            clearOldValuesInSizeMap();
            //获取参数
            String uploadTime = req.getHeader("X-File-Upload-Time");
            String mark = req.getHeader("X-File-Upload-Mark");
            String fileName = req.getHeader("X-File-Upload-Name");
            String type = req.getHeader("X-File-Upload-Type");
            Boolean isRemove = Boolean.FALSE;
            if(StringUtil.isNullStr(uploadTime)){
            	isRemove = true;
            	uploadTime = req.getParameter("uploadTime");
            }
            if(StringUtil.isNullStr(mark)){
            	mark = req.getParameter("mark");
            }
            if(StringUtil.isNullStr(fileName)){
            	fileName = req.getParameter("name");
            }else{
            	fileName = URLDecoder.decode(fileName,"UTF-8");
            }
            if(StringUtil.isNullStr(type)){
            	type = req.getParameter("type");
            }
            String sizeMapKey = getSizeMapKey(login,uploadTime,mark,fileName,type);
            log.info("upload request,RemoteAddr:"+req.getRemoteAddr()+",Method:"+req.getMethod()
            	+",login:"+login
            	+",fileName:"+fileName
            	+",uploadTime:"+uploadTime
            	+",mark:"+mark
            	+",type:"+type
            	+",sizeMapKey:"+sizeMapKey);
            //校验参数
            if(StringUtil.isNullStr(fileName)
            		|| StringUtil.isNullStr(uploadTime) || StringUtil.nullToCloneLong(uploadTime) == null || uploadTime.length() != 17
            		|| StringUtil.isNullStr(mark) || StringUtil.nullToCloneLong(mark) == null 
            		|| StringUtil.nullToInteger(mark) < 0 || StringUtil.nullToInteger(mark) > 100
            		|| StringUtil.isNullStr(type) || StringUtil.nullToCloneLong(type) == null
            		|| StringUtil.nullToInteger(type) < 0 || StringUtil.nullToInteger(type) > 100
            		){
            	log.info("upload request,RemoteAddr:"+req.getRemoteAddr()+",Method:"+req.getMethod()+",param error");
            	res.sendError(404, "Not Found");
            	return;
            }
            //校验uploadTime
            Date uploadTimeD = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, uploadTime.substring(0,8));
            Date uploadTimeS = DateUtil.parseDate(DateUtil.DATE_TIME_NO_SPACE_PATTERN, uploadTime.substring(0,14));
            if(uploadTimeD == null || uploadTimeS == null || !uploadTime.substring(0,8).equals(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, uploadTimeD))){
            	res.sendError(404, "Not Found");
            	log.info("upload request,RemoteAddr:"+req.getRemoteAddr()+",Method:"+req.getMethod()+",uploadTime error");
            	return;
            }
            //校验请求是不是一个小时之内的
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, -1);
            if(c.getTime().after(uploadTimeS)){
            	res.sendError(404, "Not Found");
            	log.info("upload request,RemoteAddr:"+req.getRemoteAddr()+",Method:"+req.getMethod()+",uploadTime invalid");
            	return;
            }
            //GET请求是重传、断点续传时获取现有文件大小的
            if (req.getMethod().equalsIgnoreCase("GET")) {
                if (req.getParameter("restart") != null) {
                    sizeMap.remove(sizeMapKey);
                }
                SizeEntry entry = sizeMap.get(sizeMapKey);
                res.getWriter().write("{\"size\":" + (entry == null ? 0 : entry.size) + "}");
                res.setContentType("application/json");
                return;
            }
            //只支持POST请求上传文件
            if (!"POST".equalsIgnoreCase(req.getMethod())) {
                res.sendError(404, "Not Found");
                return;
            }
            //flash是一次性上传的，所以在保存文件之前，不能有文件
            if(isRemove){
            	sizeMap.remove(sizeMapKey);
            }
            req.setCharacterEncoding("utf-8");
            String filePath = null;//文件保存路径
            long totalSize = 0;//文件总大小
            long currentSize = 0;//当前文件上传大小
            StringBuilder sb = new StringBuilder("{\"result\": [");
            Boolean isSupportResume = Boolean.FALSE;//只有在参数中传了_totalSize的才默认支持断点续传
            //post过来的文件上传请求
            if (req.getHeader("Content-Type") != null
                    && req.getHeader("Content-Type").startsWith("multipart/form-data")) {
                ServletFileUpload upload = new ServletFileUpload();
                FileItemIterator iterator = upload.getItemIterator(req);
                String val = null;
                while (iterator.hasNext()) {
                    FileItemStream item = iterator.next();
                    sb.append("{");
                    sb.append("\"fieldName\":\"").append(item.getFieldName()).append("\",");
                    if (item.getName() != null) {
                        sb.append("\"name\":\"").append(item.getName()).append("\",");
                    }
                    if (item.getName() != null) {//有name是上传的文件
                    	sizeMapKey = getSizeMapKey(login,uploadTime,mark,item.getName(),type);
                    	filePath = getRelativeFilePath(uploadTime,mark,sizeMapKey,fileName,type);
                    	//保存文件
                    	currentSize = saveFile(sizeMapKey, item.openStream(),FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
                    	
                        sb.append("\"size\":\"").append(currentSize).append("\"");
                    } else {//其他参数
                    	val = readParamVal(item.openStream()).replace("\"", "'");
                        sb.append("\"value\":\"").append(val).append("\"");
                        if(item.getFieldName() != null && item.getFieldName().equalsIgnoreCase("_totalSize")){//不是flash上传的，没有带这个的，都不算断点续传的
                        	totalSize = StringUtil.nullToLong(val);
                        	isSupportResume = Boolean.TRUE;
                        }
                    }
                    sb.append("}");
                    if (iterator.hasNext()) {
                        sb.append(",");
                    }
                }
            } else {
            	filePath = getRelativeFilePath(uploadTime,mark,sizeMapKey,fileName,type);
            	//保存文件
            	currentSize = saveFile(sizeMapKey, req.getInputStream(),FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
            	
                sb.append("{\"size\":\"").append(currentSize).append("\"}");
            }
            sb.append("]");
            //不支持断点续传，或者续传完成的，都删除当前key
            if(!isSupportResume || currentSize >= totalSize){
            	sizeMap.remove(sizeMapKey);
            	sb.append(", \"finished\":").append(true);
            }else{
            	sb.append(", \"finished\":").append(false);
            }
            sb.append(", \"filePath\": \"").append(filePath).append("\"");
            sb.append(", \"requestHeaders\": {");
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                sb.append("\"").append(header).append("\":\"").append(req.getHeader(header)).append("\"");
                if (headerNames.hasMoreElements()) {
                    sb.append(",");
                }
            }
            sb.append("}}");
            res.setCharacterEncoding("utf-8");
            res.getWriter().write(sb.toString());
            res.setContentType("application/json");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
    /**
     * 去除老的文件上传信息
     */
    private void clearOldValuesInSizeMap() {
        if (counter++ == 100) {
            for (Map.Entry<String, SizeEntry> entry : sizeMap.entrySet()) {
                if (entry.getValue().time.isBefore(LocalDateTime.now().minusHours(1))) {
                    sizeMap.remove(entry.getKey());
                }
            }
            counter = 0;
        }
    }
    /**
     * 上传文件
     * @param fileName 
     */
	protected long saveFile(String key, InputStream stream, String filePath) {
		long length = sizeMap.get(key) == null ? 0 : sizeMap.get(key).size;
        FileOutputStream os = null;
        try {
            byte[] buffer = new byte[200000];
            int size;
            File f = new File(filePath);
            if(length == 0){
            	FileUtil.createNewFile(f);
            	os = new FileOutputStream(f,false);
            }else{
            	os = new FileOutputStream(f,true);
            }
            while ((size = stream.read(buffer)) != -1) {
                length += size;
                os.write(buffer, 0, size);//写入文件
                
                SizeEntry entry = new SizeEntry();
                entry.size = length;
                entry.time = LocalDateTime.now();
                sizeMap.put(key, entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
        	try {
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return length;

    }
    /**
     * 获取相对文件路径
     * @param type 
     */
    private String getRelativeFilePath(String uploadTime, String mark, String key, String fileName, String type) {
    	String postfix = FilePathHelper.getFilePostfix(fileName, "");
    	if(!StringUtil.isNullStr(postfix)){
    		return FilePathHelper.joinPath(
    				uploadTime.substring(0,6),//年月
    				uploadTime.substring(0,8),//年月日
    				type+"_"+mark+"_"+key+"."+postfix);
    	}else{
    		return FilePathHelper.joinPath(
    				uploadTime.substring(0,6),//年月
    				uploadTime.substring(0,8),//年月日
    				type+"_"+mark+"_"+key);
    	}
	}
	/**
     * map的key
     * @return
     */
    protected String getSizeMapKey(String login, String uploadTime, String mark, String fileName, String type){
    	return Md5Util.md5Code(login+"_"+type+"_"+uploadTime+"_"+mark+"_"+fileName);
    }
    /**
     * 读取其他信息
     */
    protected String readParamVal(InputStream stream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //ignore
            }
        }
        return sb.toString();
    }
}
