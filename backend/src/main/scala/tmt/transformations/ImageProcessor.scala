package tmt.transformations

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.nio.{Buffer, ByteBuffer}
import java.nio.file.Files
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import javax.inject.Singleton

import org.bytedeco.javacpp.helper.opencv_core.AbstractIplImage
import org.bytedeco.javacpp.opencv_core.{CvMat, IplImage}
import org.bytedeco.javacpp.{Loader, BytePointer, opencv_core, opencv_imgcodecs}
import org.imgscalr.Scalr
import org.imgscalr.Scalr.Rotation
import tmt.app.AppSettings
import tmt.shared.models.Image

import scala.async.Async._
import scala.concurrent.ExecutionContext

@Singleton
class ImageProcessor(appSettings: AppSettings) {
  Loader.load(classOf[opencv_core])

  private val imageProcessingEc = ExecutionContext.fromExecutorService(
    Executors.newFixedThreadPool(appSettings.imageProcessingThreadPoolSize)
  )

  def dd(image: Image) = async {
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

  def rotate(image: Image) = async {
    val bytePointer = new BytePointer(ByteBuffer.wrap(image.bytes))
    val iplImage = opencv_imgcodecs.cvDecodeImage(opencv_core.cvMat(1, image.bytes.length, opencv_core.CV_8UC1, bytePointer))
    val rotatedImage = rotate1(iplImage, 180)
    val encodeImage = opencv_imgcodecs.cvEncodeImage(".jpeg", rotatedImage)
    val array = new Array[Byte](encodeImage.total() * encodeImage.channels())
    encodeImage.data_ptr().get(array)
    Image(image.name, array)
  }(imageProcessingEc)

  def rotate1(src: IplImage, angle: Int): IplImage = {
    val newImg = AbstractIplImage.create(src.height, src.width, src.depth, src.nChannels)
    opencv_core.cvTranspose(src, newImg)
    opencv_core.cvFlip(newImg, newImg, angle)
    newImg
  }
  
}

object A {
  def dd = {
    Loader.load(classOf[opencv_core])
    val bytes = Files.readAllBytes(new File("/usr/local/data/tmt/frames/input/image-10003.jpeg").toPath)
    val bytePointer = new BytePointer(ByteBuffer.wrap(bytes))
    val image = opencv_imgcodecs.cvDecodeImage(opencv_core.cvMat(1, bytes.length, opencv_core.CV_8UC1, bytePointer))

    val encodeImage: CvMat = opencv_imgcodecs.cvEncodeImage(".jpeg", rotate(image, 90))
    val array = new Array[Byte](encodeImage.total() * encodeImage.channels())
    encodeImage.data_ptr().get(array)
    Files.write(new File("/usr/local/data/tmt/frames/output/000.jpeg").toPath, array)
  }

  def rotate(src: IplImage, angle: Int): IplImage = {
    val newImg = AbstractIplImage.create(src.height, src.width, src.depth, src.nChannels)
    opencv_core.cvTranspose(src, newImg)
    opencv_core.cvFlip(newImg, newImg, angle)
    newImg
  }
}
