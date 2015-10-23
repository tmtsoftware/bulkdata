package tmt.app

import tmt.views._

class ClientAssembly {

  implicit val scheduler = monifu.concurrent.Implicits.globalScheduler

  val dataStore = new DataStore

  val body = new Body(
    new ThrottleView(dataStore.data),
    new FrequencyView(dataStore.data),
    new SubscriptionView(dataStore.data),
    new ImageView(dataStore.data)
  )
}
