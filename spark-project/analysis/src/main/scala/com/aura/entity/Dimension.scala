package com.aura.entity

import scala.beans.BeanProperty

class Dimension extends Serializable {
  var Ts: Long = 0L

  @BeanProperty var dimeId: Int = 0
  @BeanProperty var day: String = ""
  @BeanProperty var pv: Int = 0
  @BeanProperty var uv: Int = 0
  @BeanProperty var ip: Int = 0
  @BeanProperty var time: Long = 0L

  @BeanProperty var second: Int = 0

  @BeanProperty var `type`: String = ""
  @BeanProperty var value: String = ""

  var uvs = Set[String]()
  var ips = Set[String]()

  def this(pv: Int, uv: Int, ip: Int){
    this()
    this.pv = pv
    this.uv = uv
    this.ip = ip
  }
}
