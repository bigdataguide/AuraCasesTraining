package com.aura.model;

import com.aura.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Log implements Serializable {

    @JsonProperty("Ts") private long ts;
    @JsonProperty("Ip") private String ip;
    @JsonProperty("UUid") private String uuid;
    @JsonProperty("Country") private String country;
    @JsonProperty("Area") private String area;
    @JsonProperty("ContentId") private long contentId;
    @JsonProperty("Url") private String url;
    @JsonProperty("Title") private String title;
    @JsonProperty("Wd") private Wd wd;

    public Log() {}

    public int getPageType() {
        if (wd != null && wd.getT() != null) {
            String t = wd.getT();
            if (t.length() == 3) {
                char pt = t.charAt(2);
                if (pt >= '0' && pt <= '5') {
                    return pt - '0';
                }
            }
        }
        return 6;
    }

    public String getClearTitle() {
        if (title != null) {
            return StringUtil.clearTitleAll(title);
        } else {
            return "";
        }
    }

    public boolean isLegal() {
        return ip != null && !ip.isEmpty() && uuid != null && !uuid.isEmpty();
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Wd getWd() {
        return wd;
    }

    public void setWd(Wd wd) {
        this.wd = wd;
    }
}
