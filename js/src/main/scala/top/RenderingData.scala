package top

import top.common.Image
import org.scalajs.dom._

case class RenderingData(url: String, image: Image) {
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
  def fromImage(image: Image) = {
    UiControls.canvas.width = image.width/3
    UiControls.canvas.height = image.height/3
    val blob = ImageConversions.toBlob(image)
    val url = UiControls.URL.createObjectURL(blob)
    RenderingData(url, image)
  }
}
