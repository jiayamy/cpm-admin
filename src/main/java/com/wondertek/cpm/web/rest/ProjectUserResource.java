package com.wondertek.cpm.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

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
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.ExcelValue;
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.FilePathHelper;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectUserVo;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.service.ProjectUserService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectUser.
 */
@RestController
@RequestMapping("/api")
public class ProjectUserResource {

	private final Logger log = LoggerFactory.getLogger(ProjectUserResource.class);

	@Inject
	private ProjectUserService projectUserService;

	@Inject
	private ProjectInfoService projectInfoService;

	@Inject
	private UserService userService;

	@Inject
	private DeptInfoService deptInfoService;

	@Inject
	private UserRepository userRepository;

	/**
	 * PUT /project-users : Updates an existing projectUser.
	 *
	 * @param projectUser
	 *            the projectUser to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         projectUser, or with status 400 (Bad Request) if the projectUser
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         projectUser couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PutMapping("/project-users")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
	public ResponseEntity<Boolean> updateProjectUser(@RequestBody ProjectUser projectUser) throws URISyntaxException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ProjectUser : {}", projectUser);
		Boolean isNew = projectUser.getId() == null;
		if (projectUser.getProjectId() == null || projectUser.getUserId() == null
				|| StringUtil.isNullStr(projectUser.getUserName()) || StringUtil.isNullStr(projectUser.getUserRole())
				|| projectUser.getJoinDay() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.paramNone", ""))
					.body(null);
		}
		if (projectUser.getLeaveDay() != null && projectUser.getLeaveDay().longValue() < projectUser.getJoinDay()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.dayError", ""))
					.body(null);
		}
		// 查看用户是否被添加
		boolean isExist = projectUserService.checkUserExist(projectUser);
		if (isExist) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createError("cpmApp.projectUser.save.userIdError", "")).body(null);
		}
		// 查看项目是否删除或者结项
		ProjectInfo projectInfo = projectInfoService.findOne(projectUser.getProjectId());
		if (projectInfo.getStatus() != ProjectInfo.STATUS_ADD) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createError("cpmApp.projectUser.save.projectError", "")).body(null);
		}
		String updator = SecurityUtils.getCurrentUserLogin();
		ZonedDateTime updateTime = ZonedDateTime.now();
		if (isNew) {
			projectUser.setCreateTime(updateTime);
			projectUser.setCreator(updator);
		} else {
			ProjectUserVo projectUserVo = projectUserService.getProjectUser(projectUser.getId());
			if (projectUserVo == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.noPerm", ""))
						.body(null);
			}
			ProjectUser old = projectUserService.findOne(projectUser.getId());
			if (old == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.idNone", ""))
						.body(null);
			} else if (old.getProjectId() != projectUser.getProjectId().longValue()) {
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createError("cpmApp.projectUser.save.projectIdError", "")).body(null);
			}
			long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
			if (old.getLeaveDay() != null && old.getLeaveDay() <= leaveDay) {
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createError("cpmApp.projectUser.save.leaveDayError", "")).body(null);
			}
			projectUser.setCreateTime(old.getCreateTime());
			projectUser.setCreator(old.getCreator());
		}
		projectUser.setUpdateTime(updateTime);
		projectUser.setUpdator(updator);

		ProjectUser result = projectUserService.save(projectUser);
		if (isNew) {
			return ResponseEntity.ok()
					.headers(HeaderUtil.createEntityCreationAlert("projectUser", result.getId().toString()))
					.body(isNew);
		} else {
			return ResponseEntity.ok()
					.headers(HeaderUtil.createEntityUpdateAlert("projectUser", result.getId().toString())).body(isNew);
		}
	}

	/**
	 * GET /project-users : get all the projectUsers.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         projectUsers in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@GetMapping("/project-users")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
	public ResponseEntity<List<ProjectUserVo>> getAllProjectUsers(
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "userId", required = false) Long userId, @ApiParam Pageable pageable)
					throws URISyntaxException {

		log.debug(
				SecurityUtils.getCurrentUserLogin()
						+ " REST request to get a page of ProjectUsers by projectId : {}, userId : {}",
				projectId, userId);
		ProjectUser projectUser = new ProjectUser();
		projectUser.setProjectId(projectId);
		projectUser.setUserId(userId);

		Page<ProjectUserVo> page = projectUserService.getUserPage(projectUser, pageable);

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-users");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /project-users/:id : get the "id" projectUser.
	 *
	 * @param id
	 *            the id of the projectUser to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         projectUser, or with status 404 (Not Found)
	 */
	@GetMapping("/project-users/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
	public ResponseEntity<ProjectUserVo> getProjectUser(@PathVariable Long id) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ProjectUser : {}", id);
		ProjectUserVo projectUserVo = projectUserService.getProjectUser(id);
		return Optional.ofNullable(projectUserVo).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /project-users/:id : delete the "id" projectUser.
	 *
	 * @param id
	 *            the id of the projectUser to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/project-users/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
	public ResponseEntity<Void> deleteProjectUser(@PathVariable Long id) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ProjectUser : {}", id);
		ProjectUserVo projectUserVo = projectUserService.getProjectUser(id);
		if (projectUserVo == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.noPerm", ""))
					.body(null);
		}
		long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
		if (projectUserVo.getLeaveDay() != null && projectUserVo.getLeaveDay() <= leaveDay) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createError("cpmApp.projectUser.save.leaveDayError", "")).body(null);
		}
		// 查看项目是否删除或者结项
		ProjectInfo projectInfo = projectInfoService.findOne(projectUserVo.getProjectId());
		if (projectInfo.getStatus() != ProjectInfo.STATUS_ADD) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createError("cpmApp.projectUser.save.projectError", "")).body(null);
		}
		projectUserService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectUser", id.toString())).build();
	}

	@GetMapping("/project-user/exportXls")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
	public void exportXls(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "userId", required = false) Long userId) throws IOException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to exportXls : projectId:{},userId:{}",
				projectId, userId);
		ProjectUser searchParams = new ProjectUser();
		searchParams.setProjectId(projectId);
		searchParams.setUserId(userId);
		List<ProjectUserVo> list = projectUserService.getProjectUserList(searchParams);
		// 拼接sheet数据
		// 标题
		String[] heads = new String[] { "项目编号", "项目名称", "员工", "角色", "加盟日", "离开日" };
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String now = sdf.format(date);
		String fileName = "项目人员信息_" + now + ".xlsx";
		// 写入sheet
		ServletOutputStream outputStream = response.getOutputStream();
		response.setHeader("Content-Disposition", "attachment;filename=" + ExcelUtil.getExportName(request, fileName));
		response.setContentType("application/x-msdownload");
		response.setCharacterEncoding("UTF-8");
		ExcelWrite excelWrite = new ExcelWrite();
		// 写入标题
		excelWrite.createSheetTitle("咨询", 1, heads);
		// 写入数据
		if (list != null) {
			handleSheetData(list, 2, excelWrite);
		}
		excelWrite.close(outputStream);
	}

	private void handleSheetData(List<ProjectUserVo> list, int startRow, ExcelWrite excelWrite) {
		Integer[] cellType = new Integer[] { Cell.CELL_TYPE_STRING, Cell.CELL_TYPE_STRING, Cell.CELL_TYPE_STRING,
				Cell.CELL_TYPE_STRING, Cell.CELL_TYPE_STRING, Cell.CELL_TYPE_STRING, Cell.CELL_TYPE_STRING };
		XSSFSheet sheet = excelWrite.getCurrentSheet();
		XSSFWorkbook wb = excelWrite.getXSSFWorkbook();
		XSSFRow row = null;
		XSSFCell cell = null;
		int i = -1;
		int j = 0;
		// 百分比格式
		XSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		// 数据
		for (ProjectUserVo vo : list) {
			i++;
			row = sheet.createRow(i + startRow - 1);

			j = 0;
			cell = row.createCell(j, cellType[j]);
			if (vo.getProjectNum() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(vo.getProjectNum());
			}
			j++;
			cell = row.createCell(j, cellType[j]);
			if (vo.getProjectName() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(vo.getProjectName());
			}
			j++;
			cell = row.createCell(j, cellType[j]);
			if (vo.getUserName() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(vo.getUserName());
			}
			j++;
			cell = row.createCell(j, cellType[j]);
			if (vo.getUserRole() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(vo.getUserRole());
			}
			j++;
			cell = row.createCell(j, cellType[j]);
			if (vo.getJoinDay() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(vo.getJoinDay());
			}
			j++;
			cell = row.createCell(j, cellType[j]);
			if (vo.getLeaveDay() == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(vo.getLeaveDay());
			}
			j++;
		}

	}

	@GetMapping("/project-users/uploadExcel")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
	public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value = "filePath", required = true) String filePath)
			throws URISyntaxException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to uploadExcel for file : {}", filePath);
		List<ProjectUser> users = null;
		CpmResponse cpmResponse = new CpmResponse();
		try {
			// 校验文件是否存在
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH, filePath));
			if (!file.exists() || !file.isFile()) {
				return ResponseEntity.ok()
						.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.requiredError"));
			}
			// 从第一行读取，最多读取10个sheet，最多读取7列
			int startNum = 1;
			List<ExcelValue> lists = ExcelUtil.readExcel(file, startNum, 10, 7);
			if (lists == null || lists.isEmpty()) {
				return ResponseEntity.ok()
						.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.requiredError"));
			}
			// 初始信息
			// 得到项目编号和项目ID
			Map<String, Long> projectInfos = projectInfoService.getProjectInfo();
			// 得到员工编号员工ID
			Map<String, String> allUser = userService.getAllUsers();
			// 其他信息
			users = new ArrayList<ProjectUser>();
			String updator = SecurityUtils.getCurrentUserLogin();
			ZonedDateTime updateTime = ZonedDateTime.now();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			Long projectId = null;
			User user = null;
			DeptInfo deptInfo = null;
			for (ExcelValue excelValue : lists) {
				if (excelValue.getVals() == null || excelValue.getVals().isEmpty()) {// 每个sheet也可能没有数据，空sheet
					continue;
				}
				rowNum = 1;// 都是从第一行读取的。
				for (List<Object> ls : excelValue.getVals()) {
					rowNum++;
					if (ls == null) {// 每个sheet里面也可能有空行。
						continue;
					}
					try {
						ProjectUser projectUser = new ProjectUser();

						projectUser.setUpdator(updator);
						projectUser.setUpdateTime(updateTime);
						projectUser.setCreateTime(updateTime);
						projectUser.setCreator(updator);
						// 校验第一列，项目编号， 查看导入的列是否在数据库中存在。
						columnNum = 0;
						val = ls.get(columnNum);
						if (val == null || StringUtil.isNullStr(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						// 根据项目编号得到项目id
						projectId = projectInfos.get(val);
						// 校验项目编号是否存在。
						if (projectId == null) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataNotExist")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						projectUser.setProjectId(projectInfos.get(StringUtil.nullToString(val)));
						// 项目名称，可以不填写，不用校验
						columnNum++;
						// 校验第三列，员工编号，查看导入的员工编号是否存在。
						columnNum++;
						val = ls.get(columnNum);
						if (val == null || StringUtil.isNullStr(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						if (!allUser.containsKey(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataNotExist")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						// 员工编号转为字符串user_serial_num
						String user_serial_num = (String) val;
						// 根据员工编号得到员工id
						long userIdBySerialNum = userService.getUserId(user_serial_num);
						projectUser.setUserId(userIdBySerialNum);
						// 根据员工编号得到对应的员工所在的部门信息
						deptInfo = deptInfoService.findDeptInfo(user_serial_num);
						// 校验第四列，员工姓名,查看在数据库中员工编号是否与员工姓名相对应。
						columnNum++;
						val = ls.get(columnNum);
						// 对员工姓名进行字符编码的转化
						val = new String(val.toString().getBytes(), "gbk");
						if (val == null || StringUtil.isNullStr(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						if (!allUser.get(user_serial_num).equals(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataNotExist")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						projectUser.setUserName(StringUtil.null2Str(val));
						
						// 校验第五列，角色
						columnNum++;
						val = ls.get(columnNum);
						if (val == null || StringUtil.isNullStr(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						projectUser.setUserRole(StringUtil.null2Str(val));
						
						//校验级别
						columnNum++;
						val = ls.get(columnNum);
						//根据项目id得到项目所属的合同是否是外包
						long type = projectUserService.getContractType(projectId);
						if(type == ContractInfo.TYPE_EXTERNAL){
							if(val == null){
								return ResponseEntity.ok().body(cpmResponse.setSuccess(Boolean.FALSE)
										.setMsgKey("合同为外包合同，人员的等级必须填写"));
							}else{
								
							}
						}else{
							
						}
						
						
						
						// 校验第六列，加盟日
						columnNum++;
						val = ls.get(columnNum);
						if (val == null || StringUtil.isNullStr(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						// 对从Excel传来的数据进行处理。
						DecimalFormat df = new DecimalFormat("0");
						String value = df.format(val);
						if (value.length() != 8) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.dateLengthError"));
						} else {
							if (Integer.parseInt(value.substring(4, 6)) > 12) {
				
								return ResponseEntity.ok()
										.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.monthStyleError"));
							} else {
								if (Integer.parseInt(value.substring(4, 6)) == 1
										|| Integer.parseInt(value.substring(4, 6)) == 3
										|| Integer.parseInt(value.substring(4, 6)) == 5
										|| Integer.parseInt(value.substring(4, 6)) == 7
										|| Integer.parseInt(value.substring(4, 6)) == 8
										|| Integer.parseInt(value.substring(4, 6)) == 10
										|| Integer.parseInt(value.substring(4, 6)) == 12) {
									if (Integer.parseInt(value.substring(6, 8)) > 31) {
										return ResponseEntity.ok().body(cpmResponse.setSuccess(Boolean.FALSE)
												.setMsgKey("cpmApp.projectUser.save.dayStyleError"));
									}
								}
								if (Integer.parseInt(value.substring(4, 6)) == 4
										|| Integer.parseInt(value.substring(4, 6)) == 6
										|| Integer.parseInt(value.substring(4, 6)) == 9
										|| Integer.parseInt(value.substring(4, 6)) == 11) {
									if (Integer.parseInt(value.substring(6, 8)) > 30) {
										return ResponseEntity.ok().body(cpmResponse.setSuccess(Boolean.FALSE)
												.setMsgKey("cpmApp.projectUser.save.dayStyleError"));
									}
								}
								if (Integer.parseInt(value.substring(0, 4)) % 4 == 0
										&& Integer.parseInt(value.substring(0, 4)) % 100 != 0
										|| Integer.parseInt(value.substring(0, 4)) % 400 == 0) {
									if (Integer.parseInt(value.substring(4, 6)) == 2) {
										if (Integer.parseInt(value.substring(6, 8)) > 28) {
											return ResponseEntity.ok()
													.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.dayStyleError"));
										}
									}
								} else {
									if (Integer.parseInt(value.substring(4, 6)) == 2) {
										if (Integer.parseInt(value.substring(6, 8)) > 29) {
											return ResponseEntity.ok()
													.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.dayStyleError"));
										}
									}
								}
							}

						}

						long joinDay = Long.parseLong(value);
						projectUser.setJoinDay(joinDay);

						// 校验第七列，离开日。
						columnNum++;
						long leaveDay = 0;
						if (ls.size() > columnNum) {
							val = ls.get(columnNum);
							String ld = df.format(val);
							
							if (ld.length() != 8) {
								return ResponseEntity.ok()
										.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.dateLengthError"));
							} else {
								if (Integer.parseInt(ld.substring(4, 6)) > 12) {
					
									return ResponseEntity.ok()
											.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.monthStyleError"));
								} else {
									if (Integer.parseInt(ld.substring(4, 6)) == 1
											|| Integer.parseInt(ld.substring(4, 6)) == 3
											|| Integer.parseInt(ld.substring(4, 6)) == 5
											|| Integer.parseInt(ld.substring(4, 6)) == 7
											|| Integer.parseInt(ld.substring(4, 6)) == 8
											|| Integer.parseInt(ld.substring(4, 6)) == 10
											|| Integer.parseInt(ld.substring(4, 6)) == 12) {
										if (Integer.parseInt(ld.substring(6, 8)) > 31) {
											return ResponseEntity.ok().body(cpmResponse.setSuccess(Boolean.FALSE)
													.setMsgKey("cpmApp.projectUser.save.dayStyleError"));
										}
									}
									if (Integer.parseInt(ld.substring(4, 6)) == 4
											|| Integer.parseInt(ld.substring(4, 6)) == 6
											|| Integer.parseInt(ld.substring(4, 6)) == 9
											|| Integer.parseInt(ld.substring(4, 6)) == 11) {
										if (Integer.parseInt(ld.substring(6, 8)) > 30) {
											return ResponseEntity.ok().body(cpmResponse.setSuccess(Boolean.FALSE)
													.setMsgKey("cpmApp.projectUser.save.dayStyleError"));
										}
									}
									if (Integer.parseInt(ld.substring(0, 4)) % 4 == 0
											&& Integer.parseInt(ld.substring(0, 4)) % 100 != 0
											|| Integer.parseInt(ld.substring(0, 4)) % 400 == 0) {
										if (Integer.parseInt(ld.substring(4, 6)) == 2) {
											if (Integer.parseInt(ld.substring(6, 8)) > 28) {
												return ResponseEntity.ok()
														.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.dayStyleError"));
											}
										}
									} else {
										if (Integer.parseInt(ld.substring(4, 6)) == 2) {
											if (Integer.parseInt(ld.substring(6, 8)) > 29) {
												return ResponseEntity.ok()
														.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.dayStyleError"));
											}
										}
									}
								}

							}
							leaveDay = Long.parseLong(ld);
							if (leaveDay < joinDay) {
								return ResponseEntity.badRequest()
										.headers(HeaderUtil.createError("cpmApp.projectUser.save.dayError", ""))
										.body(null);
							}
							projectUser.setLeaveDay(leaveDay);
						}
						// 根据项目id和员工编号查看w_project_user表中的加盟日和离开日
						Map<Long, Long> date = projectUserService.getdates(projectId, userIdBySerialNum);
						if (date != null) {
							Set<Entry<Long, Long>> entryLong = date.entrySet();
							for (Entry<Long, Long> entry : entryLong) {
								if (entry.getValue() == null) {
									if (leaveDay >= entry.getKey() || leaveDay == 0) {
										return ResponseEntity
												.badRequest().headers(HeaderUtil
														.createError("cpmApp.projectUser.save.userIdError", ""))
												.body(null);
									}
								} else {
									if (leaveDay == 0) {
										if (joinDay <= entry.getValue()) {
											return ResponseEntity.badRequest()
													.headers(HeaderUtil
															.createError("cpmApp.projectUser.save.userIdError", ""))
													.body(null);
										}
									} else {
										if (leaveDay >= entry.getKey() && joinDay <= entry.getKey()) {
											return ResponseEntity.badRequest()
													.headers(HeaderUtil
															.createError("cpmApp.projectUser.save.userIdError", ""))
													.body(null);
										}
										if (joinDay >= entry.getKey() && leaveDay <= entry.getValue()) {
											return ResponseEntity.badRequest()
													.headers(HeaderUtil
															.createError("cpmApp.projectUser.save.userIdError", ""))
													.body(null);
										}
										if (joinDay <= entry.getValue() && leaveDay >= entry.getValue()) {
											return ResponseEntity.badRequest()
													.headers(HeaderUtil
															.createError("cpmApp.projectUser.save.userIdError", ""))
													.body(null);
										}
									}
								}

							}
						}
						// 根据员工编号得到员工信息
						user = userRepository.getAllBySerialNum(user_serial_num);
						users.add(projectUser);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:" + e.getMessage(), e);
						return ResponseEntity.ok()
								.body(cpmResponse.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.projectUser.save.dataIsError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
					}
				}
			}
			// 入库
			if (users != null) {
				for (ProjectUser projectUser : users) {
					projectUserService.saveOrUpdateUserForExcel(projectUser, user, deptInfo);
				}
			}
			return ResponseEntity.ok()
					.body(cpmResponse.setSuccess(Boolean.TRUE).setMsgKey("cpmApp.projectUser.save.importSuccess"));
		} catch (IOException e) {
			log.error("msg:" + e.getMessage(), e);
			return ResponseEntity.ok()
					.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.projectUser.save.importError"));
		}

	}
}
