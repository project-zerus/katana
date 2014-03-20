package co.zerus.katana.test

import org.specs2.mutable._
import co.zerus.katana.KatanaUtils

/**
 * @author huahang
 */
class KatanaTest extends SpecificationWithJUnit {

  "KatanaUtils" should {

    "file mode parse test" in {
      KatanaUtils.parseFileMode("000").get shouldEqual 0
      KatanaUtils.parseFileMode("777").get shouldEqual 511
      KatanaUtils.parseFileMode("008") shouldEqual None
      KatanaUtils.parseFileMode("00") shouldEqual None
      KatanaUtils.parseFileMode("0000").isEmpty shouldEqual true
    }

  }

}
