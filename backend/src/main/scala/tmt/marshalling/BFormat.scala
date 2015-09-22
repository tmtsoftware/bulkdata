package tmt.marshalling

import akka.util.ByteString
import boopickle.Default.Pickle
import boopickle.Default.Unpickle
import boopickle.Pickler

trait BFormat[T] {
  def read(byteString: ByteString): T
  def write(o: T): ByteString
}

object BFormat {
  def apply[T: BFormat] = implicitly[BFormat[T]]

  def make[T](fromBinary: ByteString => T, toBinary: T => ByteString): BFormat[T] = new BFormat[T] {
    def read(byteString: ByteString) = fromBinary(byteString)
    def write(o: T) = toBinary(o)
  }

  implicit val stringFormat = make[String](_.utf8String, ByteString.apply)
  implicit val byeStringFormat = make[ByteString](identity, identity)

  implicit def objectFormat[T: Pickler]: BFormat[T] = BFormat.make[T](
    x => Unpickle[T].fromBytes(x.toByteBuffer),
    x => ByteString(Pickle.intoBytes(x))
  )
}
