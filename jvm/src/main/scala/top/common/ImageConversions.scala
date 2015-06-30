package top.common

import java.awt.image.DataBufferByte
import java.io.{ByteArrayInputStream, File}
import java.nio.file.Files
import javax.activation.MimetypesFileTypeMap
import javax.imageio.ImageIO

import akka.util.ByteString

object ImageConversions {
  def toByteString(image: Image) = ByteString(Image.toByteBuffer(image))
  def fromByteString(byteString: ByteString) =  Image.fromByteBuffer(byteString.toByteBuffer)
}

object RealImageConversions {
  def fromFile(file: File) = {
    val bytes = Files.readAllBytes(file.toPath)
    val bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes))
    RealImage(
      file.getName,
      new MimetypesFileTypeMap().getContentType(file),
      bufferedImage.getWidth,
      bufferedImage.getHeight,
      bytes
    )
  }

  def toByteString(image: RealImage) = ByteString(RealImage.toByteBuffer(image))
  def fromByteString(byteString: ByteString) =  RealImage.fromByteBuffer(byteString.toByteBuffer)
}
