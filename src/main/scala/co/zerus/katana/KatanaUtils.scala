package co.zerus.katana

import org.freecompany.redline.Builder
import org.freecompany.redline.header.{RpmType, Os, Architecture}
import java.net.InetAddress
import java.io.{FileInputStream, BufferedOutputStream, FileOutputStream, File}
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}
import org.apache.commons.compress.utils.IOUtils
import com.google.common.io.Closer
import org.apache.commons.lang3.StringUtils

/**
 * @author huahang
 */
object KatanaUtils {

  def buildRpm(katanaContext: KatanaContext, katana: Katana) {
    val builder = new Builder
    builder.setPackage(katana.name, katana.version, katana.release)
    builder.setPlatform(Architecture.X86_64, Os.LINUX)
    builder.setBuildHost(InetAddress.getLocalHost.getHostName)
    builder.setType(RpmType.BINARY)
    builder.setDescription(katana.description)
    builder.setSummary(katana.summary)
    val prefix = "/opt/" + katana.name
    builder.setPrefixes(prefix)
    katana.files.foreach(f => {
      val sourceFile =
        if (f._1.startsWith("/")) {
          new File(katanaContext.bladeRoot, f._1.substring(1))
        } else {
          new File(katanaContext.workingDir, f._1)
        }
      val fileName = f._2._1
      val fileMode = KatanaUtils.parseFileMode(f._2._2)
      if (fileMode.isEmpty) {
        throw new Exception("File mode is invalid! " + fileMode)
      }
      builder.addFile(prefix + "/" + fileName, sourceFile, fileMode.get)
    })
    builder.build(katanaContext.workingDir)
  }

  def buildTarball(katanaContext: KatanaContext, katana: Katana) {
    val closer = Closer.create
    try {
      val tarballPath = katana.name + ".tar.gz"
      val fileOutputStream = closer.register(new FileOutputStream(new File(tarballPath)))
      val bufferedOutputStream = closer.register(new BufferedOutputStream(fileOutputStream))
      val gzOutputStream = closer.register(new GzipCompressorOutputStream(bufferedOutputStream))
      val tarOutputStream = closer.register(new TarArchiveOutputStream(gzOutputStream))
      katana.files.foreach(f => {
        val sourceFile =
          if (f._1.startsWith("/")) {
            new File(katanaContext.bladeRoot, f._1.substring(1))
          } else {
            new File(katanaContext.workingDir, f._1)
          }
        val fileName = f._2._1
        val fileMode = KatanaUtils.parseFileMode(f._2._2)
        if (fileMode.isEmpty) {
          throw new Exception("File mode is invalid! " + fileMode)
        }
        val tarEntry = new TarArchiveEntry(sourceFile, fileName)
        tarEntry.setMode(fileMode.get)
        tarOutputStream.putArchiveEntry(tarEntry)
        IOUtils.copy(new FileInputStream(sourceFile), tarOutputStream)
        tarOutputStream.closeArchiveEntry
      })
      tarOutputStream.finish
    } catch {
      case t: Throwable => closer.rethrow(t)
    } finally {
      closer.close
    }
  }

  def parseFileMode(mode: String): Option[Integer] = {
    if (null != mode && (mode matches "[0-7]{3}")) {
      Some(Integer.parseInt(mode, 8))
    } else {
      None
    }
  }

  def isValidPackageName(name: String) =
    StringUtils.isAlphanumeric(name) && StringUtils.isNotBlank(name)
}
