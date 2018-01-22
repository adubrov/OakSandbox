package com.bluevinyl.main

import com.bluevinyl.config._
import com.bluevinyl.config.filters.FilterConf
import com.bluevinyl.factories.SoundMixerFactory

import scala.collection.JavaConverters._

/**
  * @author Alexey Dubrov
  *
  *         BlueVinyl main class.
  *
  */

object BlueVinyl extends App {

  val blueConfig = ConfigLoader.loadConfig("/Work/Workspaces/BlueVinyl/src/main/resources/config.yml");

  val soundMixer = SoundMixerFactory.initMixer(blueConfig)
  soundMixer.play()

}
