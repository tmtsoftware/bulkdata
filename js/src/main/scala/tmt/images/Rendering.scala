package tmt.images

import org.scalajs.dom._
import tmt.common.{UiControls, Stream, SharedConfigs}

class Rendering(url: String) {

  val img = document.createElement("img").asInstanceOf[CustomImage]
  
  def loaded = {
    val rendering = Stream.event(img.onload_=).map(_ => this)
    img.src = url
    rendering
  }

  def render() = {
    UiControls.ctx.drawImage(img, 0, 0, SharedConfigs.imageWidth, SharedConfigs.imageHeight)
    UiControls.URL.revokeObjectURL(img.src)
  }

}
