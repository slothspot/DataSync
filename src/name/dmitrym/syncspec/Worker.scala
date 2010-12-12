package name.dmitrym.syncspec

import java.io.{File, FileInputStream, FileOutputStream}
import scala.collection.mutable.ArrayBuilder

/**
 * Worker executes SyncSpec operations
 * @author Dmitry Melnichenko
 */
object Worker {

  private class SyncEntry(parent: String, name: String, size: Long, lastModified: Long) {
    def this(parent: File, f: File) = this (parent.getCanonicalPath, f.getCanonicalPath.substring(parent.getCanonicalPath.length + 1), f.length, f.lastModified)

    def getParent = parent

    def getName = name

    def getSize = size

    def getLastModified = lastModified

    override def equals(o: Any) = {
      try {
        val t = o.asInstanceOf[SyncEntry]
        if (!name.equals(t.getName)) {
          false
        }
        else {
          if (lastModified < t.getLastModified) {
            false
          }
          else true
        }
      }
      catch {
        case e: ClassCastException => println(e.getMessage); false
      }
    }
  }

  /**
   * Return directory listing as Array[SyncEntry]
   * @param dir directory to generate listing from
   */
  private def listDir(dir: File): Array[SyncEntry] = listDir(dir, dir)

  /**
   * Return directory listing as Array[SyncEntry]
   * @param parent parent to pass to SyncEntry ctor
   * @param dir directory to generate listing from
   */
  private def listDir(parent: File, dir: File): Array[SyncEntry] = {
    var filesList = new ArrayBuilder.ofRef[SyncEntry]
    val lst = dir.listFiles
    lst.foreach(f =>
      if (!f.isDirectory) filesList += new SyncEntry(parent, f)
      else listDir(parent, f).foreach(f => filesList += f)
    )
    filesList.result.sortWith(sortByNameAsc)
  }

  @deprecated("Deprecated by SyncEntry overloaded constructor")
  private def file2SyncEntry(parent: File, f: File) = new SyncEntry(parent.getCanonicalPath, f.getCanonicalPath.substring(parent.getCanonicalPath.length + 1), f.length, f.lastModified)

  private def sortByNameAsc(a: SyncEntry, b: SyncEntry) = a.getName.compareToIgnoreCase(b.getName) < 0

  private def sortByNameDsc(a: SyncEntry, b: SyncEntry) = a.getName.compareToIgnoreCase(b.getName) > 0

  private def sortBySizeAsc(a: SyncEntry, b: SyncEntry) = a.getSize < b.getSize

  private def sortBySizeDsc(a: SyncEntry, b: SyncEntry) = a.getSize > b.getSize

  private def mergeForCopy(srcLst: Array[SyncEntry], dstLst: Array[SyncEntry]): Array[SyncEntry] = {
    srcLst.filter(e => !dstLst.contains(e))
  }

  /**
   * Copy content of <i>from</i> path to <i>to</i> path
   * @param from path to source folder
   * @param to path to destination folder
   */
  def copy(from: String, to: String): Boolean = {
    println("Simple copy of \"" + from + "\" to \"" + to + "\"")
    copy(from, to, false)
  }

  /**
   * Copy content of <i>from</i> path to <i>to</i> path and delete the source if needed
   * @param from path to source folder
   * @param to path to destination folder
   * @param deleteSource delete the source file or no
   */
  private def copy(from: String, to: String, deleteSource: Boolean): Boolean = {
    val srcF = new File(from)
    if (!srcF.exists) {
      false
    }
    else {
      val dstF = new File(to)
      if (!dstF.exists && !dstF.mkdirs) {
        false
      }
      else {
        val srcLst = listDir(srcF)
        val dstLst = listDir(dstF)
        val resLst = mergeForCopy(srcLst, dstLst)
        try {
          resLst.foreach(e => {
            val dn = to + File.separator + e.getName
            val sn = from + File.separator + e.getName
            println("Target file name: " + dn)
            println("Source file name: " + sn)
            val dF = new File(dn)
            val sF = new File(sn)
            dF.getParentFile.mkdirs
            dF.createNewFile
            val fis = new FileInputStream(sF)
            val fos = new FileOutputStream(dF)
            // FIXME: current implementation works only for relatively small files due to in-memory buffer allocation for whole source file
            val buf = new Array[Byte](fis.available)
            fis.read(buf)
            fos.write(buf)
            fis.close
            fos.close
            dF.setLastModified(e.getLastModified)
            if (deleteSource) {
              sF.delete
              var p = sF.getParentFile
              while (p != null) {
                if (p.list.length == 0) p.delete
                p = p.getParentFile
              }
            }
            println(e.getName + "; " + e.getParent + "; " + e.getSize + "; " + e.getLastModified)
          })
          true
        } catch {
          case e: Exception => println(e.getMessage); false
        }
      }
    }
  }

  /**
   * Move content of <i>from</i> path to <i>to</i> path
   * @param from path to source folder
   * @param to path to destination folder
   */
  def move(from: String, to: String): Boolean = {
    println("Simple move of \"" + from + "\" to \"" + to + "\"")
    copy(from, to, true)
  }

  /**
   * Sync content of <i>from</i> path with <i>to</i> path; equivalent to <b>copy(from, to) && copy(to, from)</b>
   * @param from path to source folder
   * @param to path to destination folder
   */
  def sync(from: String, to: String): Boolean = {
    println("Simple sync of \"" + from + "\" to \"" + to + "\"")
    copy(from, to) && copy(to, from)
  }

  /**
   * Copy content of <i>from</i> path to <i>to</i> path and delete the source if needed
   * @param from path to source folder
   * @param to path to destination folder
   * @param p path to file with rename rules
   */
  def copy(from: String, to: String, p: String): Boolean = {
    println("Simple copy of \"" + from + "\" to \"" + to + "\" with rules from \"" + p + "\"")
    copy(from, to, p, false)
  }

  /**
   * Copy content of <i>from</i> path to <i>to</i> path and delete the source if needed
   * @param from path to source folder
   * @param to path to destination folder
   * @param deleteSource delete the source file or no
   * @param p path to file with rename rules
   */
  private def copy(from: String, to: String, p: String, deleteSource: Boolean): Boolean = {
    false
  }

  /**
   * Move content of <i>from</i> path to <i>to</i> path
   * @param from path to source folder
   * @param to path to destination folder
   * @param p path to file with rename rules
   */
  def move(from: String, to: String, p: String): Boolean = {
    println("Simple move of \"" + from + "\" to \"" + to + "\" with rules from \"" + p + "\"")
    copy(from, to, p, true)
  }

  /**
   * Sync content of <i>from</i> path with <i>to</i> path; equivalent to <b>copy(from, to) && copy(to, from)</b>
   * @param from path to source folder
   * @param to path to destination folder
   * @param p path to file with rename rules
   */
  def sync(from: String, to: String, p: String): Boolean = {
    println("Simple sync of \"" + from + "\" to \"" + to + "\" with rules from \"" + p + "\"")
    copy(from, to, p) && copy(to, from, p)
  }
}
