package name.dmitrym.syncspec

import java.io.File
import scala.collection.mutable.ArrayBuilder

/**
 * Worker executes SyncSpec operations
 * @author Dmitry Melnichenko
 */
object Worker {
    class SyncEntry(parent : String, name : String, size : Long, lastModified : Long) {
        def this( parent : File, f : File ) = this(parent.getCanonicalPath, f.getCanonicalPath.substring(parent.getCanonicalPath.length+1), f.length, f.lastModified)
        def getParent = parent
        def getName = name
        def getSize = size
        def getLastModified = lastModified
    }

    def listDir( dir : File ) : Array[SyncEntry] = listDir( dir, dir )

    def listDir(parent : File, dir : File) : Array[SyncEntry] = {
        var filesList = new ArrayBuilder.ofRef[SyncEntry]
        val lst = dir.listFiles
        lst.foreach( f =>
            if(! f.isDirectory) filesList += new SyncEntry(parent, f)
            else listDir(parent, f).foreach( f => filesList += f )
        )
        filesList.result.sortWith( sortByNameAsc )
    }

    @deprecated("Deprecated by SyncEntry overloaded constructor")
    def file2SyncEntry( parent : File, f : File ) = new SyncEntry(parent.getCanonicalPath, f.getCanonicalPath.substring(parent.getCanonicalPath.length+1) , f.length, f.lastModified)
    def sortByNameAsc( a : SyncEntry, b : SyncEntry ) = a.getName.compareToIgnoreCase(b.getName) < 0
    def sortByNameDsc( a : SyncEntry, b : SyncEntry ) = a.getName.compareToIgnoreCase(b.getName) > 0
    def sortBySizeAsc( a : SyncEntry, b : SyncEntry ) = a.getSize < b.getSize
    def sortBySizeDsc( a : SyncEntry, b : SyncEntry ) = a.getSize > b.getSize

    def mergeForCopy( srcLst : Array[SyncEntry], dstLst : Array[SyncEntry] ) : Array[SyncEntry] = {
      val resLst = new ArrayBuilder.ofRef[SyncEntry]
      resLst.result
    }

    /**
     * Copy content of <i>from</i> path to <i>to</i> path
     * @param from path to source folder
     * @param to path to destination folder
     */
    def copy(from : String, to : String) : Boolean = {
        println("Simple copy of \"" + from + "\" to \"" + to + "\"")
        val srcF = new File(from)
        if(! srcF.exists ) false
        val dstF = new File(to)
        if(! dstF.exists && ! dstF.mkdirs ) false
        val srcLst = listDir(srcF)
        val dstLst = listDir(dstF)
        val resLst = mergeForCopy(srcLst, dstLst)
        //    1, 2, 3, 4, 5
        //    1,    3, 4,
        // =>    2,       5
        false
    }

    /**
     * Move content of <i>from</i> path to <i>to</i> path
     * @param from path to source folder
     * @param to path to destination folder
     */
    def move(from : String, to : String) : Boolean = {
        println("Simple move of \"" + from + "\" to \"" + to + "\"")
        false
    }

    /**
     * Sync content of <i>from</i> path with <i>to</i> path
     * @param from path to source folder
     * @param to path to destination folder
     */
    def sync(from : String, to : String) : Boolean = {
        println("Simple sync of \"" + from + "\" to \"" + to + "\"")
        false
    }
}
