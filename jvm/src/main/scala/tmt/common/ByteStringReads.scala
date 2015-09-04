package tmt.common

import akka.util.ByteString

trait ByteStringReads[T] {
  def reads(byteString: ByteString): T
}

object ByteStringReads {
  def apply[T: ByteStringReads] = implicitly[ByteStringReads[T]]

  def make[T](f: ByteString => T): ByteStringReads[T] = new ByteStringReads[T] {
    def reads(byteString: ByteString) = f(byteString)
  }

  implicit val stringReads = make(_.utf8String)
  implicit val byeStringReads = make(identity)
}
