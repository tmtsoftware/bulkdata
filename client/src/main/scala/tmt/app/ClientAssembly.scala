package tmt.app

import tmt.views.{ThrottleView, StreamView, SubscriptionView, Body}

class ClientAssembly {

  implicit val scheduler = monifu.concurrent.Implicits.globalScheduler

  val dataStore = new DataStore

  val body = new Body(
    new ThrottleView(dataStore.data),
    new SubscriptionView(dataStore.data),
    new StreamView(dataStore.data)
  )
}
