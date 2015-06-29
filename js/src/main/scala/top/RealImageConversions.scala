package top

import top.common.RealImage
import org.scalajs.dom._
import org.scalajs.dom.raw.Blob

import scala.scalajs.js
import scala.scalajs.js.typedarray.{Uint8Array, TypedArrayBuffer, ArrayBuffer}
import scala.scalajs.js.JSConverters._

object RealImageConversions {

  def fromArrayBuffer(arrayBuffer: ArrayBuffer) = {
    val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
    RealImage.fromByteBuffer(byteBuffer)
  }

  def toBlob(image: RealImage) = {
    val uint8Array = new Uint8Array(image.bytes.toJSArray)
    val properties = js.Dynamic.literal("type" -> image.mimeType).asInstanceOf[BlobPropertyBag]
    new Blob(js.Array(uint8Array), properties)
  }
}
