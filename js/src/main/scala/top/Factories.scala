package top

import monifu.reactive.Observable
import org.scalajs.dom.raw.ErrorEvent
import org.scalajs.dom.{Event, MessageEvent, WebSocket}

object Factories {

  def socketStream(socket: WebSocket) = {
    Observable.create[MessageEvent] { subscriber =>
      socket.onmessage = { e: MessageEvent =>
        subscriber.observer.onNext(e)
      }
      socket.onclose = { e: Event =>
        subscriber.observer.onComplete()
      }
      socket.onerror = { e: ErrorEvent =>
        subscriber.observer.onError(throw new RuntimeException(e.message))
      }
    }

  }
}
