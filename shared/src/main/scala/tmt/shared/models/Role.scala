package tmt.shared.models

import prickle.{PicklerPair, CompositePickler}

sealed abstract class Role(maybeConsumes: Option[ItemType], maybeProduces: Option[ItemType]) {
  def entryName: String
}
sealed abstract class SourceRole(val entryName: String, val produces: ItemType) extends Role(None, Some(produces))
sealed abstract class SinkRole(val entryName: String, val consumes: ItemType) extends Role(Some(consumes), None)
sealed abstract class FlowRole(val entryName: String, val consumes: ItemType, val produces: ItemType) extends Role(Some(consumes), Some(produces))

object Role {
  case object Source  extends SourceRole("source", ItemType.Image)
  case object Copier  extends SinkRole("copier", ItemType.Image)
  case object Filter  extends FlowRole("filter", ItemType.Image, ItemType.Image)

  case object Metric  extends FlowRole("metric", ItemType.Image, ItemType.Metric)
  case object Frequency  extends FlowRole("frequency", ItemType.Metric, ItemType.Frequency)
  case object Rotator  extends FlowRole("rotator", ItemType.Image, ItemType.Image)

  case object NoOp extends Role(None, None) {
    override def entryName = ""
  }

  val values: Seq[Role] = Seq(Source, Copier, Filter, Metric, Frequency, Rotator, NoOp)

  def withName(name: String) = values
    .find(_.entryName == name)
    .getOrElse(throw new RuntimeException(s"Role with name: $name is not found"))

  implicit val rolePickler: PicklerPair[Role] = CompositePickler[Role]
    .concreteType[Source.type]
    .concreteType[Copier.type]
    .concreteType[Filter.type]
    .concreteType[Metric.type]
    .concreteType[Frequency.type]
    .concreteType[Rotator.type]
    .concreteType[NoOp.type]

}

sealed abstract class ItemType(val entryName: String)

object ItemType {
  case object Image extends ItemType("image")
  case object Metric extends ItemType("metric")
  case object Frequency extends ItemType("frequency")

  implicit val itemTypePickler = CompositePickler[ItemType]
    .concreteType[Image.type]
    .concreteType[Metric.type]
    .concreteType[Frequency.type]
}
