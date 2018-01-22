package com.bluevinyl.config

import java.util

import scala.beans.BeanProperty

/**
  * @author Alexey Dubrov
  *
  *         Output sound source configuration.
  *
  */

class OutSoundConf {

  @BeanProperty var bufferSize: Int = 65536;
  @BeanProperty var control = new util.HashMap[String, Float]();

  override def toString = s"OutSoundConf($bufferSize, $control)"
}
