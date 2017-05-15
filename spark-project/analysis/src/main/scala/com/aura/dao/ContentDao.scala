package com.aura.dao

import java.sql.Connection
import com.aura.basic.BasicDao
import com.aura.entity.Content

object ContentDao {

  /**
    * 稿件数据写库
    * @param list
    * @param conn
    * @return
    */
  def saveContentData(list: List[Content], conn: Connection): Int = {
    val sql: String = "insert into sparkcore_content_data(contentId,`day`,pv,uv) values (#{contentId},#{day},#{pv},#{uv}) on duplicate key update pv = values(pv),uv = values(uv)"
    BasicDao.saveListBatch(sql, list, conn)
  }

  /**
    * 稿件详细信息写库
    * @param list
    * @param conn
    * @return
    */
  def saveContentDetail(list: List[Content], conn: Connection): Int = {
    val sql: String = "insert into sparkcore_content_detail(contentId,url,title) values (#{contentId},#{url},#{title}) on duplicate key update url = values(url),title = values(title)"
    BasicDao.saveListBatch(sql, list, conn)
  }

  /**
    * 实时稿件数据写库
    * @param list
    * @param conn
    * @return
    */
  def saveStreamingContentData(list: List[Content], conn: Connection): Int = {
    val sql: String = "insert into streaming_content_data(contentId,`second`,pv,uv) values (#{contentId},#{second},#{pv},#{uv}) on duplicate key update pv = values(pv),uv = values(uv)"
    BasicDao.saveListBatch(sql, list, conn)
  }

  /**
    * 实时稿件详细信息写库
    * @param list
    * @param conn
    * @return
    */
  def saveStreamingContentDetail(list: List[Content], conn: Connection): Int = {
    val sql: String = "insert into streaming_content_detail(contentId,url,title) values (#{contentId},#{url},#{title}) on duplicate key update url = values(url),title = values(title)"
    BasicDao.saveListBatch(sql, list, conn)
  }
}
