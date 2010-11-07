/**
 * @author Dmitry Melnichenko
 */
package name.dmitrym.syncspec

/**
 * case classes for operations representation
 */
abstract class Expr
/// copy (from "SRC") (to "DEST")
/// copy (to "DEST") (from "SRC")
/// sync (from "SRC") (to "DEST")
/// sync (to "DEST") (from "SRC")
/// move (from "SRC") (to "DEST")
/// move (to "DEST") (from "SRC")
case class SimpleOp(operator : String, left : (String,String), right : (String,String)) extends Expr

/**
 * Object for operations evaluation
 */
object SyncSpec {
    /**
     * Evaluates given expression
     * @param e expression to evaluate
     */
    def evaluate( e : Expr ) : Boolean = {
        e match {
            case SimpleOp("copy", ("from", from), ("to", to)) => Worker.copy(from, to)
            case SimpleOp("copy", ("to", to), ("from", from)) => Worker.copy(from, to)
            case SimpleOp("move", ("from", from), ("to", to)) => Worker.move(from, to)
            case SimpleOp("move", ("to", to), ("from", from)) => Worker.move(from, to)
            case SimpleOp("sync", ("from", from), ("to", to)) => Worker.sync(from, to)
            case SimpleOp("sync", ("to", to), ("from", from)) => Worker.sync(from, to)
            case _ => false
        }
    }
}
