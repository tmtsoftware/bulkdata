package top

import top.common.RealImage
import org.scalajs.dom._

import scala.scalajs.js.typedarray.ArrayBuffer

case class RenderingData(url: String, image: RealImage) {
  val img = document.createElement("img").asInstanceOf[CustomImage]
  def render() = {
    img.onload = { () =>
      console.log("image loaded")
      UiControls.ctx.drawImage(img, 0, 0, image.width/3, image.height/3)
      UiControls.URL.revokeObjectURL(img.src)
    }
    img.src = url
  }
}

object RenderingData {
  def fromMessage(message: MessageEvent) = {
    val arrayBuffer = message.data.asInstanceOf[ArrayBuffer]
    val image = RealImageConversions.fromArrayBuffer(arrayBuffer)
    UiControls.canvas.width = image.width/3
    UiControls.canvas.height = image.height/3
    val blob = RealImageConversions.toBlob(image)
    val url = UiControls.URL.createObjectURL(blob)
    RenderingData(url, image)
  }
}
