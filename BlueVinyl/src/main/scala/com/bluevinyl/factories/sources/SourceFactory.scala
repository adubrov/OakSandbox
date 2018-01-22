package com.bluevinyl.factories.sources

import javax.sound.sampled.{AudioFormat, AudioInputStream, AudioSystem}

import com.bluevinyl.config.sources.SourceConf
import com.typesafe.scalalogging.Logger
import org.slf4j.{LoggerFactory}

/**
  * @author Alexey Dubrov
  *
  *         Sources factory.
  *
  */

trait SourceFactory[+T <: AudioInputStream, +S <: SourceConf] {

  val LOG = Logger(LoggerFactory.getLogger(this.getClass))

  def createSource[U >: S](bufferSize: Int, conf: U): Option[T];

  protected def convertCompressedAudio(audioInputStream: AudioInputStream): AudioInputStream = {
    val audioFormat: AudioFormat = audioInputStream.getFormat

    // Convert compressed audio data to uncompressed PCM format.
    if (audioFormat.getEncoding ne AudioFormat.Encoding.PCM_SIGNED) {
      val newFormat: AudioFormat = new AudioFormat(audioFormat.getSampleRate,
        audioFormat.getSampleSizeInBits, audioFormat.getChannels, true, false)

      LOG.info("Converting audio format to {}", newFormat)

      AudioSystem.getAudioInputStream(newFormat, audioInputStream)
    } else
      audioInputStream
  }

}
