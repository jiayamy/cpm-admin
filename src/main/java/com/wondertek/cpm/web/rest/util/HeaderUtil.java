package com.wondertek.cpm.web.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-cpmApp-alert", message);
        headers.add("X-cpmApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("cpmApp." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("cpmApp." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("cpmApp." + entityName + ".deleted", param);
    }
    
    public static HttpHeaders createEntityUploadAlert(String entityName, String param){
    	return createAlert("cpmApp." + entityName + ".uploaded", param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error("Entity creation failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-cpmApp-error", "error." + errorKey);
        headers.add("X-cpmApp-params", entityName);
        return headers;
    }
    
    public static HttpHeaders createError(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-cpmApp-error", message);
        headers.add("X-cpmApp-params", param);
        return headers;
    }
}
