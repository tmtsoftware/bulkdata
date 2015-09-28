package tmt.images

import org.scalajs.dom._
import org.scalajs.dom.html.Image
import tmt.common.{CanvasControls, Stream, SharedConfigs}

class Rendering(url: String) {

  val img = document.createElement("img").asInstanceOf[Image]
  
  def loaded = {
    val rendering = Stream.event(img.onload_=).map(_ => this)
    img.src = url
    rendering
  }

  def render() = {
    CanvasControls.ctx.drawImage(img, 0, 0, SharedConfigs.imageWidth, SharedConfigs.imageHeight)
    CanvasControls.URL.revokeObjectURL(img.src)
  }

}
