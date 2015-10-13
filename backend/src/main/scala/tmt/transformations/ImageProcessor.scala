package tmt.transformations

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import javax.inject.Singleton

import org.imgscalr.Scalr
import org.imgscalr.Scalr.Rotation
import tmt.shared.models.Image

import scala.concurrent.ExecutionContext
import async.Async._

@Singleton
class ImageProcessor {

  private val imageProcessingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(15))

  def rotate(image: Image) = async {
    val bufferedImage = ImageIO.read(new ByteArrayInputStream(image.bytes))
    val rotatedBufferedImage = Scalr.rotate(bufferedImage, Rotation.FLIP_VERT)
    makeNewImage(image.name, rotatedBufferedImage)
  }(imageProcessingEc)

  private def makeNewImage(name: String, rotatedBufferedImage: BufferedImage): Image = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(rotatedBufferedImage, "jpeg", baos)
    baos.flush()
    val rotatedImage = Image(name, baos.toByteArray)
    baos.close()
    rotatedImage
  }
}
