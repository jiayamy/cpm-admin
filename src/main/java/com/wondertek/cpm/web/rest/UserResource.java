package com.wondertek.cpm.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.ExcelValue;
import com.wondertek.cpm.config.Constants;
import com.wondertek.cpm.config.FilePathHelper;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.Authority;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.repository.AuthorityRepository;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.repository.ExternalQuotationRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.UserSearchRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.service.MailService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.service.WorkAreaService;
import com.wondertek.cpm.service.util.RandomUtil;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;
import com.wondertek.cpm.web.rest.vm.ManagedUserVM;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private DeptInfoRepository deptInfoRepository;
    
    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    @Inject
    private UserSearchRepository userSearchRepository;
    @Inject
    private ExternalQuotationRepository externalQuotationRepository;
    @Inject
    private DeptInfoService deptInfoService;
    @Inject
    private WorkAreaService workAreaService;
    @Inject
    private AuthorityRepository authorityRepository;
    
    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     * </p>
     *
     * @param managedUserVM the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<?> createUser(@RequestBody ManagedUserVM managedUserVM) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to save User : {}", managedUserVM);

        //Lowercase the user login before comparing with database
        if (userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use"))
                .body(null);
        } else if (userRepository.findOneByEmail(managedUserVM.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "Email already in use"))
                .body(null);
        } else if (userRepository.findOneBySerialNum(managedUserVM.getSerialNum()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("userManagement", "serialNumexists", "Serial num already in use"))
                    .body(null);
        } else {
        	if(managedUserVM.getIsManager() == null){
        		managedUserVM.setIsManager(Boolean.FALSE);
        	}
            User newUser = userService.createUser(managedUserVM);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert( "userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    /**
     * PUT  /users : Updates an existing User.
     *
     * @param managedUserVM the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     */
    @PutMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<ManagedUserVM> updateUser(@RequestBody ManagedUserVM managedUserVM) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update User : {}", managedUserVM);
        Optional<User> existingUser = userRepository.findOneByEmail(managedUserVM.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "E-mail already in use")).body(null);
        }
        existingUser = userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use")).body(null);
        }
        existingUser = userRepository.findOneBySerialNum(managedUserVM.getSerialNum().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "serialNumexists", "Serial num already in use")).body(null);
        }
        if(managedUserVM.getIsManager() == null){
    		managedUserVM.setIsManager(Boolean.FALSE);
    	}
        userService.updateUser(managedUserVM.getId(), managedUserVM.getLogin(), managedUserVM.getFirstName(),
            managedUserVM.getLastName(), managedUserVM.getEmail(), managedUserVM.isActivated(),
            managedUserVM.getLangKey(), managedUserVM.getAuthorities(), managedUserVM);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert("userManagement.updated", managedUserVM.getLogin()))
            .body(new ManagedUserVM(userService.getUserWithAuthorities(managedUserVM.getId())));
    }

    /**
     * GET  /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     * @throws URISyntaxException if the pagination headers couldn't be generated
     */
    @GetMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<ManagedUserVM>> getAllUsers(
    		@RequestParam(value = "loginName",required=false) String login, 
    		@RequestParam(value = "serialNum",required=false) String serialNum, 
    		@RequestParam(value = "lastName",required=false) String lastName, 
    		@RequestParam(value = "deptId",required=false) Long deptId, 
    		@RequestParam(value = "workArea",required=false) String workArea, 
    		@RequestParam(value = "grade",required=false) Integer grade,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get All Users login : {}, serialNum : {}, lastName : {}, "
    			+ "deptId : {}, workArea : {}, grade : {}", login, serialNum, lastName, deptId, workArea, grade);
    	User user = new User();
    	user.setLogin(login);
    	user.setSerialNum(serialNum);
    	user.setLastName(lastName);
    	user.setDeptId(deptId);
    	user.setWorkArea(workArea);
    	user.setGrade(grade);
    	
