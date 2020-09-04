package com.mrserviceman.messenger.Model.AnalyticsFragMent;

public class SocialApps {
    private String appName;
    private String url;
    private String website;

    public SocialApps(String appName, String url,String website) {
        this.appName = appName;
        this.url = url;
        this.website=website;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
