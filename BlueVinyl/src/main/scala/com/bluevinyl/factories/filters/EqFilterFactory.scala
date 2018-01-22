package com.bluevinyl.factories.filters

import javax.sound.sampled.AudioInputStream

import com.bluevinyl.config.filters.{EqConf, FilterConf}
import davaguine.jeq.spi.EqualizerInputStream

import scala.collection.JavaConverters._

/**
  * @author Alexey Dubrov
  *
  *         Equalizer filter factory.
  *
  */

class EqFilterFactory extends FilterFactory[EqualizerInputStream, EqConf] {

  override def createFilter[U >: EqConf](inputStream: AudioInputStream, conf: U): Option[EqualizerInputStream] =
    try {
      val eqConf = conf.asInstanceOf[EqConf]
      val eq = new EqualizerInputStream(inputStream, eqConf.bandsCount)
      configureBands(eq, eqConf.bands.asScala.toMap)

      Some(eq)
    } catch {
      case e: Exception => {
        LOG.error("Failed to configure equalizer due to error:", e)

        None
      }
    }

  /**
    * Configures bands controls
    * @param eq equalizer stream
    * @param bandsConf map of bands configuration
    */
  protected def configureBands(eq: EqualizerInputStream, bandsConf: Map[String, Float]) = {
    bandsConf.filter(_._1.startsWith("band")).foreach(conf => {
      val band = conf._1.substring(4, conf._1.length-1).toInt
      val channel = if (conf._1.charAt(conf._1.length-1).equals('l')) 0 else 1

      eq.getControls.setBandValue(band, channel, conf._2.asInstanceOf[java.lang.Double].toFloat)
    })
  }

}
