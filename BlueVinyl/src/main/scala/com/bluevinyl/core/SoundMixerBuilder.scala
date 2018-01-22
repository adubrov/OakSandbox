package com.bluevinyl.core

import javax.sound.sampled.{AudioInputStream, DataLine, Mixer, SourceDataLine}

import scala.collection.mutable.ListBuffer

/**
  * @author Alexey Dubrov
  *
  *         SoundMixer builder.
  *
  */

class SoundMixerBuilder private {
  var soundMixer = new SoundMixer()
  val filterLstBuilder = ListBuffer.empty[AudioInputStream]

  def setMixer(mixerInfo: Option[Mixer.Info]) = {
    soundMixer.mixerInfo = mixerInfo
    this
  }

  def setSource(sourceStream: Option[AudioInputStream]) = {
    soundMixer.sourceStream = sourceStream
    this
  }

  def addFilter(filterStream: AudioInputStream) = {
    filterLstBuilder += filterStream
    this
  }

  def setDataLine(dataLine: Option[SourceDataLine]) = {
    soundMixer.outputLine = dataLine
    this
  }

  def setConfig(config: Map[String, Float]) = {
    soundMixer.prepareDataLine(config)
    this
  }

  def build = {
    soundMixer.lastFilter = filterLstBuilder.last
    soundMixer.filters = filterLstBuilder.toList
    soundMixer
  }
}

object SoundMixerBuilder {
  def instance = new SoundMixerBuilder()
}