package tmt

import monifu.reactive.Observable
import org.scalajs.dom.raw.ErrorEvent
import org.scalajs.dom.{Event, MessageEvent, WebSocket}

import scala.scalajs.js

object Stream {
  def from(socket: WebSocket) = {
    Observable.create[MessageEvent] { subscriber =>
      val obs = subscriber.observer
      socket.onopen = { e: Event =>
        println("***********open")
      }
      socket.onmessage = { e: MessageEvent =>
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

  def event[T <: Event](listener: js.Function1[T, _] => Unit) = Observable.create[T] { subscriber =>
    val obs = subscriber.observer
    listener { e: T =>
      obs.onNext(e)
      obs.onComplete()
    }
  }
}
