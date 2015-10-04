package tmt.actors

import akka.actor.{Actor, ActorRef, Props}
import tmt.app.{ActorConfigs, AppSettings}
import tmt.common.Messages
import tmt.shared.Topics

import scala.concurrent.duration.FiniteDuration

class TickerService(
  appSettings: AppSettings, actorConfigs: ActorConfigs
) extends SourceActorLink[Ticker.Tick](actorConfigs, Topics.Throttle) {
  
  import actorConfigs._
  def wrap(sourceLinkedRef: ActorRef) = 
    system.actorOf(Ticker.props(appSettings.binding.name, appSettings.imageReadThrottle, sourceLinkedRef))
}

class Ticker(serverName: String, initialDelay: FiniteDuration, sourceLinkedRef: ActorRef) extends Actor {
  import context.dispatcher

  val scheduler = context.system.scheduler

  def startNewSchedule(delay: FiniteDuration) = scheduler.schedule(delay, delay, sourceLinkedRef, Ticker.Tick)

  var currentSchedule = startNewSchedule(initialDelay)

  def receive = {
    case Messages.UpdateDelay(`serverName`, newDelay) =>
      println("**** new delay is: ", newDelay)
      currentSchedule.cancel()
      currentSchedule = startNewSchedule(newDelay)
  }
}

object Ticker {
  trait Tick
  case object Tick extends Tick

  def props(serverName: String, initialDelay: FiniteDuration, sourceLinkedRef: ActorRef) = Props(
    new Ticker(serverName, initialDelay, sourceLinkedRef)
  )
}
