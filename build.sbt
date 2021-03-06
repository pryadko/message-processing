name := "message-procesing"
scalaVersion in ThisBuild := "2.13.2"

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    common,
    serviceA,
    serviceB,
    serviceC
  )

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.http4sClient,
      dependencies.http4sServer,
      dependencies.http4sDsl,
      dependencies.kafkaVersion,
      dependencies.circleParser
    )
  )
  .disablePlugins(AssemblyPlugin)

lazy val serviceA = project
  .settings(
    name := "serviceA",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )

lazy val serviceB = project
  .settings(
    name := "serviceB",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.circleParser,
      dependencies.fs2core,
      dependencies.fs2io
    )
  )
  .dependsOn(
    common
  )

lazy val serviceC = project
  .settings(
    name := "serviceC",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.sparkCore,
      dependencies.sparkKafka,
      dependencies.sparkStreaming,
      dependencies.sparkSql,
      dependencies.sparkMlib,
      dependencies.mongoDb
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val logbackV = "1.2.3"
    val scalaLoggingV = "3.9.2"
    val slf4jV = "1.7.25"
    val typesafeConfigV = "1.3.1"
    val scalatestV = "3.3.0-SNAP2"
    val http4sVersion = "0.21.3"
    val catsEffectV = "2.1.3"
    val circleV = "0.13.0"
    val fs2V = "2.4.2"
    val fs2KafkaVersionV = "0.20.2"
    val sparkV = "1.6.3"
    val mongoV = "0.11.1"

    val logback = "ch.qos.logback" % "logback-classic" % logbackV
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV
    val slf4j = "org.slf4j" % "jcl-over-slf4j" % slf4jV
    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigV
    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
    val http4sDsl = "org.http4s" %% "http4s-dsl" % http4sVersion
    val http4sServer = "org.http4s" %% "http4s-blaze-server" % http4sVersion
    val http4sClient = "org.http4s" %% "http4s-blaze-client" % http4sVersion
    val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectV
    val http4sCircle = "org.http4s" %% "http4s-circe" % http4sVersion
    val circleCore = "io.circe" %% "circe-core" % circleV
    val circleGeneric = "io.circe" %% "circe-generic" % circleV
    val circleLiteral = "io.circe" %% "circe-literal" % circleV
    val circleParser = "io.circe" %% "circe-parser" % circleV
    val fs2core = "co.fs2" %% "fs2-core" % fs2V
    val fs2io = "co.fs2" %% "fs2-io" % fs2V
    val kafkaVersion = "com.ovoenergy" %% "fs2-kafka" % fs2KafkaVersionV
    val sparkKafka = "org.apache.spark" % "spark-streaming-kafka_2.10" % sparkV
    val sparkCore = "org.apache.spark" % "spark-core_2.10" % sparkV
    val sparkStreaming = "org.apache.spark" % "spark-streaming_2.10" % sparkV
    val sparkSql = "org.apache.spark" % "spark-sql_2.10" % sparkV
    val sparkMlib = "org.apache.spark" % "spark-mllib_2.10" % sparkV

    val mongoDb = "com.stratio.datasource" % "spark-mongodb_2.10" % mongoV

  }

lazy val commonDependencies = Seq(
  dependencies.logback,
  dependencies.scalaLogging,
  dependencies.slf4j,
  dependencies.typesafeConfig,
  dependencies.catsEffect,
  dependencies.http4sCircle,
  dependencies.circleCore,
  dependencies.circleGeneric,
  dependencies.circleLiteral,
  dependencies.scalatest % "test"
)

// SETTINGS

lazy val settings =
  commonSettings ++
    wartremoverSettings ++
    scalafmtSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x                             => MergeStrategy.first
  }
)
