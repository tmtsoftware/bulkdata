package top

import org.scalajs.dom._

class Rendering(url: String, width: Int, height: Int) {
  val img = document.createElement("img").asInstanceOf[CustomImage]
  def render() = {
    img.onload = { () =>
      UiControls.canvas.width = width
      UiControls.canvas.height = height
      console.log("image loaded")
      UiControls.ctx.drawImage(img, 0, 0, width, height)
      UiControls.URL.revokeObjectURL(img.src)
    }
    img.src = url
  }
}
