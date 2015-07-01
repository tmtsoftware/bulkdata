package top.common

import java.io.{ByteArrayInputStream, File}
import java.nio.file.Files
import javax.activation.MimetypesFileTypeMap
import javax.imageio.ImageIO

import akka.util.ByteString

object BoxConversions {
  def toByteString(box: Box) = ByteString(Box.toByteBuffer(box))
  def fromByteString(byteString: ByteString) =  Box.fromByteBuffer(byteString.toByteBuffer)
}

object ImageConversions {
  def fromFile(file: File) = {
    val bytes = Files.readAllBytes(file.toPath)
    val bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes))
    Image(
      file.getName,
      new MimetypesFileTypeMap().getContentType(file),
      bufferedImage.getWidth,
      bufferedImage.getHeight,
      bytes
    )
  }

  def toByteString(image: Image) = ByteString(Image.toByteBuffer(image))
  def fromByteString(byteString: ByteString) =  Image.fromByteBuffer(byteString.toByteBuffer)
}
