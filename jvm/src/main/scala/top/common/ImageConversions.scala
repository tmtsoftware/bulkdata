package top.common

import java.io.File
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
    val bufferedImage = ImageIO.read(file)
    RealImage(
      file.getName,
      new MimetypesFileTypeMap().getContentType(file),
      bufferedImage.getWidth,
      bufferedImage.getHeight,
      Files.readAllBytes(file.toPath)
    )
  }

  def toByteString(image: RealImage) = ByteString(RealImage.toByteBuffer(image))
  def fromByteString(byteString: ByteString) =  RealImage.fromByteBuffer(byteString.toByteBuffer)
}
