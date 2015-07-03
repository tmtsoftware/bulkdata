package tmt

import monifu.reactive.Observable

object StreamExtensions {
  implicit class RichStream[K, V](val observable: Observable[(K, V)]) extends AnyVal {
    def mapValues[T](f: V => T) = observable.map { case (k, v) => k -> f(v) }
  }
}
