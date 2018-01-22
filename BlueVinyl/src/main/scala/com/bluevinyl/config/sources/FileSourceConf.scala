package com.bluevinyl.config.sources

import scala.beans.BeanProperty

/**
  * @author Alexey Dubrov
  *
  *         File source configuration.
  *
  */

class FileSourceConf extends SourceConf{

  @BeanProperty var filePath: String = "";

  override def getName: String = "FileSource";
}
