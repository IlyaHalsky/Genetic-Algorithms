/*
import java.io.{BufferedReader, InputStreamReader}

import scala.annotation.tailrec
import scala.collection.immutable
import scala.math._

object E extends App {

  def findTreasure(start: Circle,console: Console): (Int, Int) = {
    def getCircle(point: Point): Circle =
      C(point, console.activate(point, true))

    val center = start.center
    val left = getCircle(center.move(-1, 0))
    val top = getCircle(center.move(0, 1))
    val horizontal = left /\ start /\ top
    horizontal.get.toInt
  }

  def findLabyrinth(mn: (Int, Int), console: Console): Labyrinth = {
    val circles: immutable.Seq[Circle] =
      for {
        angle ← Range(0, 360, 12)
        rad = toRadians(angle)
      } yield {
        val searchPoint = IntP(9000.0d * cos(rad), 9000.0d * sin(rad))
        C(searchPoint, console.activate(searchPoint))
      }
    val points = for {
      i ← 0 until (circles.length-3)
    } yield {
      circles(i) /\ circles(i+1) /\ circles(i+2)
    }
    val labPoints = findUnique(points.flatten.toList)
    //assert(labPoints.size >= 3)
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

  val console: Console = new Console
  val mn = console.getMN
  val lab: Labyrinth = findLabyrinth(mn,console)
  val best = lab.findBest(console)
  /*if (best.r > 10.0)
    while (true) Thread.sleep(10)*/
  val (ax, ay) = findTreasure(best,console)
  console.submit(ax, ay)
}

case class Labyrinth(points: List[Point], mn: (Int, Int)) {
  private val (m, n) = mn
  private val (a, b, c) = {
    val a :: rest = points
    val b = rest.find(p ⇒ (p - a) ~= m * 10).get // Can fail
    val c = rest.find(p ⇒ (p - b) ~= n * 10).get // Can fail #2
    (a, b, c)
  }

  private val cbi: Vector = (c - b).normalize
  private val abi: Vector = (a - b).normalize

  private def getPoint(i: Int, j: Int): Point =
    (b + abi * (i + 0.5) + cbi * (j + 0.5)).toPoint

  def findBest(console: Console): Circle = {
    var minP: Point = Point(10000000.0, 10000000.0)
    var minD = 10000000.0
    for {
      i ← 0 until m
      j ← 0 until n
    } yield {
      val testP = getPoint(i, j)
      val response = console.activate(testP, true)
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
    abs(length - dist) < 0.5d

  def normalize: Vector =
    this * 10 / lengthP
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
    abs(x - other.x) < 0.5d && abs(y - other.y) < 0.5d

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
    abs((other.x - x) * (other.x - x) + (other.y - y) * (other.y - y) - r * r) < 0.5d
  }
}

case class Intersection(points: List[Point]) {
  def /\(other: Circle): Option[Point] =
    points.find(p ⇒ other /\ p)
}

class Console {
  private val reader = new BufferedReader(new InputStreamReader(System.in))

  def getMN: (Int, Int) = {
    val in = readLine
    (in(0).toInt, in(1).toInt)
  }


  def activate(point: Point, inside: Boolean = false): Double = {
    println(f"activate ${point.x}%.9f ${point.y}%.9f")
    val input = readLine
    val header = input(0)
    if (header == "inside") {
      input(1).toDouble
    } else if (header == "blocked") {
      1000000.0
    } else {
      if (inside) {
        1000000.0
      } else {
        input(1).toDouble
      }
    }
  }

  def submit(x: Int, y: Int): Unit = {
    println(s"found $x $y")
  }

  private def readLine: Array[String] = {
    reader.readLine().split("\\s+")
  }
}*/
