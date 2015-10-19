package tmt.app

import tmt.views._

class ClientAssembly {

  implicit val scheduler = monifu.concurrent.Implicits.globalScheduler

  val dataStore = new DataStore

  val body = new Body(
    new ConfigurationControlsView(dataStore),
    new ImageStreamView(dataStore.data),
    new FrequencyStreamView(dataStore.data)
  )
}
