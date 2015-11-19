package tmt.shared.models

import prickle.{PicklerPair, CompositePickler}

sealed abstract class Role(val entryName: String, val input: ItemType, val output: ItemType)

object Role {
  case object ScienceImageSource  extends Role("science-image-source", ItemType.Empty, ItemType.Empty)
  
  case object Wavefront  extends Role("wavefront", ItemType.Empty, ItemType.Image)
  case object Copier  extends Role("copier", ItemType.Image, ItemType.Empty)
  case object Filter  extends Role("filter", ItemType.Image, ItemType.Image)

  case object Metadata  extends Role("metadata", ItemType.Image, ItemType.Metadata)
  case object Metric  extends Role("metric", ItemType.Metadata, ItemType.Metric)
  case object Rotator  extends Role("rotator", ItemType.Image, ItemType.Image)

  case object Empty extends Role("empty", ItemType.Empty, ItemType.Empty)

  val values: Seq[Role] = Seq(ScienceImageSource, Wavefront, Copier, Filter, Metadata, Metric, Rotator, Empty)

  def withName(name: String) = values.find(_.entryName == name).getOrElse(Empty)

  implicit val rolePickler: PicklerPair[Role] = CompositePickler[Role]
    .concreteType[ScienceImageSource.type]
    .concreteType[Wavefront.type]
    .concreteType[Copier.type]
    .concreteType[Filter.type]
    .concreteType[Metadata.type]
    .concreteType[Metric.type]
    .concreteType[Rotator.type]
    .concreteType[Empty.type]

}

sealed abstract class ItemType(val entryName: String) {
  def nonEmpty = this != ItemType.Empty
}

object ItemType {
  case object Image extends ItemType("image")
  case object Metadata extends ItemType("metadata")
  case object Metric extends ItemType("metric")
  case object Empty extends ItemType("empty")

  val values: Seq[ItemType] = Seq(Image, Metadata, Metric, Empty)

  def withName(name: String) = values.find(_.entryName == name).getOrElse(Empty)

  implicit val itemTypePickler = CompositePickler[ItemType]
    .concreteType[Image.type]
    .concreteType[Metadata.type]
    .concreteType[Metric.type]
    .concreteType[Empty.type]
}
