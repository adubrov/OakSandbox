package com.bluevinyl.config

import scala.beans.BeanProperty

/**
  * @author Alexey Dubrov
  *
  *         Main BlueVinyl configuration.
  */

class BlueConfig {

  @BeanProperty var input: InSoundConf = null;
  @BeanProperty var output: OutSoundConf = null;

  override def toString = s"BlueConfig($input, $output)"
}