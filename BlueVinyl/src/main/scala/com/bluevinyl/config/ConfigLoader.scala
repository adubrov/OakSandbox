package com.bluevinyl.config

import java.io.FileInputStream

import com.bluevinyl.config.filters.{AmpConf, EqConf}
import com.bluevinyl.config.sources.FileSourceConf
import org.yaml.snakeyaml.{TypeDescription, Yaml}
import org.yaml.snakeyaml.constructor.Constructor

/**
  * @author Alexey Dubrov
  *
  *         Loads configuration from yaml file.
  *
  */

object ConfigLoader {

  //configure YAML
  val constructor = new Constructor(classOf[BlueConfig]);
  constructor.addTypeDescription(new TypeDescription(classOf[InSoundConf], "!InSoundConf"))
  constructor.addTypeDescription(new TypeDescription(classOf[OutSoundConf], "!OutSoundConf"))
  constructor.addTypeDescription(new TypeDescription(classOf[FileSourceConf], "!FileSourceConf"))
  constructor.addTypeDescription(new TypeDescription(classOf[AmpConf], "!AmpConf"))
  constructor.addTypeDescription(new TypeDescription(classOf[EqConf], "!EqConf"))

  val yaml: Yaml = new Yaml(constructor)

  def loadConfig(filePath: String) = yaml.loadAs(new FileInputStream(filePath), classOf[BlueConfig])

}
