package com.aura.model;

public class Content {
	private Integer startSecond;
	private Integer endSecond;
	private Long contentId;
	private Integer dimeId;
	private String url;
	private String title;
	private Integer pv;
	private Integer uv;
	
	private Integer value;
	
	public Integer getStartSecond() {
		return startSecond;
	}
	public void setStartSecond(Integer startSecond) {
		this.startSecond = startSecond;
	}
	public Integer getEndSecond() {
		return endSecond;
	}
	public void setEndSecond(Integer endSecond) {
		this.endSecond = endSecond;
	}
	public Long getContentId() {
		return contentId;
	}
	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}
	public Integer getDimeId() {
		return dimeId;
	}
	public void setDimeId(Integer dimeId) {
		this.dimeId = dimeId;
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
	public Integer getPv() {
		return pv;
	}
	public void setPv(Integer pv) {
		this.pv = pv;
	}
	public Integer getUv() {
		return uv;
	}
	public void setUv(Integer uv) {
		this.uv = uv;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	
}
