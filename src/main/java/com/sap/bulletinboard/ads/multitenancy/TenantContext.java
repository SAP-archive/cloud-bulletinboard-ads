package com.sap.bulletinboard.ads.multitenancy;

public class TenantContext {
    public static final String TENANT_ID = "tenantId";

    final static String DEFAULT_TENANT = "public";
    private static ThreadLocal<String> currentTenant = ThreadLocal.withInitial(() -> DEFAULT_TENANT);

    private TenantContext() {
        throw new IllegalStateException("Utility class");
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    // to be called as part of TenantFilter only
    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    // to be called as part of TenantFilter only
    public static void clear() {
        currentTenant.remove();
    }
}
