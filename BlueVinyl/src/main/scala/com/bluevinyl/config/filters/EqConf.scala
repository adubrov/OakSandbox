package com.bluevinyl.config.filters

import java.util

import scala.beans.BeanProperty

/**
  * @author Alexey Dubrov
  *
  *         Equalizer configuration.
  *
  */

class EqConf extends FilterConf {

  @BeanProperty var bandsCount: Int = 0;
  @BeanProperty var bands = new util.HashMap[String, Float]();

  override def getName: String = "eq";
}
