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

  def frag = div(
    label("Make Connection"),
    hr(Styles.hr),
    Rx { makeOptions1("select output server", dataStore.producers(), topicName() = _, topicName()) },
    label("Subscribed by")(Styles.normalFontWeight),
    Rx{ makeOptions1("select input server", consumers(), serverName() = _, serverName()) },
    button(onclick := {() => addConnection()}, `class` := "btn btn-block btn-default active")("Connect")(Styles.topMargin),
    Rx {
      ul(id := "connections")(
        connectionSet().connections.toSeq.map { c  =>
          li(
            label(c.server)(width := "80px", Styles.normalFontWeight),
            label("\u2192")(width := "30px", Styles.normalFontWeight),
            label(c.topic)(width := "80px", Styles.normalFontWeight),
            a(onclick := {() => removeConnection(c)}, href := "#")("undo")
          )
        }
      )(Styles.ul)
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
