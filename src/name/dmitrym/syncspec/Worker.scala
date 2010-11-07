package name.dmitrym.syncspec

/**
 * Worker executes SyncSpec operations
 * @author Dmitry Melnichenko
 */
object Worker {
    /**
     * Copy content of <i>from</i> path to <i>to</i> path
     * @param from path to source folder
     * @param to path to destination folder
     */
    def copy(from : String, to : String) : Boolean = {
        println("Simple copy of \"" + from + "\" to \"" + to + "\"")
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
