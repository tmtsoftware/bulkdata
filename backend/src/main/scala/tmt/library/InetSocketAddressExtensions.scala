package tmt.library

import java.net.InetSocketAddress

import akka.http.scaladsl.model.Uri

object InetSocketAddressExtensions {
  
  implicit class RichInetSocketAddress(address: InetSocketAddress) {
    def absoluteUri(relativeUri: String) = Uri(s"http://${address.getHostName}:${address.getPort}$relativeUri")
    def update(uri: Uri) = uri.withAuthority(address.getHostName, address.getPort)
  }

}
