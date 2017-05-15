package com.aura.service;

import com.aura.basic.BasicServiceSupportImpl;
import com.aura.dao.DimensionDao;
import com.aura.model.Dimension;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("dimensionService")
public class DimensionService extends BasicServiceSupportImpl {
	
	@Resource(name="dimensionDao")
	protected DimensionDao dimensionDao;
	
	/**
	 * Spark Streaming 地区分布
	 * @param dimension
	 * @return List<Dimension> 查询结果
	 */
	@SuppressWarnings("unchecked")
	public List<Dimension> getStreamProvinceList(Dimension dimension) {
		return (List<Dimension>)dimensionDao.selectList("common.dimension.getStreamProvinceList", dimension);
	}
	
	/**
	 * Spark Core 流量统计
	 * @param dimension
	 * @return List<Dimension> 查询结果
	 */
	@SuppressWarnings("unchecked")
	public List<Dimension> getMemoryList(Dimension dimension) {
		return (List<Dimension>)dimensionDao.selectList("common.dimension.getMemoryList", dimension);
	}
	
	/**
	 * Spark Core 维度信息
	 * @param dimension
	 * @return List<Dimension> 查询结果
	 */
	@SuppressWarnings("unchecked")
	public List<Dimension> getMemoryDimensionList(Dimension dimension) {
		return (List<Dimension>)dimensionDao.selectList("common.dimension.getMemoryDimensionList", dimension);
	}
}