package name.dmitrym.syncspec

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

class Parser2 extends StandardTokenParsers {
	lexical.reserved += ("copy", "move", "sync", "from", "to")
	def operation = command ~ source ~ destination | command ~ destination ~ source
	def command = "copy" | "move" | "sync"
	def source = "from" ~ location
	def destination = "to" ~ location
	def location = stringLit
}
