package com.aura.spark.streaming;

import scala.Serializable;

public class LogRecord implements Serializable {
    private int dimId;
    private int second;
    private String uuid;
    private String ip;
    private String url;
    private String title;
    private long contentId;
    private String area;

    public LogRecord(int dimId, int second, String uuid, String ip, String url, String title, long contentId, String area) {
        this.dimId = dimId;
        this.second = second;
        this.uuid = uuid;
        this.ip = ip;
        this.url = url;
        this.title = title;
        this.contentId = contentId;
        this.area = area;
    }

    public int getDimId() {
        return dimId;
    }

    public void setDimId(int dimId) {
        this.dimId = dimId;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
