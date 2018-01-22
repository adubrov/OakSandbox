package com.bluevinyl.config.filters

import scala.beans.BeanProperty

/**
  * @author Alexey Dubrov
  *
  *         Amplifier configuration.
  *
  */

class AmpConf extends FilterConf {

  @BeanProperty var gain: Float = 0;

  override def getName: String = "amp";
}
