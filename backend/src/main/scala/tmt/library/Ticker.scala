package tmt.library

import akka.actor.{Props, Actor, ActorRef}
import tmt.common.models.Throttle

import scala.concurrent.duration.FiniteDuration

class Ticker(initialDelay: FiniteDuration, actorRef: ActorRef) extends Actor {
  import context.dispatcher

  val scheduler = context.system.scheduler

  def startNewSchedule(delay: FiniteDuration) =
    scheduler.schedule(delay, delay, actorRef, Ticker.Tick())

  var currentSchedule = startNewSchedule(initialDelay)

  def receive = {
    case Throttle.UpdateDelay(newDelay) =>
      println("**** new delay is: ", newDelay)
      currentSchedule.cancel()
      currentSchedule = startNewSchedule(newDelay)
  }
}

object Ticker {
  case class Tick()

  def props(initialDelay: FiniteDuration, actorRef: ActorRef) = Props(new Ticker(initialDelay, actorRef))
}
