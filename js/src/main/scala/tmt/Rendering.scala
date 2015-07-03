package tmt

import org.scalajs.dom._

class Rendering(url: String, width: Int, height: Int) {
  val img = document.createElement("img").asInstanceOf[CustomImage]
  def render() = {
    img.onload = { () =>
      console.log("image loaded")
      UiControls.ctx.drawImage(img, 0, 0, width, height)
      console.log("image rendered")
      UiControls.URL.revokeObjectURL(img.src)
    }
    img.src = url
  }
}
