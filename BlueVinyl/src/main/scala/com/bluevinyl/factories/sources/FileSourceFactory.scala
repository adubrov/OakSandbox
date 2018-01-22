package com.bluevinyl.factories.sources

import java.io.{BufferedInputStream, FileInputStream, IOException}
import javax.sound.sampled.{AudioInputStream, AudioSystem, UnsupportedAudioFileException}

import com.bluevinyl.config.sources.{FileSourceConf}

/**
  * @author Alexey Dubrov
  *
  *         File source factory.
  *
  */

class FileSourceFactory extends SourceFactory[AudioInputStream, FileSourceConf] {

  override def createSource[U >: FileSourceConf](bufferSize: Int, conf: U): Option[AudioInputStream] =
    try {
      val flConf = conf.asInstanceOf[FileSourceConf]
      val audioInputStream = openBufferedStream(bufferSize, flConf.filePath)
      val result = convertCompressedAudio(audioInputStream)

      LOG.info("Opened input stream from file path: '{}'", flConf.getFilePath)

      Some(result)
    } catch {
      case e: Exception => {
        LOG.error("Failed to open input stream from file path: '{}', due to error", conf.toString, e)

        None
      }
    }

  @throws[IOException]
  @throws[UnsupportedAudioFileException]
  protected def openBufferedStream(bufferSize: Int, filePath: String): AudioInputStream = {
    val fileInputStream: FileInputStream = new FileInputStream(filePath)
    val bufferedInputStream: BufferedInputStream = new BufferedInputStream(fileInputStream, bufferSize)

    AudioSystem.getAudioInputStream(bufferedInputStream)
  }
}
