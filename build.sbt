
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

lazy val root = (project in file ("."))
  .settings(
    name := "porg",
    description := "An AWS Lambda function for compressing image.",
    version := "0.1",
    scalaVersion := "2.12.4",
    retrieveManaged := true,
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
      "com.amazonaws" % "aws-lambda-java-events" % "2.0.1",
      "com.amazonaws" % "aws-java-sdk-s3" % "1.11.163",

      "com.sksamuel.scrimage" %% "scrimage-core" % "3.0.0-alpha4",
      "com.typesafe.akka" %% "akka-http" % "10.0.11"
    ),
  )
  .settings(
    mainClass in assembly := Some("com.porg.BucketConnector")
  )
