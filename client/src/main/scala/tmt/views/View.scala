package tmt.views

import scalatags.JsDom.all.Modifier

trait View {
  def viewTitle: Modifier
  def viewContent: Modifier
  def viewAction: Modifier
}
