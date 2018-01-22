package com.bluevinyl.core

import javax.sound.sampled._

import org.slf4j.LoggerFactory

import scala.annotation.tailrec

/**
  * @author Alexey Dubrov
  *
  *         Sound mixer class.
  *         Mixer contains all streams, filters and sound's source/target.
  */

class SoundMixer {

  val LOG = LoggerFactory.getLogger(this.getClass)

  var mixerInfo: Option[Mixer.Info] = None
  var sourceStream: Option[AudioInputStream] = None
  var filters = List[AudioInputStream]()
  var lastFilter: AudioInputStream = null
  var outputLine: Option[SourceDataLine] = None

  def prepareDataLine(config: Map[String, Float]) = {
    config.map(elm => configureByName(elm._1, elm._2.asInstanceOf[java.lang.Double].toFloat));
  }

  def play() = {
    val audioFormat = sourceStream.map(_.getFormat).get;
    val bufferSize = (audioFormat.getSampleRate * audioFormat.getFrameSize).toInt

    outputLine.map(dataLine => {
      dataLine.open(audioFormat)
      dataLine.start()

      val buffer = new Array[Byte](bufferSize)

      try
        transferStream(lastFilter, dataLine, buffer)
      catch {
        case e: Exception => LOG.error("Failed to play audio due to error", e)
      }

      LOG info "Play audio started"
    })
  }

  @tailrec
  private def transferStream(src: AudioInputStream, rc: SourceDataLine, buffer: Array[Byte]):Boolean = {
    val bytesRead = src.read(buffer, 0, buffer.length)
    if (bytesRead < 0)
      true
    else {
      rc.write(buffer, 0, bytesRead)
      transferStream(src, rc, buffer)
    }
  }

  protected def configureByName(name: String, prm: Float) = name match {
    case "gain" => configureGain(prm)
    case e => LOG error s"No such float control configuration: ${e}"
  }

  /**
    * Configure gain.
    * @param prm float parameter
    * @return nothing actually
    */
  private def configureGain(prm: Float) = {
    outputLine match {
      case dataLine: DataLine => {
        val volume = dataLine.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]

        if (dataLine isControlSupported FloatControl.Type.MASTER_GAIN) {
          volume setValue (volume.getMaximum() - volume.getMinimum()) * 0.7f + volume.getMinimum();

          LOG info "Gain was successfully configured";
        }
      }
      case _ => None
    }
  }

}
