package tmt.images

import org.scalajs.dom._
import org.scalajs.dom.html.{Canvas, Image}
import tmt.common.{CanvasControls, Constants, Stream}

class Rendering(url: String) {

  val img = document.createElement("img").asInstanceOf[Image]
  
  def loaded = {
    val rendering = Stream.event(img.onload_=).map(_ => this)
    img.src = url
    rendering
  }

  def render(canvas: Canvas) = {
    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    ctx.drawImage(img, 0, 0, Constants.canvasWidth, Constants.canvasHeight)
    CanvasControls.URL.revokeObjectURL(img.src)
  }

}