//        Page<User> page = userRepository.findAllWithAuthorities(pageable);
        Page<User> page = userService.getUserPage(user,pageable);
        List<User> objs = page.getContent();
        if(objs != null){
        	DeptInfo tmp = null;
        	for(User o : objs){
        		if(o.getDeptId() != null){
        			tmp = deptInfoRepository.findOne(o.getDeptId());
        			if(tmp != null){
        				o.setDept(tmp.getName());
        			}
        		}
        	}
        }
        List<ManagedUserVM> managedUserVMs = page.getContent().stream()
            .map(ManagedUserVM::new)
            .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(managedUserVMs, headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<ManagedUserVM> getUser(@PathVariable String login) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
                .map(ManagedUserVM::new)
                .map(managedUserVM -> new ResponseEntity<>(managedUserVM, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete User: {}", login);
        userService.deleteUser(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert( "userManagement.deleted", login)).build();
    }

    /**
     * SEARCH  /_search/users/:query : search for the User corresponding
     * to the query.
     *
     * @param query the query to search
     * @return the result of the search
     */
    @GetMapping("/_search/users/{query}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public List<User> search(@PathVariable String query) {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to search User: {}", query);
    	return StreamSupport
            .stream(userSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
    
    @GetMapping("/_authorities/users/queryAll")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<Authority>> queryAllAuthorities() {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to query All Authorities");
    	List<Authority> page = userService.queryAllAuthorities();
        return new ResponseEntity<>(page, null, HttpStatus.OK);
    }
    
    @GetMapping("/_usersGrade/users/queryAll")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<Integer>> queryAllGrade() {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to query All Grade");
    	List<Integer> all = externalQuotationRepository.findGradeOrderByGrade();
        return new ResponseEntity<>(all, null, HttpStatus.OK);
    }
    
    @GetMapping("/users/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value = "filePath",required=true) String filePath)
            throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for file : {}",filePath);
        List<User> users = null;
        CpmResponse cpmResponse = new CpmResponse();
        
		try {
			//从第一行读取，最多读取10个sheet，最多读取15列
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
			if(!file.exists() || !file.isFile()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("userManagement.import.requiredError"));
			}
			int startNum = 1;
			List<ExcelValue> lists = ExcelUtil.readExcel(file,startNum,10,15);
			if(lists == null || lists.isEmpty()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("userManagement.import.requiredError"));
			}
			//初始信息
			//工作地点
			List<String> areas = workAreaService.queryAll();
			//初始化部门
			Map<String,Long> companys = deptInfoService.getUsedCompanyInfos();
			//获取公司下的部门
			Map<String,List<DeptInfo>> deptInfos = deptInfoService.getUsedDetpInfos(companys.values());
			//现有登录账号对应工号
			Map<String,String> loginSerialNums = userService.getSerialNumForLogin();
			//角色
			List<Authority> authorityLists = authorityRepository.findAll();
			Map<String,Authority> authoritys = new HashMap<String,Authority>();
			if(authorityLists != null){
				for(Authority authority : authorityLists){
					authoritys.put(authority.getDetail(), authority);
				}
			}
			//其他信息
			users = new ArrayList<User>();
			String updator = SecurityUtils.getCurrentUserLogin();
			ZonedDateTime updateTime = ZonedDateTime.now();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			String serialNum = null;
			Long companyId = null;
			List<DeptInfo> primaryDeptInfos = null;	//一级部门
			List<DeptInfo> secondaryDeptInfos = null;	//二级部门
			Map<String,Integer> loginExistMap = new HashMap<String,Integer>();
			Map<String,Integer> serialNumExistMap = new HashMap<String,Integer>();
			for (ExcelValue excelValue : lists) {
				if (excelValue.getVals() == null || excelValue.getVals().isEmpty()) {//每个sheet也可能没有数据，空sheet
					continue;
				}
				rowNum = 1;//都是从第一行读取的
				for(List<Object> ls : excelValue.getVals()){
					rowNum ++;
					if(ls == null){//每个sheet里面也可能有空行
						continue;
					}
					try {
						User user = new User();
				        
				        user.setLangKey("zh-cn");
				        user.setResetKey(RandomUtil.generateResetKey());
				        user.setResetDate(updateTime);
				        user.setActivated(true);
				        user.setGrade(1);
				        user.setCreatedBy(updator);
				        user.setCreatedDate(updateTime);
				        user.setLastModifiedBy(updator);
				        user.setLastModifiedDate(updateTime);
				        
						//校验第一列 员工工号 要唯一，不可重复，若系统存在该工号，就是更新该员工的其他信息
						columnNum = 0;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("userManagement.import.dataError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						user.setSerialNum(StringUtil.null2Str(val));
						//记录中是否存在同一工号
						if(serialNumExistMap.containsKey(user.getSerialNum())){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.recordExistError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						serialNumExistMap.put(user.getSerialNum(), 1);
						
						//校验第二列 登录账号 可为空，默认就是工号
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							user.setLogin(user.getSerialNum());
						}else{
							user.setLogin(StringUtil.null2Str(val));
						}
						//记录中是否存在同一登录账号
						if(loginExistMap.containsKey(user.getSerialNum())){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.recordExistError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						//查看下登录账号是否被现有数据库中的其他工号占用
						serialNum = loginSerialNums.get(user.getLogin());
						if(serialNum != null && !serialNum.equals(user.getSerialNum())){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.loginNotMatchError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						loginExistMap.put(user.getLogin(), 1);
						
						//校验第三列 员工姓名 不可为空，可重复
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						user.setLastName(StringUtil.null2Str(val));
						
						//校验第四列 公司名 系统设置-部门信息中的顶级部门名称。方便获取下面的一级部门和二级部门
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						companyId = companys.get(StringUtil.null2Str(val));
						if(companyId == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第五列 一级部门 不可为空，且在一个公司下名称必须唯一
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						primaryDeptInfos = deptInfos.get(companyId + "_" + StringUtil.null2Str(val));
						if(primaryDeptInfos == null || primaryDeptInfos.isEmpty()){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(primaryDeptInfos.size() > 1){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第六列 二级部门 必须在一级部门下，可为空
						columnNum++;
						val = ls.get(columnNum);
						if(!StringUtil.isNullStr(val)){//校验二级部门是否存在
							secondaryDeptInfos = deptInfos.get(companyId + "_" + StringUtil.null2Str(val));
							if(secondaryDeptInfos == null || secondaryDeptInfos.isEmpty()){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("userManagement.import.dataNotExist")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}									
						}
						
						Long deptId = getUserDeptFromDeptInfos(deptInfos,companyId,primaryDeptInfos.get(0),StringUtil.null2Str(val));
						if(deptId < 0){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.deptNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						user.setDeptId(deptId);
				        
						//校验第七列 岗位 可随意 填写
						columnNum++;
						val = ls.get(columnNum);
						user.setDuty(StringUtil.null2Str(val));
						
						//校验第八列 工作地点 必须是系统设置-“工作地点”中
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						user.setWorkArea(StringUtil.null2Str(val));
						if(!areas.contains(user.getWorkArea())){//校验工作地点是否存在
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第九列 管理人员 是否是对应“一级部门”或者“二级部门”的管理人员，方便获取数据权限
						columnNum++;
						val = ls.get(columnNum);
						user.setIsManager(StringUtil.nullToBoolean(val));
						
						//校验第十列 邮箱 要唯一，可不填写
						columnNum++;
						val = ls.get(columnNum);
						user.setEmail(StringUtil.null2Str(val));
						
						//校验第十一列 手机号 正常联系方式
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
						}else if(val instanceof Double){//double
							user.setTelephone(""+((Double)val).longValue());
						}else{
							user.setTelephone(StringUtil.null2Str(val));
						}
						
						//校验第十二列 性别 女/男
						columnNum++;
						val = ls.get(columnNum);
						if(val != null && val.equals("女")){
							user.setGender(2);
						}else{
							user.setGender(1);
						}
						
						//校验第十三列 出生年份 系统根据输入确定，最好四位数字，如1985
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
						}else if(val instanceof Double){//double
							user.setBirthYear(""+((Double)val).longValue());
						}else{
							user.setBirthYear(StringUtil.null2Str(val));
						}
						if(user.getBirthYear() != null && !(user.getBirthYear().length() == 0 || user.getBirthYear().length() == 4)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第十四列 生日 系统根据输入确定，最好为四位数字，如1212
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
						}else if(val instanceof Double){//double
							user.setBirthDay(""+((Double)val).longValue());
						}else{
							user.setBirthDay(StringUtil.null2Str(val));
						}
						if(user.getBirthDay() != null && !(user.getBirthDay().length() == 0 || user.getBirthDay().length() == 4)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第十五列 角色 必须在“系统设置”-“用户信息”-“新增”中的“角色”范围内
						columnNum++;
						val = ls.get(columnNum);
						Set<Authority> authorities = new HashSet<>();
						if(val != null && !StringUtil.isNullStr(val)){
							List<String> list = StringUtil.stringToStrList(StringUtil.null2Str(val), "[,|，]");
							if(list != null){
								for(String auth : list){
									if(authoritys.containsKey(auth)){
										authorities.add(authoritys.get(auth));
									}else{
										return ResponseEntity.ok().body(cpmResponse
												.setSuccess(Boolean.FALSE)
												.setMsgKey("userManagement.import.authNotExist")
												.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
									}
								}
							}
						}
						user.setAuthorities(authorities);
						
						users.add(user);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:"+e.getMessage(),e);
						return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("userManagement.import.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
					}
				}
			}
			
			//入库
			if(users != null ){
				for(User user : users){
					userService.saveOrUpdateUserForExcel(user);
				}
			}
			return ResponseEntity.ok().body(cpmResponse
					.setSuccess(Boolean.TRUE)
					.setMsgKey("userManagement.import.handleSucc"));
		} catch (IOException e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("userManagement.import.handleError"));
		}
		
    }
    /**
     * 根据一级部门，二级部门来判定用户的所属部门
     * @return
     */
	private Long getUserDeptFromDeptInfos(Map<String, List<DeptInfo>> deptInfos, Long companyId, DeptInfo primaryDeptInfo, String secondaryDept) {
		if(secondaryDept == null || StringUtil.isNullStr(secondaryDept)){
			return primaryDeptInfo.getId();
		}
		List<DeptInfo> secondaryDepts = deptInfos.get(companyId + "_" + secondaryDept);
		String likePath = "/"+primaryDeptInfo.getId()+"/";
		for(DeptInfo deptInfo : secondaryDepts){
			if(deptInfo.getIdPath().indexOf(likePath) != -1){//属于一级部门的子部门
				return deptInfo.getId();
			}
		}
		return -1L;
	}
			
}
