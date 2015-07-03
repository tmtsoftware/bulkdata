package tmt

import org.scalajs.dom._
import tmt.common.Config

class Rendering(url: String) {
  val img = document.createElement("img").asInstanceOf[CustomImage]
  def render() = {
    img.onload = { () =>
      console.log("image loaded")
      UiControls.ctx.drawImage(img, 0, 0, Config.imageWidth, Config.imageHeight)
      console.log("image rendered")
      UiControls.URL.revokeObjectURL(img.src)
    }
    img.src = url
  }
}
