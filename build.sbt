val scala3Version = "3.5.1"

lazy val root = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    name                                   := "ffserver",
    version                                := "0.1.0-SNAPSHOT",
    scalaVersion                           := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    dockerBaseImage                        := "amazoncorretto:23",
    Docker / daemonUserUid                 := None,
    Docker / daemonUser                    := "daemon",
    dockerExposedPorts ++= Seq(8080),
  )
