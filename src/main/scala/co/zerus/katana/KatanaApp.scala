package im.chic.devtools

import java.io.File
import co.zerus.katana.{KatanaUtils, KatanaContext, Katana}
import org.apache.commons.lang3.StringUtils

/**
 * @author huahang
 */
object KatanaApp extends App {

  case class Config(dir: File = new File("."),
                    katana: String = "KATANA")

  val parser = new scopt.OptionParser[Config]("katana") {
    head("katana", "0.1-SNAPSHOT")
    opt[String]('k', "katana") action {
      (x, c) => c.copy(katana = x)
    } text ("The KATANA file that describes the packaging process.")
    arg[File]("<dir>") minOccurs (0) maxOccurs (1) action {
      (x, c) => c.copy(dir = x)
    } text ("The dir in which the packaging will be processed.")
    help("help") text ("prints this usage text")
  }

  parser.parse(args, Config()) map {
    config => {
      val workingDir = Some(config.dir.getAbsoluteFile)
      if (workingDir.get.exists && workingDir.get.isDirectory) {}
      println("Working dir: " + workingDir.get.getAbsolutePath)

      val bladeRoot = findBladeRoot(workingDir.get)
      if (bladeRoot.isEmpty) {
        throw new Exception("No BLADE_ROOT found!")
      }
      println("BLADE_ROOT: " + bladeRoot.get.getAbsolutePath)

      val katanaFile = findKatanaFile(workingDir.get, config.katana)
      if (katanaFile.isEmpty) {
        throw new Exception("No KATANA file found!")
      }
      println("KATANA file: " + config.katana)

      val katanaContext = new KatanaContext(bladeRoot.get, workingDir.get, config.katana)

      Katana.parse(katanaFile.get).foreach(katana => {
        if (!StringUtils.isAlphanumeric(katana.name)) {
          throw new Exception("Package name must be alpha numeric!")
        }
        KatanaUtils.buildRpm(katanaContext, katana)
        KatanaUtils.buildTarball(katanaContext, katana)
      })
    }
  }

  def findKatanaFile(dir: File, katana: String): Option[File] = {
    val katanaFile = new File(dir.getAbsoluteFile, katana)
    if (katanaFile.exists) {
      Some(katanaFile)
    } else {
      None
    }
  }

  def findBladeRoot(dir: File): Option[File] = {
    if (null == dir) {
      None
    } else {
      val bladeRootFile = new File(dir.getAbsoluteFile, "BLADE_ROOT")
      val parentFile = dir.getAbsoluteFile.getParentFile
      if (bladeRootFile.exists && bladeRootFile.isFile) {
        Some(dir.getAbsoluteFile)
      } else if (null != parentFile && parentFile.exists()) {
        findBladeRoot(parentFile.getAbsoluteFile)
      } else {
        None
      }
    }
  }

}
