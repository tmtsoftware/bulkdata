package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.shared.models._

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

class SubscriptionView(dataStore: ViewData)(implicit ec: ExecutionContext) extends View {

  val topicName = Var("")
  val serverName = Var("")
  val consumers = dataStore.consumersOf(topicName)

  val connection = Rx(Connection(serverName(), topicName()))

  val connectionSet = Var(ConnectionSet.empty)

  Obs(dataStore.connectionSet) {
    connectionSet() = dataStore.connectionSet()
  }

  val i = "i".tag

  def viewTitle = h5("Connect")

  def viewContent = div(
    label("Select output server"),
    makeSelection(dataStore.producers, topicName),

    label("Select input server"),
    makeSelection(consumers, serverName)
  )

  def viewAction = div(
    a(cls := "btn-floating waves-effect waves-light")(
      onclick := {() => addConnection()},
      i(cls := "material-icons")("add")
    ),

    Rx {
      table(
        tbody(
          connectionSet().connections.toSeq.map { c =>
            tr(
              td(c.topic),
              td("====>"),
              td(c.server),
              td(
                a(cls := "btn-floating waves-effect waves-light red")(
                  onclick := {() => removeConnection(c)},
                  i(cls := "material-icons")("remove")
                )
              )
            )
          }
        )
      )
    }
  )

  def addConnection() = {
    subscribe(connection()).onSuccess {
      case x if x.status == 202 =>  connectionSet() = connectionSet().add(connection())
    }
  }

  def removeConnection(connection: Connection) = {
    unsubscribe(connection)
    connectionSet() = connectionSet().remove(connection)
  }

  def subscribe(connection: Connection) = Ajax.post(s"${connection.server}/subscribe/${connection.topic}")

  def unsubscribe(connection: Connection) = Ajax.post(s"${connection.server}/unsubscribe/${connection.topic}")
}
