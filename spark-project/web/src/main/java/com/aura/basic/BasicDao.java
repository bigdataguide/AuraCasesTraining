package com.aura.basic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.aura.database.DBHelper;

public abstract class BasicDao {
	
	/**
	 * 根据Sql和实体参数 查询数据库 返回结果集的第一个实体对象
	 * 1. setPreparedSql方法是对预置语句进行处理
	 * 2. getRsListFromMetaData方法是对结果集自动填充容器
	 * 3. 实体类里面需要调用的方法必须是对象类型 即int -> Integer
	 * @param sql
	 * @param conn
	 * @param entity
	 * @return
	 */
	public static Object getSqlObject(String sql, Object entity, Connection conn) throws Exception {
		List<?> list = getSqlList(sql, entity, conn);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据Sql和实体参数 查询数据库 返回结果集
	 * 1. setPreparedSql方法是对预置语句进行处理
	 * 2. getRsListFromMetaData方法是对结果集自动填充容器
	 * 3. 实体类里面需要调用的方法必须是对象类型 即int -> Integer
	 * @param sql
	 * @param entity
	 * @param conn
	 * @return
	 */
	public static List<?> getSqlList(String sql, Object entity, Connection conn) throws Exception {
		Class<?> cls = entity.getClass();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(getRealSql(sql));
			setPreparedSql(sql, pstmt, entity);
			rs = pstmt.executeQuery();
			return getRsListFromMetaData(rs, cls);
		} finally {
			DBHelper.close(rs);
			DBHelper.close(pstmt);
		}
	}
	
