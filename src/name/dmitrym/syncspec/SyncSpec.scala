package name.dmitrym.syncspec

import scala.Enumeration
import scala.util.parsing.combinator.syntactical.StandardTokenParsers

class SyncSpec extends StandardTokenParsers {
    object Command extends Enumeration {
        type Command = Value
        val Copy, Sync, Move = Value
    }

    object Location extends Enumeration {
        type Location = Value
        val Source, Destination = Value
    }

    lexical.reserved += ("copy", "move", "sync", "from", "to")

    def operation = (command ~ source ~ destination | command ~ destination ~ source) ^^ {
        case Command.Copy ~ l1 ~ l2 => (l1,l2) match {
            case ((Location.Source, src), (Location.Destination, dest)) => Worker.copy(src, dest)
            case ((Location.Destination, dest), (Location.Source, src)) => Worker.copy(src,dest)
        }
        case Command.Move ~ l1 ~ l2 => (l1,l2) match {
            case ((Location.Source, src), (Location.Destination, dest)) => Worker.move(src, dest)
            case ((Location.Destination, dest), (Location.Source, src)) => Worker.move(src, dest)
        }
        case Command.Sync ~ l1 ~ l2 => (l1,l2) match {
            case ((Location.Source, src), (Location.Destination, dest)) => Worker.sync(src, dest)
            case ((Location.Destination, dest), (Location.Source, src)) => Worker.sync(src, dest)
        }
    }

    def command : Parser[Command.Command] = ("copy" | "move" | "sync") ^^ {
        case "copy" => Command.Copy
        case "move" => Command.Move
        case "sync" => Command.Sync
    }

    def source : Parser[(Location.Location, String)] = ("from" ~ location) ^^ {
        case "from" ~ loc => (Location.Source, loc)
    }

    def destination : Parser[(Location.Location, String)] = ("to" ~ location) ^^ {
        case "to" ~ loc => (Location.Destination, loc)
    }

    def location = stringLit

    def doMatchOperation( op : String ) = {
        operation( new lexical.Scanner( op ) ) match {
            case Success(s, _) => println("doMatchOperation: Success: " + s)
            case Failure(s, _) => println("doMatchOperation: Failure: " + s)
            case Error(s, _) => println("doMatchOperation: Error: " + s)
        }
    }
}
