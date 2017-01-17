package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.vo.DeptInfoVo;
import com.wondertek.cpm.domain.vo.DeptTree;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;

/**
 * REST controller for managing DeptInfo.
 */
@RestController
@RequestMapping("/api")
public class DeptInfoResource {

    private final Logger log = LoggerFactory.getLogger(DeptInfoResource.class);
        
    @Inject
    private DeptInfoService deptInfoService;

    /**
     * PUT  /dept-infos : Updates an existing deptInfo.
     *
     * @param deptInfo the deptInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deptInfo,
     * or with status 400 (Bad Request) if the deptInfo is not valid,
     * or with status 500 (Internal Server Error) if the deptInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dept-infos")
    @Timed
    public ResponseEntity<DeptInfo> updateDeptInfo(@Valid @RequestBody DeptInfo deptInfo) throws URISyntaxException {
        log.debug("REST request to update DeptInfo : {}", deptInfo);
        Boolean isNew = deptInfo.getId() == null;
        
        //必要校验
        if(StringUtil.isNullStr(deptInfo.getName()) || deptInfo.getType() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.deptInfo.save.paramNone", "")).body(null);
        }
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if(isNew){//新增
        	Long parentId = deptInfo.getParentId();
        	String idPath = null;
        	if(parentId == null){
        		idPath = "/";
        	}else{
        		DeptInfo parent = deptInfoService.findOne(parentId);
        		if(parent == null){
        			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.deptInfo.save.parentNone", "")).body(null);
        		}
        		idPath = parent.getIdPath() + parent.getId() + "/";
        	}
        	deptInfo.setIdPath(idPath);
        	deptInfo.setParentId(parentId);
        	//校验是否存在同名
        	Optional<DeptInfo> tmp = deptInfoService.findOneByParentName(parentId,deptInfo.getName());
        	if(tmp.isPresent()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.deptInfo.save.nameExist", "")).body(null);
        	}
        	
        	deptInfo.setStatus(CpmConstants.STATUS_VALID);
        	deptInfo.setCreator(updator);
        	deptInfo.setCreateTime(updateTime);
        }else{//修改
        	DeptInfo old = deptInfoService.findOne(deptInfo.getId());
        	if(old == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.deptInfo.save.paramError", "")).body(null);
        	}
        	deptInfo.setParentId(old.getParentId());
        	deptInfo.setIdPath(old.getIdPath());
        	deptInfo.setStatus(old.getStatus());
        	deptInfo.setCreator(old.getCreator());
        	deptInfo.setCreateTime(old.getCreateTime());
        	Optional<DeptInfo> tmp = deptInfoService.findOneByParentName(old.getParentId(),deptInfo.getName());
        	if(tmp.isPresent() && tmp.get().getId().longValue() != old.getId()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.deptInfo.save.nameExist", "")).body(null);
        	}
        }
        deptInfo.setUpdateTime(updateTime);
        deptInfo.setUpdator(updator);
        
        DeptInfo result = deptInfoService.save(deptInfo);
        if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("deptInfo", deptInfo.getId().toString()))
                    .body(result);
        }else{
	        return ResponseEntity.ok()
	            .headers(HeaderUtil.createEntityUpdateAlert("deptInfo", deptInfo.getId().toString()))
	            .body(result);
        }
    }
    /**
     * GET  /dept-infos/:id : get the "id" deptInfo.
     *
     * @param id the id of the deptInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deptInfo, or with status 404 (Not Found)
     */
    @GetMapping("/dept-infos/{id}")
    @Timed
    public ResponseEntity<DeptInfoVo> getDeptInfo(@PathVariable Long id) {
        log.debug("REST request to get DeptInfo : {}", id);
        DeptInfoVo deptInfo = deptInfoService.getDeptInfo(id);
        return Optional.ofNullable(deptInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    /**
     * DELETE  /dept-infos/:id : delete the "id" deptInfo.
     *
     * @param id the id of the deptInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dept-infos/{id}")
    @Timed
    public ResponseEntity<Void> deleteDeptInfo(@PathVariable Long id) {
        log.debug("REST request to delete DeptInfo : {}", id);
        //检查部门及下面的部门对应用户是否全部删除
        DeptInfo deptInfo = deptInfoService.findOne(id);
        if(deptInfo != null){
        	List<String> deptNames = deptInfoService.getExistUserDeptNameByDeptParent(deptInfo.getId(),deptInfo.getIdPath());
        	if(deptNames != null && !deptNames.isEmpty()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.deptInfo.delete.userExist", deptNames.toString())).body(null);
        	}
        	deptInfoService.delete(id);
        }
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("deptInfo", id.toString())).build();
    }

    @GetMapping("/dept-infos/getDeptAndUserTree")
    @Timed
    public ResponseEntity<List<DeptTree>> getDeptAndUserTree(
    			@RequestParam(value = "selectType",required=false) Integer selectType,
    			@RequestParam(value = "showChild",required=false) Boolean showChild,
    			@RequestParam(value = "showUser",required=false) Boolean showUser
    		) throws URISyntaxException {
        log.debug("REST request to get a page of getDeptAndUserTree");
        if(selectType == null){
        	selectType = DeptTree.SELECTTYPE_NONE;
        }
        if(showChild == null){
        	showChild = Boolean.TRUE;
        }
        if(showUser == null){
        	showUser = Boolean.TRUE;
        }
        List<DeptTree> list = deptInfoService.getDeptAndUserTree(selectType,showChild,showUser);
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
    @GetMapping("/dept-infos/getDeptTree")
    @Timed
    public ResponseEntity<List<DeptTree>> getDeptTree() throws URISyntaxException {
        log.debug("REST request to get a page of getDeptAndUserTree");
        List<DeptTree> list = deptInfoService.getDeptAndUserTree(DeptTree.SELECTTYPE_NONE,Boolean.TRUE,Boolean.FALSE);
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
}