	/**
	 * 无where条件查询数据库 返回结果集
	 * 1. 最普通的查询不需要解析#{}里面的内容
	 * 2. getRsListFromMetaData方法是对结果集自动填充容器
	 * 3. 实体类里面需要调用的方法必须是对象类型 即int -> Integer
	 * @param sql
	 * @param cls
	 * @param conn
	 * @return
	 */
	public static List<?> getSqlList(String sql, Class<?> cls, Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			return getRsListFromMetaData(rs, cls);
		} finally {
			DBHelper.close(rs);
			DBHelper.close(pstmt);
		}
	}
	
	/**
	 * 保存数据
	 * @param sql
	 * @param entity
	 * @param conn
	 */
	public static int saveObject(String sql, Object entity, Connection conn) throws Exception {
		return executeSql(sql, entity, conn);
	}
	
	/**
	 * 保存数据集合
	 * @param sql
	 * @param entities
	 * @param conn
	 */
	public static int saveList(String sql, List<?> entities, Connection conn) throws Exception {
		int count = 0;
		for(Object entity : entities) {
			count += saveObject(sql, entity, conn);
		}
		return count;
	}
	
	/**
	 * 批处理保存数据集合
	 * @param sql
	 * @param entities
	 * @param conn
	 */
	public static int saveListBatch(String sql, Collection<?> entities, Connection conn) throws Exception {
		DBHelper.setAutoCommit(conn, false);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(getRealSql(sql));
			int count = 0;
			for(Object entity : entities) {
				setPreparedSql(sql, pstmt, entity);
				pstmt.addBatch();
				count++;
				DBHelper.executeBatch(conn, pstmt, count);
			}
			pstmt.executeBatch();
			DBHelper.commit(conn);
			return count;
		} finally {
			DBHelper.close(pstmt);
			DBHelper.setAutoCommit(conn, true);
		}
	}
	
	/**
	 * 删除数据
	 * @param sql
	 * @param entity
	 * @param conn
	 */
	public static int deleteObject(String sql, Object entity, Connection conn) throws Exception {
		return executeSql(sql, entity, conn);
	}
	
	/**
	 * 修改数据
	 * @param sql
	 * @param entity
	 * @param conn
	 */
	public static int updateObject(String sql, Object entity, Connection conn) throws Exception {
		return executeSql(sql, entity, conn);
	}
	
	/**
	 * 修改数据集合
	 * @param sql
	 * @param entities
	 * @param conn
	 */
	public static int updateList(String sql, Collection<?> entities, Connection conn) throws Exception {
		int count = 0;
		for(Object entity : entities) {
			count += updateObject(sql, entity, conn);
		}
		return count;
	}
	
	/**
	 * 用MetaData和反射，对结果集进行处理，返回集合
	 * @param rs
	 * @param cls
	 * @return
	 * @throws NoSuchFieldException 
	 */
	private static List<?> getRsListFromMetaData(ResultSet rs, Class<?> cls) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		List<Object> list = null;
		// MetaData
		ResultSetMetaData data = rs.getMetaData();
		int columnCount = data.getColumnCount();
		
		while(rs.next()){
			if(list == null) {
				list = new ArrayList<Object>();
			}
			Object clsInstance = cls.newInstance();
			for (int i = 1; i <= columnCount; i++) {
                // MetaData columnName
                String columnName = data.getColumnLabel(i); // data.getColumnName(i);
                // GetColumn return type
                Class<?> typeCls = cls.getMethod("get" + toFirstUpperCase(columnName)).getReturnType();
                // SetColumn
				Method method = cls.getMethod("set" + toFirstUpperCase(columnName), new Class[]{typeCls});
				// Integer String Float
				String simpleName = toFirstUpperCase(typeCls.getSimpleName());
				// 对Integer类型的特殊处理
				if(simpleName.equalsIgnoreCase("Integer")) {
					simpleName = "Int";
				}
				// Rs getString getInt getFloat
				Method rsMethod = rs.getClass().getMethod("get" + simpleName, new Class[]{String.class});
				// Rs getString getInt getFloat invoke
				Object value = rsMethod.invoke(rs, columnName);
				// SetColumn invoke
				method.invoke(clsInstance, new Object[]{value});
            }
			list.add(clsInstance);
		}
		return list;
	}
	
	/**
	 * 对带#{xxx}的sql语句进行处理，自动添加pstmt.setString(1, ...)等
	 * @param sql
	 * @param pstmt
	 * @param entity
	 */
	public static void setPreparedSql(String sql, PreparedStatement pstmt, Object entity) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException {
		if(sql.contains("#")) {
			Class<?> cls = entity.getClass();
			String [] sqlSplit = sql.split("#");
			for(int i=1; i<sqlSplit.length; i++) {
				String split = sqlSplit[i];
				String splitbefore = StringUtils.substringBeforeLast(split, "}").trim();
				String paramName = StringUtils.substringAfterLast(splitbefore, "{").trim();
				
				// getXxxx
				Method paramMethod = cls.getMethod("get" + toFirstUpperCase(paramName));
				// getXxxx invoke
				Object paramValue = paramMethod.invoke(entity);
				pstmt.setString(i, paramValue.toString());
			}
		}
	}
	
	/**
	 * 得到可以运行的sql,将#{xxx}的部分替换成?
	 * @param sql
	 */
	public static String getRealSql(String sql) {
		return sql.replaceAll("\\#\\{ *[a-z,A-Z,0-9,_]+ *\\}", "\\?");
	}
	
	/**
	 * 根据Sql和实体参数 对数据库进行 增,删,改 操作
	 * setPreparedSql方法是对预置语句进行处理
	 * @param sql
	 * @param entity
	 * @param conn
	 */
	public static int executeSql(String sql, Object entity, Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(getRealSql(sql));
			setPreparedSql(sql, pstmt, entity);
			return pstmt.executeUpdate();
		} finally {
			DBHelper.close(pstmt);
		}
	}
	
	/**
	 * 根据Sql对数据库进行 增,删,改 操作
	 * @param sql
	 * @param conn
	 */
	public static int executeSql(String sql, Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			return pstmt.executeUpdate();
		} finally {
			DBHelper.close(pstmt);
		}
	}

	/**
	 * 字符串首字母大写
	 * @param str
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

	public static void main(String[] args) {
		
	}

}
