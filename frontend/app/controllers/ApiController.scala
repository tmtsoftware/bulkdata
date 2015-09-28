package controllers

import java.nio.ByteBuffer
import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.RoleMappingsFactory
import play.api.mvc.{Action, Controller}
import services.ApiService
import tmt.common.api.Api
import boopickle.Default._

import scala.concurrent.ExecutionContext

@Singleton
class ApiController @Inject()(
  apiService: ApiService,
  roleMappingsFactory: RoleMappingsFactory,
  appSettings: AppSettings)(implicit ec: ExecutionContext) extends Controller {

  def mappings() = Action {
    val roleMappings = roleMappingsFactory.fromConfig(appSettings.bindings)
    import upickle.default._
    Ok(write(roleMappings))
  }

  def autowireApi(path: String) = Action.async(parse.raw) { implicit request =>
    println(s"request path $path")
    val bytes = request.body.asBytes(parse.UNLIMITED).get

    Router.route[Api](apiService)(
      autowire.Core.Request(
        path.split("/"),
        Unpickle[Map[String, ByteBuffer]].fromBytes(ByteBuffer.wrap(bytes))
      )
    ).map { buffer =>
      val data = Array.ofDim[Byte](buffer.remaining())
      buffer.get(data)
      Ok(data)
    }
  }

  def logging = Action(parse.anyContent) { implicit request =>
    request.body.asJson.foreach { msg =>
      println(s"CLIENT - $msg")
    }
    Ok("done")
  }
}
