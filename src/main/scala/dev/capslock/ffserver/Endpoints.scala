package dev.capslock.ffserver

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import io.circe.generic.auto.*

import sttp.tapir.EndpointIO.annotations.jsonbody

object Endpoints {
  case class ErrorResponse(reason: String)
  case class ConvertRequest(input: String)
  case class ConvertResponse(output: String)
  val basicEndpoint = endpoint.errorOut(jsonBody[ErrorResponse])

  val index = basicEndpoint.get
    .in("index")
    .out(jsonBody[String])
  val convert = basicEndpoint.post
    .in("convert")
    .in(jsonBody[ConvertRequest])
    .out(jsonBody[ConvertResponse])
}
