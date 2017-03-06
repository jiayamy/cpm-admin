package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.web.rest.vm.LoggerVM;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/management")
public class LogsResource {
	
	private final Logger log = LoggerFactory.getLogger(LogsResource.class);
	
    @GetMapping("/logs")
    @Timed
    public List<LoggerVM> getList() {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get logs List");
    	LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLoggerList()
            .stream()
            .map(LoggerVM::new)
            .collect(Collectors.toList());
    }

    @PutMapping("/logs")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void changeLevel(@RequestBody LoggerVM jsonLogger) {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to change log Level : {}",jsonLogger);
    	LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    }
}
