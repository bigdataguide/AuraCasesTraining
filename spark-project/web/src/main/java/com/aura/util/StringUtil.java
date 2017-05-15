package com.aura.util;

/**
 * @项目名称：专家系统
 * @类名称：StringUtil
 * @类描述：字符工具类
 * @创建人：
 * @创建时间：2012-09-07
 * @修改人：
 * @修改时间：
 * @备注：
 */
public class StringUtil {
	
	/**
	 * 字符串首字母大写
	 * @param 字符串
	 * @return 首字母大写的字符串
	 */
	public static String toFirstUpperCase(String str) {
		if(str == null || str.length() < 1) {
			return "";
		}
		String start = str.substring(0,1).toUpperCase();
		String end = str.substring(1, str.length());
		return start + end;
	}
	/**
	 * 标题长度截取
	 * @param title
	 * @return
	 */
	public static String limitTitle(String title,int count) {
		if(title != null && title.length() > count) {
			return title.substring(0, count) + "...";
		}
		return title;
	}
	/**
	 * 标题清理
	 * @param title
	 * @return
	 */
	public static String clearTitleAll(String title) {
		String result = clearTitle(title, "-");
		result = clearTitle(result, "_");
		return result;
	}
	/**
	 * 标题清理
	 * @param title
	 * @param symbol
	 * @return
	 */
	public static String clearTitle(String title, String symbol) {
		if(title == null) {
			return "";
		}
		int index = title.lastIndexOf(symbol);
		return index < 5 ? title : clearTitle(title.substring(0, index), symbol);
	}
	
	public static void main(String [] args) {
		String title = "习近平访问欧盟";
		System.out.println(StringUtil.limitTitle(title, 5));
	}
}