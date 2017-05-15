package com.aura.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {

	/**
	 * 事务提交
	 */
	public static void commit(Connection conn){
		try {
			if (conn != null){
				conn.commit();
				System.out.println("do commit...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 事务处理
	 */
	public static void setAutoCommit(Connection conn, boolean autoCommit){
		try {
			if (conn != null)
				conn.setAutoCommit(autoCommit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 批处理提交
	 */
	public static void executeBatch(Connection conn, PreparedStatement pstmt, int count) throws SQLException {
		if(count % 1000 == 0) {
			pstmt.executeBatch();
			DBHelper.commit(conn);
		} 
	}
	
	/**
	 * 释放rs stmt conn
	 */
	public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		close(rs);
		close(pstmt);
		close(conn);
	}
	
	/**
	 * 释放conn
	 */
	public static void close(Connection conn){
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放pstmt
	 */
	public static void close(PreparedStatement pstmt){
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放rs
	 */
	public static void close(ResultSet rs){
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		
	}

}
