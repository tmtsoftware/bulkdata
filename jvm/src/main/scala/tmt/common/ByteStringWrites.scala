package tmt.common

import akka.util.ByteString

trait ByteStringWrites[T] {
  def writes(o: T): ByteString
}

object ByteStringWrites {
  def apply[T: ByteStringWrites] = implicitly[ByteStringWrites[T]]

  def make[T](f: T => ByteString): ByteStringWrites[T] = new ByteStringWrites[T] {
    def writes(o: T) = f(o)
  }

  implicit val stringWrites = make[String](ByteString.apply)
  implicit val byteStringWrites = make[ByteString](identity)
}
