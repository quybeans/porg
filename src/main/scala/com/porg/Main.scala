// Copyright (C) 2018, Dau Van Quy, All rights reserved.

package com.porg

import java.net.URLDecoder

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.ActorSystem
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import scala.util.Properties.envOrNone
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.porg.aws.BucketAction
import com.porg.aws.PorgException

object Main extends App {

  def decodeS3Key(key: String): String = URLDecoder.decode(key.replace("+", " "), "utf-8")

  def makeThumbnailFromEvent(event: S3Event): Unit = {
    implicit val system: ActorSystem = ActorSystem("porg")

    val slackUrl = envOrNone("SLACK_INCOMING_WEBHOOK").getOrElse("")
    val debuggerClient = new SlackMessenger(slackUrl)

    try {
      val bucket = envOrNone("DEFAULT_BUCKET")
        .getOrElse(throw new Exception("Please tell the bucket name."))
      val regionString = envOrNone("REGION")
        .getOrElse(throw new Exception("Please tell the bucket's region."))

      implicit val awsClient: AmazonS3 = AmazonS3ClientBuilder
        .standard()
        .withCredentials(new EnvironmentVariableCredentialsProvider)
        .withRegion(regionString)
        .build()

      val bucketActor = new BucketAction(bucket)

      event.getRecords.asScala.toList.foreach { record =>
        val key = decodeS3Key(record.getS3.getObject.getKey)
        bucketActor.makeThumbnail(key)
      }

      Await.result(debuggerClient.sendNotification(
        "Generate thumbnail successfully."
      ), 5.seconds)
    } catch {

      case ex: PorgException =>
        Await.result(debuggerClient.sendException(ex), 5.seconds)

      case ex: Exception =>
        Await.result(debuggerClient.sendException(new PorgException(
          "Serious shit happens, I don't know what exception is this. Your code is trash.",
          ex
        )), 5.seconds)
    }
  }
}
