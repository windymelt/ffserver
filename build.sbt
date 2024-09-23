import com.typesafe.sbt.packager.docker._

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
    // Use `show dockerCommands` to show current command list
    dockerCommands := {
      val mainStageIndex = dockerCommands.value.tail.indexWhere {
        case Cmd("FROM", _*) => true
        case _               => false
      }
      dockerCommands.value.take(mainStageIndex + 2) ++ Seq(
        ExecCmd("RUN", "yum", "install", "-y", "tar", "xz", "wget"),
      ) ++ dockerCommands.value.drop(mainStageIndex + 2)
    },
    dockerCommands ++= Seq(
      ExecCmd("WORKDIR", "/tmp"),
      ExecCmd(
        "RUN",
        "wget",
        "https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz",
      ),
      ExecCmd("RUN", "tar", "xvf", "ffmpeg-release-amd64-static.tar.xz"),
      Cmd("USER", "root"),
      // output directory sometimes changes according to latest version.
      ExecCmd(
        "RUN",
        "mv",
        "ffmpeg-7.0.2-amd64-static/ffmpeg",
        "/usr/bin/ffmpeg",
      ),
      ExecCmd(
        "RUN",
        "mv",
        "ffmpeg-7.0.2-amd64-static/ffprobe",
        "/usr/bin/ffprobe",
      ),
      ExecCmd(
        "RUN",
        "rm",
        "-rf",
        "ffmpeg-release-amd64-static.tar.xz",
        "ffmpeg-7.0.2-amd64-static/",
      ),
      Cmd("USER", "daemon"),
      ExecCmd("WORKDIR", "/opt/docker"),
    ),
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
