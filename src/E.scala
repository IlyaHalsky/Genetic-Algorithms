import java.io.{BufferedReader, InputStreamReader}

import scala.annotation.tailrec
import scala.collection.immutable.Seq
import scala.math._

object Settings {
  val comparePrecision: Double = 0.5d
  val side: Int = 10
  val r: Double = 9000.0d
}

import Settings._

object E extends App {

  def findTreasure(start: Circle)(implicit console: Console): (Int, Int) = {
    def getCircle(point: Point): Circle =
      C(point, console.activate(point).distance(true))

    val center = start.center
    val left = getCircle(center.move(-1, 0))
    val right = getCircle(center.move(+1, 0))
    val top = getCircle(center.move(0, 1))
    val bot = getCircle(center.move(0, -1))
    val horizontal = left /\ start /\ top
    val vertical = bot /\ start /\ right
    (horizontal, vertical) match {
      case (Some(p), None) ⇒
        p.toInt
      case (None, Some(p)) ⇒
        p.toInt
      case (Some(p1), Some(p2)) ⇒
        val d1 = console.activate(p1).distance(true)
        val d2 = console.activate(p2).distance(true)
        if (d1 <= d2)
          p1.toInt
        else
          p2.toInt
    }
  }

  def findLabyrinth(mn: (Int, Int))(implicit console: Console): Labyrinth = {
    val circles: Seq[Circle] =
      for {
        angle ← Range(0, 360, 10)
        rad = toRadians(angle)
      } yield {
        val searchPoint = IntP(r * cos(rad), r * sin(rad))
        C(searchPoint, console.activate(searchPoint).distance())
      }
    val points = (for {
      Seq(c1, c2, c3) ← circles.sliding(3).toList
    } yield {
      c1 /\ c2 /\ c3
    }).flatten
    val labPoints = findUnique(points)
    assert(labPoints.size >= 3)
    Labyrinth(labPoints, mn)
  }

  @tailrec
  def findUnique(points: List[Point], acc: List[Point] = Nil): List[Point] = {
    points match {
      case p :: rest ⇒
        if (acc.exists(_ ~= p))
          findUnique(rest, acc)
        else
          findUnique(rest, p :: acc)
      case Nil ⇒ acc
    }
  }

  implicit val console: Console = new Console
  val mn = console.getMN
  val lab: Labyrinth = findLabyrinth(mn)
  val best = lab.findBest
  if (best.r > 10.0)
    while (true) Thread.sleep(10)
  val (ax, ay) = findTreasure(best)
  console.submit(ax, ay)
}

case class Labyrinth(points: List[Point], mn: (Int, Int)) {
  private val (m, n) = mn
  private val (a, b, c) = {
    val a :: rest = points
    val b = rest.find(p ⇒ (p - a) ~= m * side).get // Can fail
    val c = rest.find(p ⇒ (p - b) ~= n * side).get // Can fail #2
    (a, b, c)
  }

  private val cbi: Vector = (c - b).normalize
  private val abi: Vector = (a - b).normalize

  private def getPoint(i: Int, j: Int): Point =
    (b + abi * (i + 0.5) + cbi * (j + 0.5)).toPoint

  def findBest(implicit console: Console): Circle = {
    var minP: Point = Point(10000000.0, 10000000.0)
    var minD = 10000000.0
    for {
      i ← 0 until m
      j ← 0 until n
    } yield {
      val testP = getPoint(i, j)
      val response = console.activate(testP).distance(true)
      if (minP == Point(10000000.0, 10000000.0) || response < minD) {
        minD = response
        minP = testP
      }
    }
    Circle(minP, minD)
  }
}

case class Vector(x: Double, y: Double) {
  def toPoint: Point =
    Point(x, y)

  def +(other: Vector): Vector = {
    Vector(x + other.x, y + other.y)
  }

  def +(other: Point): Vector = {
    Vector(x + other.x, y + other.y)
  }

  def *(scale: Double): Vector = {
    Vector(x * scale, y * scale)
  }

  def /(scale: Double): Vector = {
    Vector(x / scale, y / scale)
  }

  private lazy val lengthP = sqrt(x * x + y * y)

  def length: Double = lengthP

  def ~=(dist: Double): Boolean =
    abs(length - dist) < comparePrecision

  def normalize: Vector =
    this * side / lengthP
}

object IntP {
  def apply(x: Double, y: Double): Point = Point(round(x), round(y))
}

object P {
  def apply(x: Double, y: Double): Point = Point(x, y)
}

case class Point(x: Double, y: Double) {
  def move(ox: Int, oy: Int): Point =
    Point(x + ox, y + oy)

  def +(other: Vector): Vector =
    Vector(x + other.x, y + other.y)

  def -(other: Point): Vector = {
    Vector(x - other.x, y - other.y)
  }
  def ~=(other: Point): Boolean =
    abs(x - other.x) < comparePrecision && abs(y - other.y) < comparePrecision

  def toInt: (Int, Int) =
    (round(x).toInt, round(y).toInt)
}

object C {
  def apply(p: Point, r: Double): Circle = Circle(p, r)
}

case class Circle(center: Point, r: Double) {
  def x: Double =
    center.x
  def y: Double =
    center.y

  def /\(other: Circle): Intersection = {
    val d = (center - other.center).length
    if (d > r + other.r)
      Intersection(Nil)
    else if (d < abs(r - other.r))
      Intersection(Nil)
    else if (d == 0 && r == other.r)
      Intersection(Nil)
    else {
      val a = (r * r - other.r * other.r + d * d) / (2 * d)
      val h = sqrt(r * r - a * a)
      val p2 = (other.center - center) * a / d + center
      val x1 = p2.x + h * (other.y - y) / d
      val y1 = p2.y - h * (other.x - x) / d
      if (h == 0)
        Intersection(Point(x1, y1) :: Nil)
      else {
        val x2 = p2.x - h * (other.y - y) / d
        val y2 = p2.y + h * (other.x - x) / d
        Intersection(Point(x1, y1) :: Point(x2, y2) :: Nil)
      }
    }
  }

  def /\(other: Point): Boolean = {
    abs((other.x - x) * (other.x - x) + (other.y - y) * (other.y - y) - r * r) < comparePrecision
  }
}

case class Intersection(points: List[Point]) {
  def /\(other: Circle): Option[Point] =
    points.find(p ⇒ other /\ p)
}

trait Response {
  def distance(inside: Boolean = false): Double
}

case class Outside(distance: Double) extends Response {
  def distance(inside: Boolean = false): Double = if (inside) 1000000.0 else distance
}

case class Inside(distance: Double) extends Response {
  def distance(inside: Boolean = false): Double = distance
}

case class Blocked() extends Response {
  def distance(inside: Boolean = false): Double = 1000000.0
}

class Console {
  private val reader = new BufferedReader(new InputStreamReader(System.in))

  def getMN: (Int, Int) = {
    val in = readLine.map(_.toInt)
    (in.head, in.last)
  }


  def activate(point: Point): Response = {
    println(f"activate ${point.x}%.9f ${point.y}%.9f")
    val input = readLine
    val header = input.head
    if (header == "inside") {
      Inside(input(1).toDouble)
    } else if (header == "blocked") {
      Blocked()
    } else {
      Outside(input(1).toDouble)
    }
  }

  def submit(x: Int, y: Int): Unit = {
    println(s"found $x $y")
  }

  private def readLine: List[String] = {
    reader.readLine().split("\\s+").toList
  }
}