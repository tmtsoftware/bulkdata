package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import play.api.mvc.{Action, Controller}
import prickle.Pickle
import services.{ClusterClientService, ConnectionSetService, NodeSetService}
import templates.Page
import tmt.shared.models.Connection

import scala.async.Async._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(
  appSettings: AppSettings,
  clusterClientService: ClusterClientService,
  nodeSetService: NodeSetService,
  connectionSetService: ConnectionSetService
)(implicit ec: ExecutionContext) extends Controller {

  def streams() = Action {
    Ok(new Page("showcase").render)
  }

  def nodes() = Action.async {
    async {
      Ok(Pickle.intoString(await(nodeSetService.onlineNodeSet)))
    }
  }

  def connections() = Action.async {
    async {
      val connectionDataSet = await(connectionSetService.connectionSet)
      Ok(Pickle.intoString(connectionDataSet))
    }
  }

  def throttle(serverName: String, delay: Long) = Action {
    clusterClientService.throttle(serverName, delay.millis)
    Accepted("ok")
  }

  def subscribe(serverName: String, topic: String) = Action.async {
    async {
      await(clusterClientService.subscribe(Connection(serverName, topic)))
      Accepted("ok")
    }
  }

  def unsubscribe(serverName: String, topic: String) = Action {
    clusterClientService.unsubscribe(Connection(serverName, topic))
    Accepted("ok")
  }

  def resetConnections() = Action {
    clusterClientService.resetConnections()
    Accepted("ok")
  }
}
