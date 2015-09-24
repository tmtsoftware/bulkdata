package tmt.server

import enumeratum.{Enum, EnumEntry}

sealed abstract class Role(override val entryName: String) extends EnumEntry

object Role extends Enum[Role] {
  def values = findValues

  case object Source  extends Role("source")
  case object Copier  extends Role("copier")
  case object Filter  extends Role("filter")

  case object Metric  extends Role("metric")
  case object Accumulator  extends Role("accumulator")
  case object Frequency  extends Role("frequency")
}
