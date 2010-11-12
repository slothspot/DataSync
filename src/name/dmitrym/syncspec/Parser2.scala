package name.dmitrym.syncspec

import scala.Enumeration
import scala.util.parsing.combinator.syntactical.StandardTokenParsers

class Parser2 extends StandardTokenParsers {
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
		case Command.Copy ~ (Location.Source, src) ~ (Location.Destination, dst) => println("Operation: copy")
		case Command.Move ~ _ ~ _ => println("Operation: move")
		case Command.Sync ~ _ ~ _ => println("Operation: sync")
	}
	def command : Parser[Command.Command] = ("copy" | "move" | "sync") ^^ {
		case "copy" => println("command_copy"); Command.Copy
		case "move" => println("command_move"); Command.Move
		case "sync" => println("command_sync"); Command.Sync
	}
	def source : Parser[(Location.Location, String)] = ("from" ~ location) ^^ {
		case "from" ~ loc => println("Src loc: " + loc); (Location.Source, loc)
	}
	def destination : Parser[(Location.Location, String)] = ("to" ~ location) ^^ {
		case "to" ~ loc => println("Dest loc: " + loc); (Location.Destination, loc)
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
