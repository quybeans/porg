// Copyright (C) 2018, Dau Van Quy, All rights reserved.

package com.porg

import java.net.URLDecoder

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.ActorSystem
import com.amazonaws.services.lambda.runtime.events.S3Event

object Main extends App {

  def decodeS3Key(key: String): String = URLDecoder.decode(key.replace("+", " "), "utf-8")

  def getSourceBuckets(event: S3Event): java.util.List[String] = {
    implicit val system: ActorSystem = ActorSystem("porg")
    val result = event.getRecords.asScala.map(records => decodeS3Key(records.getS3.getObject.getKey)).asJava

    val notificationFuture =  SlackMessenger.sendNotification("I'm your father Luke.")
    Await.result(notificationFuture, 10.seconds)

    result
  }
}
