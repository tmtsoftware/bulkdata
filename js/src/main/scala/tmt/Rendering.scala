package tmt

import org.scalajs.dom._
import tmt.common.Config

class Rendering(url: String) {

  val img = document.createElement("img").asInstanceOf[CustomImage]
  
  def loadedRendering = {
    val rendering = Stream.event(img.onload_=).map(_ => this)
    img.src = url
    rendering
  }

  def render() = {
    UiControls.ctx.drawImage(img, 0, 0, Config.imageWidth, Config.imageHeight)
    UiControls.URL.revokeObjectURL(img.src)
  }

}
