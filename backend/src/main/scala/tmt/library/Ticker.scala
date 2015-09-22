package tmt.library

import akka.actor.{Props, Actor, ActorRef}

import scala.concurrent.duration.FiniteDuration

class Ticker(initialDelay: FiniteDuration, actorRef: ActorRef) extends Actor {
  var currentDelay = initialDelay

  import context.dispatcher

  val scheduler = context.system.scheduler
  def tick() = scheduler.schedule(currentDelay, currentDelay, actorRef, Ticker.Tick())
  var currentSchedule = tick()

  def receive = {
    case Ticker.UpdateDelay(newDelay) =>
      currentDelay = newDelay
      currentSchedule.cancel()
      currentSchedule = tick()
  }
}

object Ticker {
  case class UpdateDelay(value: FiniteDuration)
  case class Tick()

  def props(initialDelay: FiniteDuration, actorRef: ActorRef) = Props(new Ticker(initialDelay, actorRef))
}
