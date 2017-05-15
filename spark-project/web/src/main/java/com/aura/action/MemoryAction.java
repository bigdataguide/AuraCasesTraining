package com.aura.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.aura.basic.BasicActionSupportImpl;
import com.aura.model.Content;
import com.aura.model.Dimension;
import com.aura.service.ContentService;
import com.aura.service.DimensionService;
import com.aura.util.JsonHelper;

@Controller("memoryAction")
public class MemoryAction extends BasicActionSupportImpl {
	
	private static final long serialVersionUID = 1L;
	
	@Resource(name="dimensionService")
	private DimensionService dimensionService;
	
	@Resource(name="contentService")
	private ContentService contentService;
	
	/**
	 * Spark Core 流量统计
	 */
	public void getMemoryList() {		
		Dimension dimension = new Dimension();
		List<Dimension> list = dimensionService.getMemoryList(dimension);
		JsonHelper.printBasicJsonList(getResponse(), list);
	}
	
	/**
	 * Spark Core 维度信息
	 */
	public void getDimensionList() {
		String type = this.getRequest().getParameter("type");
		
		Dimension dimension = new Dimension();
		dimension.setType(type);
		List<Dimension> list = dimensionService.getMemoryDimensionList(dimension);
		JsonHelper.printBasicJsonList(getResponse(), list);
	}
	
	/**
	 * Spark Core 稿件排行
	 */
	public void getMemoryContentList() {
		Content content = new Content();
		List<Content> list = contentService.getMemoryContentList(content);
		JsonHelper.printBasicJsonList(getResponse(), list);
	}
}
