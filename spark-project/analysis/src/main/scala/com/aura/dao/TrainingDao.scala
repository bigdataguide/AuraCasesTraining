package com.aura.dao

import com.aura.basic.BasicSimpleDao
import com.aura.entity.Training

object TrainingDao {
  /**
    * 性别分类写库
    * @param list
    * @return
    */
  def saveGenderList(list: List[Training]): Int = {
    val sql: String = "insert into mllib_gender_data(genderid,`day`,pv,uv,ip) values (#{genderId},#{day},#{pv},#{uv},#{ip}) on duplicate key update pv = values(pv),uv = values(uv),ip = values(ip)"
    BasicSimpleDao.saveListBatch(sql, list)
  }

  /**
    * 频道分类写库
    * @param list
    * @return
    */
  def saveChannelList(list: List[Training]): Int = {
    val sql: String = "insert into mllib_channel_data(channelid,`day`,pv,uv,ip) values (#{channelId},#{day},#{pv},#{uv},#{ip}) on duplicate key update pv = values(pv),uv = values(uv),ip = values(ip)"
    BasicSimpleDao.saveListBatch(sql, list)
  }
}
