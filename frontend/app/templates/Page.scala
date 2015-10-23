package templates

import play.twirl.api.Html

import scalatags.Text.all._
import scalatags.Text.{tags2 => t}
import controllers.routes

class Page(titleValue: String) {
  val docType = "<!DOCTYPE html>"

  def page = html(
    head(
      meta(charset := "utf-8"),
      meta("http-equiv".attr :="X-UA-Compatible", content := "IE=edge"),
      meta(name :="viewport", content := "width=device-width, initial-scale=1"),

      t.title(titleValue),

      link(rel := "stylesheet", `type` := "text/css", href := routes.Assets.at("lib/bootstrap/css/bootstrap.min.css").url),
      link(rel := "stylesheet", `type` := "text/css", href := routes.Assets.at("stylesheets/main.css").url),
      link(rel := "shortcut icon", `type` := "image/png", href := routes.Assets.at("images/favicon.png").url)
    ),
    body(
      script(`type` := "text/javascript", src := routes.Assets.at("lib/jquery/jquery.min.js").url),
      script(`type` := "text/javascript", src := routes.Assets.at("lib/bootstrap/js/bootstrap.min.js").url),
      raw(playscalajs.html.scripts("client").toString())
    )
  )

  def render = Html(docType + "\n" + page.render)
}
