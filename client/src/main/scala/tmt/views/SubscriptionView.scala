package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.ViewData
import tmt.css.Styles
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

  val leftArrow = " \u2192 "
  val downArrow = "\u2193"

  def frag = div(
    label("Make Connection"),
    hr(Styles.hr),
    Rx {
      makeOptions("select output server", dataStore.producers(), topicName)
    },
    label(downArrow)(Styles.blockDisplay, Styles.centerAlign),
    Rx {
      makeOptions("select input server", consumers(), serverName)
    },
    blockButton("Connect", _ => addConnection()),
    br,
    Rx{
      ul(`class` := "list-group")(
        connectionSet().connections.toSeq.map { c  =>
          li(`class` := "list-group-item")(
            label(c.topic + leftArrow + c.server)(Styles.normalFontWeight),
            br,
            a(onclick := {() => removeConnection(c)}, href := "#")("disconnect")
          )
        }
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
