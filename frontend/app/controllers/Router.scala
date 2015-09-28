package controllers

import java.nio.ByteBuffer
import boopickle.Default._

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  def read[T: Pickler](p: ByteBuffer) = Unpickle[T].fromBytes(p)
  def write[T: Pickler](r: T) = Pickle.intoBytes(r)
}
