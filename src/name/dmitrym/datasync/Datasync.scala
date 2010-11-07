/**
 * @author Dmitry Melnichenko
 */
package name.dmitrym.datasync

import name.dmitrym.utils.MtabWrapper
import java.io.File

/**
 * DataSync main object, application entry point located here
 */
object Datasync {
    /**
     * used to initialize sync with default cfg
     */
    def syncWithDefaultCfg {
      println( "Using default cfg: sync.cfg" )
      val pwd = System.getenv( "PWD" )
      println( "Current working directory: " + pwd )

      val mp = MtabWrapper.listMountPoints
      mp.map( s => println( "MountPoint: " + s ) )

      def debugGoUp ( path : String ) {
          println( "debugGoUp[dir]: " + path )
          if( path != null ) {
              val p = path + File.separator + "sync.cfg"
              val f = new File( p )
              println( "debugGoUp[file]: " + f.getCanonicalPath )
              if ( !f.exists ) debugGoUp( f.getCanonicalFile.getParentFile.getParent )
          }
      }

      debugGoUp( pwd )
    }

    /**
     * used to initialize sync with specified config
     * @param cfgname specified config file name
     */
    def syncWithNamedCfg( cfgname : String ) {
        println( "Using cfg from params: " + cfgname )
        val f = new File( cfgname )
        if ( f.exists && f.canRead ) {
            syncWithCfg( f.getCanonicalPath )
        } else {
            println( "Specified config file not found or not readable, falling back to defautlt config resolution routine" )
            syncWithDefaultCfg
        }
    }

    /**
     * starts sync process with specified config
     * @param cfgname specified full path to config file
     */
    def syncWithCfg( cfgname : String ) {
      println( "Using cfg: " + cfgname )
    }

    /**
     * process help command-line option
     */
    def help {
        println( "DataSync - data synchronizer with configs written in SyncSpec" )
        println( "\t-help\t\tprints this help message" )
        println( "\t-version\tprints program version" )
        println( "\t-cfg [filename]\tspecifies config file to use" )
    }

    /**
     * process version command-line option
     */
    def version {
        println( "DataSync version 0.0.1 -- Copyright 2010, Dmitry Melnichenko" )
    }

    /**
     * process command line arguments
     * @param args arguments can be: <i>-help</i>, <i>-version</i>, <i>-cfs [filename]</i>
     */
    def processCommandLine( args : Array[String] ) {
      if ( args.contains( "-help" ) ) {
          help
      } else if ( args.contains( "-version" ) ) {
          version
      } else {
          val idx = args.indexOf( "-cfg" )
          if ( idx > -1 && args.length > ( idx + 1 ) ) {
              syncWithNamedCfg( args( idx + 1 ) )
          } else {
              syncWithDefaultCfg
          }
      }
    }

    /**
     * Application entry point
     * @param args command-line arguments
     */
    def main( args : Array[String] ) {
      processCommandLine( args )
    }
}

// vim: set ts=4 sw=4 et:
