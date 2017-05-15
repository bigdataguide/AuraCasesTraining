package com.aura.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.aura.basic.BasicServiceSupportImpl;
import com.aura.dao.LearningDao;
import com.aura.model.Learning;

@Service("learningService")
public class LearningService extends BasicServiceSupportImpl {
	
	@Resource(name="learningDao")
	protected LearningDao learningDao;
	
	/**
	 * Spark MLlib 性别分类
	 * @param learning
	 * @return List<Learning> 查询结果
	 */
	@SuppressWarnings("unchecked")
	public List<Learning> getGenderList(Learning learning) {
		return (List<Learning>)learningDao.selectList("common.learning.getGenderList", learning);
	}
	
	/**
	 * Spark MLlib 频道分类
	 * @param learning
	 * @return List<Learning> 查询结果
	 */
	@SuppressWarnings("unchecked")
	public List<Learning> getChannelList(Learning learning) {
		return (List<Learning>)learningDao.selectList("common.learning.getChannelList", learning);
	}
}