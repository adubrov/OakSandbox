package com.bluevinyl.factories

import javax.sound.sampled.AudioInputStream

import com.bluevinyl.config.filters.FilterConf
import com.bluevinyl.config.sources.SourceConf
import com.bluevinyl.factories.filters.{EqFilterFactory, FilterFactory}
import com.bluevinyl.factories.sources.{FileSourceFactory, SourceFactory}

/**
  * @author Alexey Dubrov
  *
  *         Factories holder.
  *
  */

object FactoryHolder {

  val filterFactories: Map[String, FilterFactory[AudioInputStream, FilterConf]] =
    Map("eq" -> new EqFilterFactory)

  val sourceFactories: Map[String, SourceFactory[AudioInputStream, SourceConf]] =
    Map("FileSource" -> new FileSourceFactory())

  def getSource(sourceName: String) = sourceFactories.get(sourceName)

  def getFilter(filterName: String) = filterFactories.get(filterName)

}
