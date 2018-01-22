package com.bluevinyl.config

import java.util

import com.bluevinyl.config.filters.FilterConf
import com.bluevinyl.config.sources.SourceConf

import scala.beans.BeanProperty

/**
  * @author Alexey Dubrov
  *
  *         Input source configuration.
  */

class InSoundConf {

  @BeanProperty var bufferSize: Int = 65536;
  @BeanProperty var mixer: String = "default";
  @BeanProperty var source: SourceConf = null;
  @BeanProperty var filters = new util.ArrayList[FilterConf]();

  override def toString = s"InSoundConf($bufferSize, $mixer, $source, $filters)"
}
