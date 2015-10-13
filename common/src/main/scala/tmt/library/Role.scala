package tmt.library

import enumeratum.{Enum, EnumEntry}

sealed abstract class Role(maybeConsumes: Option[ItemType], maybeProduces: Option[ItemType]) extends EnumEntry
sealed abstract class SourceRole(override val entryName: String, val produces: ItemType) extends Role(None, Some(produces))
sealed abstract class SinkRole(override val entryName: String, val consumes: ItemType) extends Role(Some(consumes), None)
sealed abstract class FlowRole(override val entryName: String, val consumes: ItemType, val produces: ItemType) extends Role(Some(consumes), Some(produces))

object Role extends Enum[Role] {
  def values = findValues

  case object Source  extends SourceRole("source", ItemType.Image)
  case object Copier  extends SinkRole("copier", ItemType.Image)
  case object Filter  extends FlowRole("filter", ItemType.Image, ItemType.Image)

  case object Metric  extends FlowRole("metric", ItemType.Image, ItemType.Metric)
  case object Accumulator  extends FlowRole("accumulator", ItemType.Metric, ItemType.Accumulation)
  case object Frequency  extends FlowRole("frequency", ItemType.Metric, ItemType.Frequency)
  case object Rotator  extends FlowRole("rotator", ItemType.Image, ItemType.Image)
}

sealed abstract class ItemType(override val entryName: String) extends EnumEntry

object ItemType extends Enum[Role] {
  def values = findValues

  case object Image extends ItemType("image")
  case object Metric extends ItemType("metric")
  case object Accumulation extends ItemType("accumulation")
  case object Frequency extends ItemType("frequency")
}
