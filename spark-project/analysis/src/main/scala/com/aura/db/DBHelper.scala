package com.aura.db

import java.sql.{Connection, PreparedStatement, ResultSet}

object DBHelper {
  
  /**
   * 获得默认的数据库连接
   */
  def getConnection(): Connection = {
    // return JDBCHelper.getConnection()
		C3P0Helper.getConnection()
	}
  /**
	 * 事务提交
	 */
	def commit(conn: Connection): Unit = {
	  if (conn != null){
	    conn.commit()
			println("do commit...")
	  }
	}
	
	/**
	 * 事务处理
	 */
	def setAutoCommit(conn: Connection, autoCommit: Boolean): Unit = {
	  if (conn != null) {
	    conn.setAutoCommit(autoCommit)
	  }
	}
	
	/**
	 * 批处理提交
	 */
	def executeBatch(conn: Connection, pstmt: PreparedStatement, count: Int): Unit = {
	  if(count % 1000 == 0) {
	    pstmt.executeBatch()
	    DBHelper.commit(conn)
  	} 
	}
	
	/**
	 * 释放rs stmt conn
	 */
	def close(conn: Connection, pstmt: PreparedStatement, rs: ResultSet): Unit = {
	  close(rs)
		close(pstmt)
		close(conn)
	}
	
	/**
	 * 释放conn
	 */
	def close(conn: Connection): Unit = {
	  if (conn != null) {
	    conn.close()
	  }
	}
	
	/**
	 * 释放pstmt
	 */
	def close(pstmt: PreparedStatement): Unit = {
	  if (pstmt != null) {
	    pstmt.close()
	  }
	}
	
	/**
	 * 释放rs
	 */
	def close(rs: ResultSet): Unit = {
	  if (rs != null) {
	    rs.close()
	  }
	}
  
  def main(args: Array[String]): Unit = {
    
  }
}