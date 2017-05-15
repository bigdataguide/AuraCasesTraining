package com.aura.basic

import java.sql.Connection
import scala.collection.mutable.ListBuffer

import com.aura.db.DBHelper

object BasicSimpleDao {
  
  /**
	 * 根据Sql和实体参数 查询数据库 返回结果集的第一个实体对象
	 * @param sql
	 * @param entity
	 * @return
	 */
	def getSqlObject(sql: String, entity: Object): Any = {
		 val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.getSqlObject(sql, entity, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
	
	/**
	 * 根据Sql和实体参数 查询数据库 返回结果集
	 * @param sql
	 * @param entity
	 * @return
	 */
	def getSqlList(sql: String, entity: Object): ListBuffer[Any] = {
	   val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.getSqlList(sql, entity, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
	
	/**
	 * 无where条件查询数据库 返回结果集
	 * @param sql
	 * @param cls
	 * @return
	 */
	def getSqlList(sql: String, cls: Class[_]): ListBuffer[Any] = {
		 val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.getSqlList(sql, cls, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
  
  /**
	 * 保存数据
	 * @param sql
	 * @param entity
	 */
	def saveObject(sql: String, entity: Object): Int = {
	   val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.saveObject(sql, entity, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
	
	/**
	 * 保存数据集合
	 * @param sql
	 * @param entities
	 */
	def saveList(sql: String, entities: List[Object]): Int = {
	   val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.saveList(sql, entities, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
	
	/**
	 * 批处理保存数据集合
	 * @param sql
	 * @param entities
	 */
  def saveListBatch(sql: String, entities: List[Object]): Int = {
     val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.saveListBatch(sql, entities, conn)
     } finally {
       DBHelper.close(conn)
     }
  }
  
  /**
	 * 删除数据
	 * @param sql
	 * @param entity
	 */
	def deleteObject(sql: String, entity: Object): Int = {
	   val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.deleteObject(sql, entity, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
	
	/**
	 * 修改数据
	 * @param sql
	 * @param entity
	 */
	def updateObject(sql: String, entity: Object): Int = {
	   val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.updateObject(sql, entity, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
	
	/**
	 * 修改数据集合
	 * @param sql
	 * @param entities
	 */
	def updateList(sql: String, entities: List[Object]): Int = {
	   val conn: Connection = DBHelper.getConnection()
     try {
       BasicDao.updateList(sql, entities, conn)
     } finally {
       DBHelper.close(conn)
     }
	}
}