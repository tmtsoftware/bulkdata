package tmt.shared.models

import prickle.{PicklerPair, CompositePickler}

sealed abstract class Role(val maybeInput: Option[ItemType], val maybeOutput: Option[ItemType]) {
  def entryName: String
  def isProducer = maybeOutput.isDefined
  def isConsumer = maybeInput.isDefined
}

object Role {

  sealed class SourceRole(val entryName: String, val output: ItemType) extends Role(None, Some(output))
  sealed class SinkRole(val entryName: String, val input: ItemType) extends Role(Some(input), None)
  sealed class FlowRole(val entryName: String, val input: ItemType, val output: ItemType) extends Role(Some(input), Some(output))

  case object Source  extends SourceRole("source", ItemType.Image)
  case object Copier  extends SinkRole("copier", ItemType.Image)
  case object Filter  extends FlowRole("filter", ItemType.Image, ItemType.Image)

  case object Metric  extends FlowRole("metric", ItemType.Image, ItemType.Metric)
  case object Frequency  extends FlowRole("frequency", ItemType.Metric, ItemType.Frequency)
  case object Rotator  extends FlowRole("rotator", ItemType.Image, ItemType.Image)

  case object Invalid extends Role(None, None) {
    override def entryName = "invalid"
  }

  val values: Seq[Role] = Seq(Source, Copier, Filter, Metric, Frequency, Rotator, Invalid)

  def withName(name: String) = values.find(_.entryName == name).getOrElse(Invalid)

  implicit val rolePickler: PicklerPair[Role] = CompositePickler[Role]
    .concreteType[Source.type]
    .concreteType[Copier.type]
    .concreteType[Filter.type]
    .concreteType[Metric.type]
    .concreteType[Frequency.type]
    .concreteType[Rotator.type]
    .concreteType[Invalid.type]

}

sealed abstract class ItemType(val entryName: String)

object ItemType {
  case object Image extends ItemType("image")
  case object Metric extends ItemType("metric")
  case object Frequency extends ItemType("frequency")
  case object Invalid extends ItemType("invalid")

  val values: Seq[ItemType] = Seq(Image, Metric, Frequency, Invalid)

  def withName(name: String) = values.find(_.entryName == name).getOrElse(Invalid)

  implicit val itemTypePickler = CompositePickler[ItemType]
    .concreteType[Image.type]
    .concreteType[Metric.type]
    .concreteType[Frequency.type]
}
