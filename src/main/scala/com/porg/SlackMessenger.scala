// Copyright (C) 2018, Dau Van Quy, All rights reserved.

package com.porg

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest

object SlackMessenger {

  val BASE_URL = "YOUR_SLACK_APP_INCOMMING_WEBHOOK_HTTP"

  private[this] def encodeMessage(message: String): String = {
      s"""
      {"text": "$message"}
    """.stripMargin
  }

  def sendNotification(message: String)(implicit system: ActorSystem): Future[Unit] = {
    Http(system).singleRequest(
      HttpRequest(
        HttpMethods.POST,
        BASE_URL,
        entity = HttpEntity(ContentTypes.`application/json`, encodeMessage(message))
      )
    ).map(_ => Unit)
  }
}
