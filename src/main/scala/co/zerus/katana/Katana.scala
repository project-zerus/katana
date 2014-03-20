package co.zerus.katana

import java.io.File
import scala.collection.mutable.ListBuffer
import scala.io.Source
import com.twitter.util.Eval

/**
 * @author huahang
 */
class Katana(val name: String,
             val version: String,
             val release: String,
             val description: String,
             val summary: String,
             val files: Map[String, (String, String)])

object Katana {
  val katanas: ListBuffer[Katana] = ListBuffer()

  def kt_package(name: String,
                 version: String = "0.0.1",
                 release: String = "1",
                 description: String = "(description)",
                 summary: String = "(summary)",
                 files: Map[String, (String, String)]) = {
    katanas.append(new Katana(name, version, release, description, summary, files))
    katanas.toList
  }

  def parse(katanaFile: File) = {
    val katanaFileHeader = "import co.zerus.katana.Katana.kt_package\n"
    val katanaFileContent = Source.fromFile(katanaFile).getLines().reduce((l1, l2) => l1 + "\n" + l2)
    val katanaSource = katanaFileHeader + katanaFileContent
    val eval = new Eval
    eval[List[Katana]](katanaSource)
  }
}
