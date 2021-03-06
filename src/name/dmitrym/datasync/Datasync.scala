/**
 * @author Dmitry Melnichenko
 */
package name.dmitrym.datasync

import name.dmitrym.utils.MtabWrapper
import java.io.{File, FileReader, LineNumberReader}
import name.dmitrym.syncspec.{Worker, SyncSpec}

/**
 * DataSync main object, application entry point located here
 */
object Datasync {
  /**
   * used to initialize sync with default cfg
   */
  def syncWithDefaultCfg(srcRoot: String, dstRoot: String ) {
    println("Using default cfg: sync.cfg")
    val pwd = System.getenv("PWD")
    println("Current working directory: " + pwd)

    val mpl = MtabWrapper.listMountPoints
    val mp = mpl.filter(p => pwd.startsWith(p)).sortWith((s1, s2) => s1.length > s2.length).head

    def lookupCfg(path: String): String = {
      val cfgname = if (path != null) {
        val p = path + File.separator + "sync.cfg"
        val f = new File(p)
        if (!f.exists && path != mp) {
          lookupCfg(f.getCanonicalFile.getParentFile.getParent)
        } else if (f.canRead) {
          f.getCanonicalPath
        } else ""
      } else ""
      if (cfgname.isEmpty) {
        val hc = new File(System.getenv("HOME") + File.separator + "sync.cfg")
        if (hc.exists && hc.canRead)
          hc.getCanonicalPath
        else
          cfgname
      } else {
        cfgname
      }
    }

    val cfg = lookupCfg(pwd)
    if (!cfg.isEmpty) {
      println("Trying to start sync with " + cfg + " for " + pwd)
      syncWithCfg(cfg, srcRoot, dstRoot)
    } else {
      println("Suitable config not found")
    }
  }

  /**
   * used to initialize sync with specified config
   * @param cfgname specified config file name
   */
  def syncWithNamedCfg(cfgname: String, srcRoot: String, dstRoot: String) {
    println("Using cfg from params: " + cfgname)
    val f = new File(cfgname)
    if (f.exists && f.canRead) {
      syncWithCfg(f.getCanonicalPath, srcRoot, dstRoot)
    } else {
      println("Specified config file not found or not readable, falling back to defautlt config resolution routine")
      syncWithDefaultCfg(srcRoot, dstRoot)
    }
  }

  /**
   * starts sync process with specified config
   * @param cfgname specified full path to config file
   */
  def syncWithCfg(cfgname: String, srcRoot: String, dstRoot: String) {
    println("Using cfg: " + cfgname + "[srcRoot: " + srcRoot + "; dstRoot: " + dstRoot + "]")
    val lnr = new LineNumberReader(new FileReader(cfgname))
    var str = lnr.readLine
    val ss = new SyncSpec
    if(!srcRoot.isEmpty) Worker.setSrcRoot(srcRoot)
    if(!dstRoot.isEmpty) Worker.setDstRoot(dstRoot)
    while (str != null) {
      println(lnr.getLineNumber + ": " + str)
      ss.doMatchOperation(str)
      str = lnr.readLine
    }
  }

  /**
   * process help command-line option
   */
  def help() {
    println("DataSync - data synchronizer with configs written in SyncSpec")
    println("\t-help\t\tprints this help message")
    println("\t-version\tprints program version")
    println("\t-cfg [filename]\tspecifies config file to use")
    println("\t-src-root [dirname]\tspecifies source root dir for syncing")
    println("\t-dst-root [dirname]\tspecifies destination root dir for syncing")
  }

  /**
   * process version command-line option
   */
  def version() {
    println("DataSync version 0.0.1 -- Copyright 2010, Dmitry Melnichenko")
  }

  /**
   * process command line arguments
   * @param args arguments can be: <i>-help</i>, <i>-version</i>, <i>-cfs [filename]</i>
   */
  def processCommandLine(args: Array[String]) {
    if (args.contains("-help")) {
      help()
    } else if (args.contains("-version")) {
      version()
    } else {
      var idx = args.indexOf("-src-root")
      val srcRoot = if(idx > -1 && args.length > (idx + 1)) args(idx + 1) else ""
      idx = args.indexOf("-dst-root")
      val dstRoot = if(idx > -1 && args.length > (idx + 1)) args(idx + 1) else ""
      idx = args.indexOf("-cfg")
      if (idx > -1 && args.length > (idx + 1)) {
        syncWithNamedCfg(args(idx + 1), srcRoot, dstRoot)
      } else {
        syncWithDefaultCfg(srcRoot, dstRoot)
      }
    }
  }

  /**
   * Application entry point
   * @param args command-line arguments
   */
  def main(args: Array[String]) {
    processCommandLine(args)
  }
}

// vim: set ts=4 sw=4 et:
