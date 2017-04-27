package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.SystemConfig;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.SystemConfigService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api")
public class SystemConfigResource {

	private final Logger log = LoggerFactory.getLogger(SystemConfigResource.class);

	@Inject
	private SystemConfigService systemConfigService;

	@GetMapping("/system-config")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
	public ResponseEntity<List<SystemConfig>> getAllSystemConfig(
			@RequestParam(value = "query", required = false) String query, @ApiParam Pageable pageable)
					throws URISyntaxException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of SystemConfig");
		Page<SystemConfig> page = systemConfigService.getAllSystemConfig(query, pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/system-config");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/system-config/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
	public ResponseEntity<SystemConfig> getSystemConfig(@PathVariable Long id) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get SystemConfig : {}", id);
		SystemConfig systemConfig = systemConfigService.findOne(id);
		return Optional.ofNullable(systemConfig).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@SuppressWarnings("unused")
	@PutMapping("/system-config")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
	public ResponseEntity<Boolean> updateContractUser(@RequestBody SystemConfig systemConfig)
			throws URISyntaxException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update SystemConfig : {}", systemConfig);
		boolean isNew = systemConfig.getId() == null;
		SystemConfig sc = new SystemConfig();
		String updator = SecurityUtils.getCurrentUserLogin();
		ZonedDateTime updateTime = ZonedDateTime.now();
		sc.setUpdator(updator);
		sc.setUpdateTime(updateTime);
		sc.setId(systemConfig.getId());
		sc.setKey(systemConfig.getKey());
		
		String value = systemConfig.getValue();
		if (value == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.systemConfig.save.paramNone", ""))
					.body(null);
		} else {
			Integer lastIndexOf = value.lastIndexOf(",");
			if (value.length() == (lastIndexOf + 1)) {
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createError("cpmApp.systemConfig.save.styleError", "")).body(null);
			}
			String[] strings = value.split(",");
			for (String string : strings) {
				if(!StringUtil.isNumber(value)){
					return ResponseEntity.badRequest()
							.headers(HeaderUtil.createError("cpmApp.systemConfig.save.styleError", "")).body(null);
				}else{
					int parseInt = Integer.parseInt(string);
					if(parseInt==0){
						return ResponseEntity.badRequest()
								.headers(HeaderUtil.createError("cpmApp.systemConfig.save.valueError", "")).body(null);
					}
				}
			}
		}
		sc.setValue(value);

		String description = systemConfig.getDescription();
		if (description == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.systemConfig.save.paramNone", ""))
					.body(null);
		}
		sc.setDescription(description);

		SystemConfig result = systemConfigService.save(sc);
		if (isNew) {
			return ResponseEntity.ok()
					.headers(HeaderUtil.createEntityCreationAlert("systemConfig", result.getId().toString()))
					.body(isNew);
		} else {
			return ResponseEntity.ok()
					.headers(HeaderUtil.createEntityUpdateAlert("systemConfig", result.getId().toString())).body(isNew);
		}

	}
}
