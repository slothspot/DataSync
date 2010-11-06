package name.dmitrym.datasync

import java.io.File

object Datasync {
    def syncWithDefaultCfg {
      println("Using default cfg: sync.cfg")
    }

    def main( args : Array[String] ) {
      println("Hello from DataSync")
      args.length match {
          case 0 =>
            syncWithDefaultCfg
          case _ if (new File(args(0))).exists =>
            println("Using custom cfg: " + args(0))
          case _ =>
            syncWithDefaultCfg
        }
    }
}

// vim: set ts=4 sw=4 et:
