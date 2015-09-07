package tmt.wavefront

import enumeratum.{Enum, EnumEntry}

sealed abstract class Role(override val entryName: String) extends EnumEntry

object Role extends Enum[Role] {
  def values = findValues

  case object ImageSource  extends Role("image-source")

  case object ImageCopy  extends Role("image-copy")
  case object ImageFilter  extends Role("image-filter")

  case object MetricsPerImage  extends Role("metrics-per-image")
  case object MetricsAgg  extends Role("metrics-agg")
}
