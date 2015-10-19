package tmt.shared.models

import prickle.{PicklerPair, CompositePickler}

sealed abstract class Role(val entryName: String, val input: ItemType, val output: ItemType)

object Role {

  case object Source  extends Role("source", ItemType.Empty, ItemType.Image)
  case object Copier  extends Role("copier", ItemType.Image, ItemType.Empty)
  case object Filter  extends Role("filter", ItemType.Image, ItemType.Image)

  case object Metric  extends Role("metric", ItemType.Image, ItemType.Metric)
  case object Frequency  extends Role("frequency", ItemType.Metric, ItemType.Frequency)
  case object Rotator  extends Role("rotator", ItemType.Image, ItemType.Image)

  case object Invalid extends Role("invalid", ItemType.Invalid, ItemType.Invalid)

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

sealed abstract class ItemType(val entryName: String) {
  def nonEmpty = this != ItemType.Empty
}

object ItemType {
  case object Image extends ItemType("image")
  case object Metric extends ItemType("metric")
  case object Frequency extends ItemType("frequency")
  case object Empty extends ItemType("empty")
  case object Invalid extends ItemType("invalid")

  val values: Seq[ItemType] = Seq(Image, Metric, Frequency, Empty, Invalid)

  def withName(name: String) = values.find(_.entryName == name).getOrElse(Invalid)

  implicit val itemTypePickler = CompositePickler[ItemType]
    .concreteType[Image.type]
    .concreteType[Metric.type]
    .concreteType[Frequency.type]
    .concreteType[Empty.type]
    .concreteType[Invalid.type]
}
