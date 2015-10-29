package tmt.images

import org.scalajs.dom._
import org.scalajs.dom.html.{Canvas, Image}
import tmt.common.{Constants, Stream}

class Rendering(url: String) {

  val img = document.createElement("img").asInstanceOf[Image]
  
  def loaded = {
    val rendering = Stream.event(img.onload_=).map(_ => this)
    img.src = url
    rendering
  }

  def render(ctx: CanvasRenderingContext2D) = {
    ctx.drawImage(img, 0, 0, Constants.CanvasWidth, Constants.CanvasHeight)
    Constants.URL.revokeObjectURL(img.src)
  }

}
