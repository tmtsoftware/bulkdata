package tmt.common

import java.io.{ByteArrayInputStream, File}
import java.nio.file.Files
import javax.activation.MimetypesFileTypeMap
import javax.imageio.ImageIO

import akka.util.ByteString
import boopickle.{Unpickle, Pickle}

object BoxConversions {
  def toByteString(box: Box) = ByteString(Pickle.intoBytes(box))
  def fromByteString(byteString: ByteString) = Unpickle[Box].fromBytes(byteString.toByteBuffer)
}

object ImageConversions {
  def fromFile(file: File) = {
    val bytes = Files.readAllBytes(file.toPath)
    Image(
      "",
      "image/jpeg",
      Config.imageWidth,
      Config.imageHeight,
      bytes
    )
  }

  def toByteString(image: Image) = ByteString(image.bytes)
  def fromByteString(byteString: ByteString) = Unpickle[Image].fromBytes(byteString.toByteBuffer)
}
