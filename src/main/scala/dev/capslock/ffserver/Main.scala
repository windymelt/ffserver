package dev.capslock.ffserver

import cats.effect.IOApp
import cats.effect.IO
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import cats.syntax.all.{*, given}

object Main extends IOApp.Simple {
  def run: cats.effect.IO[Unit] = for {
    _ <- cats.effect.IO(println("Starting Server"))
    _ <- server.useForever
  } yield ()

  def index = IO.pure("Hello, world!")
  val indexRoute = Http4sServerInterpreter[IO]().toRoutes(
    Endpoints.index.serverLogicSuccess(_ => index),
  )

  def convert(req: Endpoints.ConvertRequest) = {
    val path  = req.input
    val input = os.pwd / os.RelPath(path)
    FFMpeg
      .convert(input)
      .map { output =>
        Endpoints.ConvertResponse(output.toString)
      }
      .attempt
      .map(_.left.map(e => Endpoints.ErrorResponse(e.getMessage)))
  }
  val convertRoute = Http4sServerInterpreter[IO]().toRoutes(
    Endpoints.convert.serverLogic(convert),
  )

  def server = EmberServerBuilder
    .default[IO]
    .withHost(host"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp((indexRoute <+> convertRoute).orNotFound)
    .build
}
