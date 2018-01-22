package com.bluevinyl.factories

import javax.sound.sampled.{AudioFormat, AudioInputStream, AudioSystem, Mixer}

import com.bluevinyl.config.filters.FilterConf
import com.bluevinyl.config.sources.SourceConf
import com.bluevinyl.config.{BlueConfig, InSoundConf, OutSoundConf}
import com.bluevinyl.core.{SoundMixer, SoundMixerBuilder}
import com.bluevinyl.exceptions.BlueVinylException
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * @author Alexey Dubrov
  *
  *         Sound mixer factory.
  *
  */

object SoundMixerFactory {

  val LOG = LoggerFactory.getLogger(SoundMixerFactory.getClass);

  def initMixer(blueConfig: BlueConfig): SoundMixer = {
    val builder = SoundMixerBuilder.instance

    // input
    val sm = configureInput(blueConfig.input, builder)

    // configure output if input successfully configured
    sm._1 match {
      case Some(src) =>
        sm._2 match {
          case Some(mixer) => configureOutput(blueConfig.output, src, mixer, builder)
          case None => throw new BlueVinylException("Mixer was not configured")
        }
      case None => throw new BlueVinylException("Source was not configured")
    }

    builder.build
  }

  /**
    * Configure input path in mixer.
    * @param inSoundConf input sound configuration
    */
  protected def configureInput(inSoundConf: InSoundConf, builder: SoundMixerBuilder) = {
    LOG info "Configure sound mixer input"

    // audio mixer
    LOG info s"Find mixer with name: ${inSoundConf.mixer}"
    val mixer = findMixer(inSoundConf.mixer)
    builder.setMixer(mixer)

    // sound source
    val source = findSource(inSoundConf.bufferSize, inSoundConf.source)
    builder.setSource(source)

    // add filters
    val crtFilter = createFilter(builder)_;
    inSoundConf.filters.asScala.toList.foldLeft(source)(crtFilter)

    (source, mixer)
  }

  /**
    * Configure sound mixer output
    * @param outSoundConf configuration
    * @param source input source
    * @param mixer audio mixer
    * @param builder sound mixer builder
    */
  protected def configureOutput(outSoundConf: OutSoundConf, source: AudioInputStream, mixer: Mixer.Info,
                                builder: SoundMixerBuilder) = {
    LOG info "Configure sound mixer output"

    LOG info "Configure data line"
    val dataLine = getDataLineFromMixer(source.getFormat, mixer)
    builder.setDataLine(dataLine);

    LOG info "Configure data line controls"
    builder.setConfig(outSoundConf.control.asScala.toMap)
  }

  /**
    * Find mixer by name.
    * @param name mixer name
    * @return mifxer info
    */
  protected def findMixer(name: String): Option[Mixer.Info] = {
    AudioSystem.getMixerInfo.find(_.getName().toLowerCase.contains(name))
  }

  /**
    * Getting sound source.
    * @param bufferSize buffer size
    * @param source source configuration
    * @return audio input stream
    */
  protected def findSource(bufferSize: Int, source: SourceConf) =
    FactoryHolder.getSource(source.getName)
      .flatMap(_.createSource(bufferSize, source))

  /**
    * Create filter for adding in mixer.
    * @param inputStreamOpt input stream
    * @param filterConf filter configuration
    * @param builder sound mixer builder
    * @return audio stream
    */
  protected def createFilter(builder: SoundMixerBuilder)
                            (inputStreamOpt: Option[AudioInputStream], filterConf: FilterConf) = {
    val filter = inputStreamOpt.flatMap(inputStream =>
      FactoryHolder.getFilter(filterConf.getName)
        .flatMap(_.createFilter(inputStream, filterConf)))

    filter.map(builder.addFilter)
    filter
  }

  /**
    * Get dataLine from mixer.
    * @param audioFormat audio format
    * @param mixer mixer
    * @return
    */
  protected def getDataLineFromMixer(audioFormat: AudioFormat, mixer: Mixer.Info) =
    try {
      Some(AudioSystem.getSourceDataLine(audioFormat, mixer))
    } catch {
      case e: Exception => {
        LOG error "Failed to configure data line"

        None
      }
    }


}
