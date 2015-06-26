package top.dsl

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import top.common.Image

trait ImageMarshalling {

  implicit val imageMarshaller = Marshaller.opaque { images: Source[Image, Any] =>
    val byteStrings = images.map(Image.toBytes)
    HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, byteStrings)
  }

  implicit val imageUnmarshaller = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(Image.fromBytes)
  }
}
