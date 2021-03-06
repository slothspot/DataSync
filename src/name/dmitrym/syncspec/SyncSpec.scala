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

  lexical.reserved += ("copy", "move", "sync", "from", "to", "with", "=>")

  def operation_ex: Parser[(Command.Command, String, String, String)] = (operation ~ rule) ^^ {
    case op ~ r => (op._1, op._2, op._3, r)
  }

  def operation: Parser[(Command.Command, String, String)] = (command ~ source ~ destination | command ~ destination ~ source) ^^ {
    case Command.Copy ~ l1 ~ l2 => (l1, l2) match {
      case ((Location.Source, src), (Location.Destination, dest)) => (Command.Copy, src, dest)
      case ((Location.Destination, dest), (Location.Source, src)) => (Command.Copy, src, dest)
    }
    case Command.Move ~ l1 ~ l2 => (l1, l2) match {
      case ((Location.Source, src), (Location.Destination, dest)) => (Command.Move, src, dest)
      case ((Location.Destination, dest), (Location.Source, src)) => (Command.Move, src, dest)
    }
    case Command.Sync ~ l1 ~ l2 => (l1, l2) match {
      case ((Location.Source, src), (Location.Destination, dest)) => (Command.Sync, src, dest)
      case ((Location.Destination, dest), (Location.Source, src)) => (Command.Sync, src, dest)
    }
  }

  def command: Parser[Command.Command] = ("copy" | "move" | "sync") ^^ {
    case "copy" => Command.Copy
    case "move" => Command.Move
    case "sync" => Command.Sync
  }

  def source: Parser[(Location.Location, String)] = ("from" ~ location) ^^ {
    case "from" ~ loc => (Location.Source, loc)
  }

  def destination: Parser[(Location.Location, String)] = ("to" ~ location) ^^ {
    case "to" ~ loc => (Location.Destination, loc)
  }

  def rule: Parser[String] = ("with" ~ location) ^^ {
    case "with" ~ loc => loc
  }

  def location = stringLit

  def origin = stringLit

  def target = stringLit

  def renamePattern: Parser[(String, String)] = (origin ~ "=>" ~ target) ^^ {
    case origin ~ "=>" ~ target => (origin, target)
  }

  def doMatchOperation(op: String) = {
    operation_ex(new lexical.Scanner(op)) match {
      case Success(rt, n) =>
        println("Source: " + n.source)
        println(rt._1 + "; " + rt._2 + "; " + rt._3 + "; " + rt._4)
        rt match {
          case (Command.Copy, src, dst, p) => Worker.copy(src, dst, p)
          case (Command.Move, src, dst, p) => Worker.move(src, dst, p)
          case (Command.Sync, src, dst, p) => Worker.sync(src, dst, p)
        }
      case NoSuccess(s, n) =>
        println("operation_ex failure: " + s + "; next: " + n.source)
        operation(new lexical.Scanner(op)) match {
          case Success(s, n) => {
            println("doMatchOperation: Success: " + s + "; source: " + n.source)
            s match {
              case (Command.Copy, src, dst) => Worker.copy(src, dst)
              case (Command.Sync, src, dst) => Worker.sync(src, dst)
              case (Command.Move, src, dst) => Worker.move(src, dst)
            }
          }
          case NoSuccess(s, n) => println("doMatchOperation: Failure: " + s + "; next: " + n.source)
        }
    }
  }

  def doMatchPattern(p: String): Option[(String,String)] = {
    renamePattern(new lexical.Scanner(p)) match {
      case Success(rt, n) =>
        println(rt._1 + " goes to " + rt._2)
        Some(rt._1 -> rt._2)
      case NoSuccess(s, n) =>
        println("doMatchPattern: Failure: " + s + "; next: " + n.source)
        None
    }
  }
}
