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
import com.porg.aws.PorgException

final class SlackMessenger(
  url: String
)(implicit
  val system: ActorSystem
) {

  val BASE_URL = "https://hooks.slack.com/services/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"

  private[this] def encodeMessage(message: String): String = {
      s"""
      {"text": "$message"}
    """.stripMargin
  }

  private[this] def encodeException(message: PorgException): String = {
    s"""
      |{
      |  "text": "${message.title}\n`${message.exception.getMessage}`",
      |  "attachments": [
      |     {
      |       "text": "${message.exception.getStackTrace.mkString("\n")}"
      |     }
      |  ]
      |}
    """.stripMargin
  }

  def sendNotification(message: String): Future[Unit] = {
    Http(system).singleRequest(
      HttpRequest(
        HttpMethods.POST,
        url,
        entity = HttpEntity(ContentTypes.`application/json`, encodeMessage(message))
      )
    ).map(_ => Unit)
  }

  def sendException(message: PorgException): Future[Unit] = {
    Http(system).singleRequest(
      HttpRequest(
        HttpMethods.POST,
        url,
        entity = HttpEntity(ContentTypes.`application/json`, encodeException(message))
      )
    ).map(_ => Unit)
  }
}
