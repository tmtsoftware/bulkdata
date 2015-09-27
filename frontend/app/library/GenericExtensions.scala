package library

import play.api.libs.json.{Json, Writes}

object GenericExtensions {

  implicit class RichGeneric[T](val value: T) extends AnyVal {
    def toJson(implicit writes: Writes[T]) = Json.toJson(value)
  }
}
