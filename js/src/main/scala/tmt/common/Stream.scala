package tmt.common

import monifu.reactive.Observable
import org.scalajs.dom.{ErrorEvent, Event, MessageEvent, WebSocket}

import scala.scalajs.js

object Stream {
  def socket(socket: WebSocket) = Observable.create[MessageEvent] { subscriber =>
    socket.onopen = { e: Event =>
      println("***********open")
    }
    socket.onmessage = { e: MessageEvent =>
      subscriber.onNext(e)
    }
    socket.onclose = { e: Event =>
      println("**************closed")
      subscriber.onComplete()
    }
    socket.onerror = { e: ErrorEvent =>
      println("**************error")
      subscriber.onError(throw new RuntimeException(e.message))
    }
  }

  def event[T <: Event](listener: js.Function1[T, _] => Unit) = Observable.create[T] { subscriber =>
    listener { e: T =>
      subscriber.onNext(e)
      subscriber.onComplete()
    }
  }
}
