package top

import monifu.reactive.Observable
import org.scalajs.dom.raw.ErrorEvent
import org.scalajs.dom.{Event, MessageEvent, WebSocket}

object Stream {
  def from(socket: WebSocket) = {
    Observable.create[MessageEvent] { subscriber =>
      val obs = subscriber.observer
      socket.onopen = { e: Event =>
        println("***********open")
      }
      socket.onmessage = { e: MessageEvent =>
        println("message arrived")
        obs.onNext(e)
      }
      socket.onclose = { e: Event =>
        println("**************closed")
        obs.onComplete()
      }
      socket.onerror = { e: ErrorEvent =>
        println("**************error")
        obs.onError(throw new RuntimeException(e.message))
      }
    }
  }
}
