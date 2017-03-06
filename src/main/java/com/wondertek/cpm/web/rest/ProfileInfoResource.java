package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.config.DefaultProfileUtil;
import com.wondertek.cpm.config.JHipsterProperties;
import com.wondertek.cpm.security.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resource to return information about the currently running Spring profiles.
 */
@RestController
@RequestMapping("/api")
public class ProfileInfoResource {
	
	private final Logger log = LoggerFactory.getLogger(ProfileInfoResource.class);
	
    @Inject
    private Environment env;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @GetMapping("/profile-info")
    public ProfileInfoResponse getActiveProfiles() {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get Active Profiles");
    	String[] activeProfiles = DefaultProfileUtil.getActiveProfiles(env);
        return new ProfileInfoResponse(activeProfiles, getRibbonEnv(activeProfiles));
    }

    private String getRibbonEnv(String[] activeProfiles) {
        String[] displayOnActiveProfiles = jHipsterProperties.getRibbon().getDisplayOnActiveProfiles();

        if (displayOnActiveProfiles == null) {
            return null;
        }

        List<String> ribbonProfiles = new ArrayList<>(Arrays.asList(displayOnActiveProfiles));
        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        ribbonProfiles.retainAll(springBootProfiles);

        if (ribbonProfiles.size() > 0) {
            return ribbonProfiles.get(0);
        }
        return null;
    }

    class ProfileInfoResponse {

        public String[] activeProfiles;
        public String ribbonEnv;

        ProfileInfoResponse(String[] activeProfiles, String ribbonEnv) {
            this.activeProfiles = activeProfiles;
            this.ribbonEnv = ribbonEnv;
        }
    }
}
