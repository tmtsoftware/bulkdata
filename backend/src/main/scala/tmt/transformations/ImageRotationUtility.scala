package tmt.transformations

import java.io.{ByteArrayOutputStream, ByteArrayInputStream}
import javax.imageio.ImageIO

import org.imgscalr.AsyncScalr
import org.imgscalr.Scalr.Rotation
import tmt.shared.models.Image


object ImageRotationUtility {
  def rotate(image: Image) = {
    val bufferedImage = ImageIO.read(new ByteArrayInputStream(image.bytes))
    val rotatedBufferedImage = AsyncScalr.rotate(bufferedImage, Rotation.CW_90).get()
    val baos = new ByteArrayOutputStream()
    ImageIO.write(rotatedBufferedImage, "jpg", baos)
    baos.flush()
    val rotatedImage = Image(image.name, baos.toByteArray)
    baos.close()
    rotatedImage
  }
}
