package com.sap.bulletinboard.ads.multitenancy;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.xs2.security.container.SecurityContext;
import com.sap.xs2.security.container.UserInfo;
import com.sap.xs2.security.container.UserInfoException;

public class TenantFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String tenantId = readTenantIdFromJWT();

        try {
            LogContext.add(TenantContext.TENANT_ID, tenantId);
            logger.info("Set current tenantId to: {}", tenantId);
            TenantContext.setCurrentTenant(tenantId);

            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            logger.info("Removed current tenantId: {}", tenantId);
            LogContext.remove(TenantContext.TENANT_ID);
        }
    }

    private String readTenantIdFromJWT() {
        String tenantId = null;
        try {
            UserInfo userInfo = SecurityContext.getUserInfo();
            tenantId = userInfo.getIdentityZone();
            logger.info("Read TenantId from JWT token (zid): {}", tenantId);
        } catch (UserInfoException e) {
            logger.error("UserInfoException, no tenant could be determined for this request.", e);
        }
        return tenantId;
    }

    @Override
    public void destroy() {
    }
}
