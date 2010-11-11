package name.dmitrym.syncspec

import scala.util.parsing.combinator.JavaTokenParsers

class Parser extends JavaTokenParsers {
	def operation = command ~ source ~ destination | command ~ destination ~ source
	def command = "copy" | "move" | "sync"
	def source = "from" ~ location
	def destination = "to" ~ location
	def location = stringLiteral
}
