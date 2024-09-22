val scala3Version = "3.5.1"
val http4sVersion = "0.23.28"
val tapirVersion  = "1.11.4"

lazy val root = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    name                   := "ffserver",
    version                := "0.1.0-SNAPSHOT",
    scalaVersion           := scala3Version,
    fork                   := true,
    dockerBaseImage        := "amazoncorretto:23",
    Docker / daemonUserUid := None,
    Docker / daemonUser    := "daemon",
    dockerExposedPorts ++= Seq(8080),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core"          % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % tapirVersion,
    ),
    libraryDependencies ++= Seq(
      // "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
    ),
    libraryDependencies ++= Seq(
      "com.lihaoyi"         %% "os-lib" % "0.10.7",
      "io.github.windymelt" %% "qw"     % "0.1.5",
    ),
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
  )
