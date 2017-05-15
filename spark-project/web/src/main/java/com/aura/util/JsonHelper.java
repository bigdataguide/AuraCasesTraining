package com.aura.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JsonHelper {
	
	private static Logger log = LoggerFactory.getLogger(JsonHelper.class);
	
	/**
	 * 输出对象格式的JSON
	 */
	public static void printJsonObject(HttpServletResponse response, Object object) {
		String json = JSON.toJSONString(object);
		log.debug("jsonList: " + json);
		response.setContentType("text/json;charset=UTF-8");
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 输出Success格式的JSON
	 */
	public static void printJsonSuccess(HttpServletResponse response, boolean flag) {
		String json = "{\"success\": " + flag + "}";
		log.debug("jsonList: " + json);
		response.setContentType("text/json;charset=UTF-8");
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 输出基础JSON
	 */
	public static void printBasicJsonList(HttpServletResponse response, List<?> list) {
		String json = JSON.toJSONString(list);
		log.debug("jsonList: " + json);
		response.setContentType("text/json;charset=UTF-8");
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 输出基础JSON
	 */
	public static void printBasicJsonObject(HttpServletResponse response, Object object) {
		String json = JSON.toJSONString(object);
		log.debug("jsonList: " + json);
		response.setContentType("text/json;charset=UTF-8");
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 输出DataTables格式的JSON
	 */
	public static void printJsonList(HttpServletResponse response, List<?> list) {
		int draw = 1;
		int recordsTotal = list != null ? list.size() : 0;
		int recordsFiltered = recordsTotal;
		printJsonList(response, list, draw, recordsTotal, recordsFiltered);
	}
	
	/**
	 * 输出DataTables格式的JSON
	 */
	public static void printJsonList(HttpServletResponse response, List<?> list, int draw, int recordsTotal, int recordsFiltered) {
		DataTables dataTables = new DataTables();
		dataTables.setDraw(draw);
		dataTables.setRecordsTotal(recordsTotal);
		dataTables.setRecordsFiltered(recordsFiltered);
		dataTables.setData(list);
		String json = JSON.toJSONString(dataTables);
		log.debug("jsonList: " + json);
		response.setContentType("text/json;charset=UTF-8");
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class DataTables {
	private int draw;
	private int recordsTotal;
	private int recordsFiltered;
	private List<?> data;
	
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public int getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public int getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public List<?> getData() {
		return data;
	}
	public void setData(List<?> data) {
		this.data = data;
	}
}