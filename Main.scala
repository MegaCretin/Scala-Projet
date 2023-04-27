import scala.collection.immutable.ArraySeq
import scala.io.Source

/**
 * Main app containg program loop
 */
object Main extends App {

  println("Starting application")

  val status = run()

  println("\nExiting application")
  println(s"Final status: ${status.message}")

  /**
   * Read action from Stdin and execute it
   * Exit if action is 'exit' or if an error occured (status > 0)
   * DO NOT MODIFY THIS FUNCTION
   */
  def run(canvas: Canvas = Canvas()): Status = {
    println("\n======\nCanvas:")
    canvas.display

    print("\nAction: ")

    val action = scala.io.StdIn.readLine()

    val (newCanvas, status) = execute(ArraySeq.unsafeWrapArray(action.split(' ')), canvas)

    if (status.error) {
      println(s"ERROR: ${status.message}")
    }

    if (status.exit) {
      status 
    } else {
      run(newCanvas)  
    }
  }

  /**
   * Execute various actions depending on an action command and optionnaly a Canvas
   */
  def execute(action: Seq[String], canvas: Canvas): (Canvas, Status) = {
    val execution: (Seq[String], Canvas) => (Canvas, Status) = action.head match {
      case "exit" => Canvas.exit
      case "dummy" => Canvas.dummy
      case "dummy2" => Canvas.dummy2
      // TODO: Add command here
      case "new_canvas" => Canvas.newCanvas
      case "load_image" => Canvas.loadImage
      case "update_pixel" => Canvas.updatePixel
      case "draw" => Canvas.draw
      case _ => Canvas.default
    }

    execution(action.tail, canvas)
  }
}

/**
 * Define the status of the previous execution
 */
case class Status(
  exit: Boolean = false,
  error: Boolean = false,
  message: String = ""
)

/**
 * A pixel is defined by its coordinates along with its color as a char
 */
case class Pixel(x: Int, y: Int, color: Char = ' ') {
  override def toString(): String = {
    color.toString
  }
}
/**
 * Companion object of Pixel case class
 */
object Pixel {
  /**
   * Create a Pixel from a string "x,y"
   */
  def apply(s: String): Pixel = {
    val t = s.split(",")
      .map(_.trim)
      .map(_.toInt)
    Pixel(t(0), t(1))
  }

  /**
   * Create a Pixel from a string "x,y" and a color 
   */
  def apply(s: String, color: Char): Pixel = {
    val t = s.split(",")
      .map(_.trim)
      .map(_.toInt)
    Pixel(t(0), t(1), color)
  }
}

/**
 * A Canvas is defined by its width and height, and a matrix of Pixel
 */
case class Canvas(width: Int = 0, height: Int = 0, pixels: Vector[Vector[Pixel]] = Vector()) {

  /**
   * Print the canvas in the console
   */
  def display: Unit = {
    if (pixels.size == 0) {
      println("Empty Canvas")
    } else {
      println(s"Current canvas:")
      println(s"Size: $width x $height")
      pixels.foreach((vector:Vector[Pixel]) =>
        vector.foreach((pixel:Pixel) => print(pixel))
        println(" "))
    }
  }

  /**
   * Takes a pixel in argument and put it in the canvas
   * in the right position with its color
   */
  def update(pixel: Pixel): Canvas = {
    
    val newPixels = pixels.updated(pixel.y, pixels(pixel.y).updated(pixel.x, pixel))

    this.copy(pixels = newPixels)
  }

  /**
   * Return a Canvas containing all modifications
   */
  def updates(pixels: Seq[Pixel]): Canvas = {
    pixels.foldLeft(this)((f, p) => f.update(p))
  }

  // TODO: Add any useful method
}

/**
 * Companion object for Canvas case class
 */
