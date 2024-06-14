package com.learning.googleads.api.web;

public class CustomerDTO {
    private String customerId;
    private String resourceName;
    private String descriptiveName;
    private String currencyCode;
    private String timeZone;
    private Boolean testAccount;

    // Getters and setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public void setDescriptiveName(String descriptiveName) {
        this.descriptiveName = descriptiveName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getTestAccount() {
        return testAccount;
    }

    public void setTestAccount(Boolean testAccount) {
        this.testAccount = testAccount;
    }

    @Override
    public String toString() {
        return "CustomerDTO:" +
                "\n\tcustomerId='" + customerId + '\'' +
                "\n\tresourceName='" + resourceName + '\'' +
                "\n\tdescriptiveName='" + descriptiveName + '\'' +
                "\n\tcurrencyCode='" + currencyCode + '\'' +
                "\n\ttimeZone='" + timeZone + '\'' +
                "\n\ttestAccount=" + testAccount;
    }
}
