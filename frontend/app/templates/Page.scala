package templates

import play.twirl.api.Html

import scalatags.Text.all._
import scalatags.Text.{tags2 => t}
import controllers.routes

class Page(titleValue: String) {
  def page = html(
    head(
      t.title(titleValue),
      link(rel := "stylesheet", `type` := "text/css", href := routes.Assets.at("stylesheets/streams.css").url),
      link(rel := "stylesheet", `type` := "text/css", href := routes.Assets.at("stylesheets/main.css").url),
      link(rel := "shortcut icon", `type` := "image/png", href := routes.Assets.at("images/favicon.png").url),
      script(`type` := "text/javascript", src := routes.Assets.at("lib/jquery/jquery.min.js").url)
    ),
    body(
      raw(playscalajs.html.scripts("client").toString())
    )
  )
  
  def render = Html(page.render)
}
