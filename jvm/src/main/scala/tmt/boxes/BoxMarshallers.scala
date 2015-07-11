package tmt.boxes

import tmt.boxes.http.BoxConversions
import tmt.common.SourceMarshallers

trait BoxMarshallers extends SourceMarshallers {
  implicit val boxMarshaller = marshaller(BoxConversions.toByteString)
  implicit val boxUnmarshaller = unmarshaller(BoxConversions.fromByteString)
}
