package com.aura.basic;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;

public class BasicActionSupportImpl extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 得到HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	/**
	 * 得到HttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	/**
	 * 得到HttpSession
	 */
	public HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * 得到ServletContext
	 */
	public ServletContext getServletContext() {
		return ServletActionContext.getServletContext();
	}
}
