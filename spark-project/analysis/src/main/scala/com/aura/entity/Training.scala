package com.aura.entity

import scala.beans.BeanProperty

class Training extends Serializable {
  @BeanProperty var label: Int = -1
  @BeanProperty var title: String = ""

  @BeanProperty var genderId: Int = -1
  @BeanProperty var channelId: Int = -1
  @BeanProperty var day: String = ""
  @BeanProperty var pv: Int = 0
  @BeanProperty var uv: Int = 0
  @BeanProperty var ip: Int = 0

  var uvs = Set[String]()
  var ips = Set[String]()

  def this(pv: Int, uv: Int, ip: Int){
    this()
    this.pv = pv
    this.uv = uv
    this.ip = ip
  }
}
