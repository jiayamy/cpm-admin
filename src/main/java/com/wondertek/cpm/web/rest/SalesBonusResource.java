package com.wondertek.cpm.web.rest;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wondertek.cpm.service.SalesBonusService;

/**
 * REST controller for managing SalesBonus.
 */
@RestController
@RequestMapping("/api")
public class SalesBonusResource {

    private final Logger log = LoggerFactory.getLogger(SalesBonusResource.class);
        
    @Inject
    private SalesBonusService salesBonusService;
    
}
