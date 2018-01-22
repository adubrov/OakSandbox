package com.bluevinyl.factories.filters

import javax.sound.sampled.AudioInputStream

import com.bluevinyl.config.filters.FilterConf
import org.slf4j.LoggerFactory

/**
  * @author Alexey Dubrov
  *
  *         Filter abstract trait.
  *
  */

trait FilterFactory[+T <: AudioInputStream, +S <: FilterConf] {
  val LOG = LoggerFactory.getLogger(this.getClass)

  def createFilter[U >: S](inputStream: AudioInputStream, conf: U): Option[T];
}
