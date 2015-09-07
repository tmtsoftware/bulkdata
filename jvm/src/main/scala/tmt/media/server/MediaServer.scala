package tmt.media.server

import tmt.media.MediaAssembly

object MediaServer extends MediaAssembly("application") with App {
  mediaServerFactory.make().run()
}
