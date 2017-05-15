package com.aura.basic;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BasicDaoSupportImpl extends SqlSessionDaoSupport {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

//	@Autowired
//	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
//		super.setSqlSessionFactory(sqlSessionFactory);
//	}

	public List<?> selectList(String path, Object entity) {
		return getSqlSession().selectList(path, entity);
	}

	public Object selectObject(String path, Object entity) {
		return getSqlSession().selectOne(path, entity);
	}
	
	public int saveObject(String path, Object entity) {
		int count = 0;
		try {
			count = getSqlSession().insert(path, entity);
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}
	
	public int saveList(String path, List<?> entitys) {
		int count = 0;
		try {
			for(Object entity : entitys) {
				count += getSqlSession().insert(path, entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}
	
	public int deleteObject(String path, Object entity) {
		int count = 0;
		try {
			count = getSqlSession().delete(path, entity);
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}
	
	public int deleteList(String path, List<?> entitys) {
		int count = 0;
		try {
			for(Object entity : entitys) {
				count += getSqlSession().delete(path, entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}
	
	public int updateObject(String path, Object entity) {
		int count = 0;
		try {
			count = getSqlSession().update(path, entity);
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}
	
	public int updateList(String path, List<?> entitys) {
		int count = 0;
		try {
			for(Object entity : entitys) {
				count += getSqlSession().update(path, entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}
	
}
