package name.dmitrym.utils

import scala.collection.mutable.LinearSeq
import java.io.{BufferedReader, File, FileReader}

/**
 * MtabWrapper used as wrapper over /etc/fstab and /etc/mtab to obtain information about mount points
 * @author Dmitry Melnichenko
 */
object MtabWrapper {
  /**
   * Lists currently mounted mount points
   */
  def listMountPoints = {
    val f = new File("/etc/mtab")
    var ls = LinearSeq.empty[String]
    if (f.exists && f.canRead) {
      val br = new BufferedReader(new FileReader(f))

      def loop(br: BufferedReader) {
        val line = br.readLine
        if (line != null) {
          if (line.startsWith("/dev/")) {
            val parts = line.split(" ")
            if (parts.length > 1) ls = ls :+ parts(1)
          }
          loop(br)
        }
      }

      loop(br)
    }
    ls
  }
}
