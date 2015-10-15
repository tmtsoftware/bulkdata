package tmt.images

import org.scalajs.dom._
import org.scalajs.dom.html.{Canvas, Image}
import tmt.common.{Stream, CanvasControls}
import tmt.shared.SharedConfigs

class Rendering(url: String) {

  val img = document.createElement("img").asInstanceOf[Image]
  
  def loaded = {
    val rendering = Stream.event(img.onload_=).map(_ => this)
    img.src = url
    rendering
  }

  def render(canvas: Canvas) = {
    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    ctx.drawImage(img, 0, 0, SharedConfigs.imageWidth, SharedConfigs.imageHeight)
    CanvasControls.URL.revokeObjectURL(img.src)
  }

}