object Canvas {
  /**
   * Exit execution
   */
  def exit(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    (canvas, Status(exit = true, message = "Received exit signal"))

  /**
   * Default execution for unknown action
   */
  def default(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    (canvas, Status(error = true, message = s"Unknown command"))

  /**
   * Create a static Canvas
   */
  def dummy(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size > 0)
      (canvas, Status(error = true, message = "dummy action does not excpect arguments"))
    else {
      val dummyCanvas = Canvas(
        width = 3,
        height = 4,
        pixels = Vector(
          Vector(Pixel(0, 0, '#'), Pixel(1, 0, '.'), Pixel(2, 0, '#')),
          Vector(Pixel(0, 1, '#'), Pixel(1, 1, '.'), Pixel(2, 1, '#')),
          Vector(Pixel(0, 2, '#'), Pixel(1, 2, '.'), Pixel(2, 2, '#')),
          Vector(Pixel(0, 3, '#'), Pixel(1, 3, '.'), Pixel(2, 3, '#'))
        )
      )

      (dummyCanvas, Status())
    }

  /**
   * Create a static canvas using the Pixel companion object
   */
  def dummy2(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size > 0)
      (canvas, Status(error = true, message = "dummy action does not excpect arguments"))
    else {
      val dummyCanvas = Canvas(
        width = 3,
        height = 1,
        pixels = Vector(
          Vector(Pixel("0,0", '#'), Pixel("1,0"), Pixel("2,0", '#')),
        )
      )

      (dummyCanvas, Status())
    }

  def newCanvas(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size < 3)
      (canvas, Status(error = true, message = s"La commande attend 3 arguments (${arguments.size}/3). Exemple: new_canvas 13 13 #"))
    else if (!arguments(0).forall(_.isDigit) || !arguments(1).forall(_.isDigit) || arguments(2).size != 1)
      (canvas, Status(error = true, message = "Veuiller renseigner des numéros. Exemple: new_canvas 13 13 #"))
    else {
      val newCanvas = Canvas(
        width = arguments(0).toInt,
        height = arguments(1).toInt,
        pixels = Vector.fill(arguments(1).toInt)(Vector.fill(arguments(0).toInt)(Pixel(0, 0, arguments(2).head)))
      )

      (newCanvas, Status())
    }

  def loadImage(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size < 1)
      (canvas, Status(error = true, message = "Il faut donner le nom du fichier"))
    else if (arguments.size > 1)
      (canvas, Status(error = true, message = "Il ne faut donner que le nom du fichier"))
    else {
      val fileName = arguments(0)
      try {
        val content: Vector[String] = Source.fromFile(fileName).getLines().toVector
        val pixels = content.map { line =>
          line.map(char => Pixel(0, 0, char)).toVector
        }
        val loadCanvas = Canvas(pixels(0).size, pixels.size, pixels)

        (loadCanvas, Status())
      } catch {
        case e: Exception => (canvas, Status(error = true, message = s"Erreur lors du chargement de l'image: $e."))
      }
    }

  def updatePixel(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size < 2) {
      (canvas, Status(error = true, message = s"La commande attend 2 arguments (${arguments.size}/2). Exemple: update_pixel 2,2 #"))
    }
    else {
      val coordonnee = arguments(0).split(",")
      val updateCanvas = canvas.update(Pixel(coordonnee(0).toInt, coordonnee(1).toInt, arguments(1).head))

      (updateCanvas, Status())
    }

  def draw(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size < 2)
      (canvas, Status(error = true, message = s"La commande attend 2 arguments (${arguments.size}/2)."))
    else {
      arguments(0) match {
        case "line" => lineDraw(arguments, canvas)
        case "rectangle" => rectangleDraw(arguments, canvas)
        case "fill" => fill(arguments, canvas)
        case "triangle" => triangleDraw(arguments, canvas)
        case "polygon" => polygonDraw(arguments, canvas)
        case _ => (canvas, Status(error = true, message = s"La commande ${arguments(0)} n'existe pas."))
      }
    }

  def lineDraw(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size < 4 || arguments.size > 4){
      (canvas, Status(error = true, message = s"La commande attend 4 arguments (${arguments.size}/4). Exemple: draw line 2,2 4,2 #"))
    }
    else {
      val pixel1 = Pixel(arguments(1))
      val pixel2 = Pixel(arguments(2))
      val color = arguments(3).charAt(0)

      val (x1, y1, x2, y2) = (pixel1.x, pixel1.y, pixel2.x, pixel2.y)

      if (x1 < 0 || x1 >= canvas.width || y1 < 0 || y1 >= canvas.height || x2 < 0 || x2 >= canvas.width || y2 < 0 || y2 >= canvas.height) {
        return (canvas, Status(error = true, message = s"Impossible de dessiner en dehors du canvas."))
      }
      val (dx, dy) = (math.abs(x2 - x1), math.abs(y2 - y1))

      var x = x1
      var y = y1
      val sx = if (x1 < x2) 1 else -1
      val sy = if (y1 < y2) 1 else -1
      var lineCanvas = canvas

      if (dx > dy) {
        var D = 2 * dy - dx

        for (i <- 0 until dx) {
          lineCanvas = lineCanvas.update(Pixel(x, y, color))
          if (D > 0) {
            y += sy
            D -= 2 * dx
          }
          D += 2 * dy
          x += sx
        }
      } else {
        var D = 2 * dx - dy

        for (i <- 0 until dy) {
          lineCanvas = lineCanvas.update(Pixel(x, y, color))
          if (D > 0) {
            x += sx
            D -= 2 * dy
          }
          D += 2 * dx
          y += sy
        }
      }

      (lineCanvas, Status())
    }

  def rectangleDraw(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {

    if (arguments.size < 4) {
      return (canvas, Status(error = true, message = s"La commande attend 4 arguments (${arguments.size}/4). Exemple: draw rectangle 2,2 4,4 #"))
    }
    else if (!arguments(1).forall(_.isDigit) || !arguments(2).forall(_.isDigit) || !arguments(3).forall(_.isDigit)) {
      return (canvas, Status(error = true, message = s"La commande rectangle attend des arguments de ces types: rectangle x1,y1 x2,y2 ."))
    }
    else {
      val pixel1 = Pixel(arguments(1))
      val pixel2 = Pixel(arguments(2))
      val color = arguments(3).charAt(0)

      val (x1, y1, x2, y2) = (math.min(pixel1.x, pixel2.x), math.min(pixel1.y, pixel2.y), math.max(pixel1.x, pixel2.x), math.max(pixel1.y, pixel2.y))

      var rectangleCanvas = canvas
      rectangleCanvas = lineDraw(Seq("line", s"$x1,$y1", s"$x2,$y1", s"$color"), rectangleCanvas)._1
      rectangleCanvas = lineDraw(Seq("line", s"$x1,$y2", s"$x2,$y2", s"$color"), rectangleCanvas)._1
      rectangleCanvas = lineDraw(Seq("line", s"$x1,$y1", s"$x1,$y2", s"$color"), rectangleCanvas)._1
      rectangleCanvas = lineDraw(Seq("line", s"$x2,$y1", s"$x2,$y2", s"$color"), rectangleCanvas)._1

      (rectangleCanvas, Status())
    }
  }

  def fill(arguments: Seq[String], canvas: Canvas): (Canvas, Status) =
    if (arguments.size < 3) {
      return (canvas, Status(error = true, message =s"La commande attend 2 arguments (${arguments.size}/2)."))
    }
    else if (!arguments(1).forall(_.isDigit)) {
      return (canvas, Status(error = true, message = s"La commande fill attend des arguments de ces types: fill x,y ."))
    }
    else {
      val pixel = Pixel(arguments(1))

      if (pixel.x < 0 || pixel.x >= canvas.width || pixel.y < 0 || pixel.y >= canvas.height) {
        return (canvas, Status(error = true, message = s"Vous avez donné des coordonnées en dehors du canvas."))
      }

      val color = arguments(2).charAt(0)
      val targetColor = canvas.pixels(pixel.y)(pixel.x).color
      val updatedPixels = fillRecursive(pixel.x, pixel.y, targetColor, color, canvas)

      (canvas, Status())
    }

  def fillRecursive(x: Int, y: Int, targetColor: Char, color: Char, canvas: Canvas): Canvas = {
    if (x < 0 || x >= canvas.width || y < 0 || y >= canvas.height || canvas.pixels(y)(x).color != targetColor) {
      canvas
    } else {
      Seq((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)).foldLeft(canvas.update(Pixel(x, y, color))) { (canvas, coords) =>
        val (xx, yy) = coords
        fillRecursive(xx, yy, targetColor, color, canvas)
      }
    }
  }

  def triangleDraw(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size < 4) {
      return (canvas, Status(error = true, message = s"La commande attend 4 arguments (${arguments.size}/4). Exemple: draw triangle 2,2 4,2 #"))
    }
    else if (!arguments(1).forall(_.isDigit) || !arguments(2).forall(_.isDigit) || !arguments(3).forall(_.isDigit)){
      return (canvas, Status(error = true, message = s"Erreur de type. Exemple: triangle x1,y1 x2,y2 x3,y3 ."))
    }
    else {
      val pixel1 = Pixel(arguments(1))
      val pixel2 = Pixel(arguments(2))
      val pixel3 = Pixel(arguments(3))
      val color = arguments(4).charAt(0)

      var triangleCanvas = canvas
      triangleCanvas = lineDraw(Seq("line", s"${pixel1.x},${pixel1.y}", s"${pixel2.x},${pixel2.y}", s"$color"), triangleCanvas)._1
      triangleCanvas = lineDraw(Seq("line", s"${pixel2.x},${pixel2.y}", s"${pixel3.x},${pixel3.y}", s"$color"), triangleCanvas)._1
      triangleCanvas = lineDraw(Seq("line", s"${pixel3.x},${pixel3.y}", s"${pixel1.x},${pixel1.y}", s"$color"), triangleCanvas)._1

      (triangleCanvas, Status())
    }
  }

  def polygonDraw(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size < 3) {
      return (canvas, Status(error = true, message = s"La commande attend 2 arguments (${arguments.size}/2). Exemple: draw polygon 2,2 4,2 #"))
    }
    else if (!arguments.forall(_.forall(_.isDigit))) {
      return (canvas, Status(error = true, message = "La commande polygon attend des arguments de ces types: polygon x1,y1 x2,y2 x3,y3 xn,yn ."))
    }
    else {
      val color = arguments(arguments.size - 1).charAt(0)
      var polygonCanvas = canvas

      for (i <- 1 until arguments.size - 2) {
        val (polygonCanvas1, _) = lineDraw(Seq("line", arguments(i), arguments(i + 1), color.toString), polygonCanvas)
        polygonCanvas = polygonCanvas1
      }
      val (polygonCanvas2, _) = lineDraw(Seq("line", arguments(arguments.size - 2), arguments(1), color.toString), polygonCanvas)

      (polygonCanvas2, Status())
    }
  }
}
