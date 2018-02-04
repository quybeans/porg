// Copyright (C) 2018, Dau Van Quy, All rights reserved.

package com.porg.aws

import java.io.ByteArrayInputStream
import java.io.InputStream

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.Permission
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.JpegWriter

final class BucketAction(
  bucketName: String
)(implicit
  val client: AmazonS3
) {

  def makeThumbnail(
    key: String,
    height: Int = 320,
    width: Int = 320
  ): Unit = {
    val obj = getObject(key)
    val thumbnailStream = makeThumbnail(obj, height, width)
    val fileName = key.split('/').lastOption.getOrElse("")
    val fileExt = fileName.split('.').lastOption.getOrElse("")
    val name = fileName.split('.').dropRight(1).mkString(".")

    uploadToBucket(s"user/thumbnails/$name.$fileExt", thumbnailStream, makePublic = true)
  }

  private[this] def uploadToBucket(
    key: String,
    stream: InputStream,
    makePublic: Boolean = false
  ): Unit = {
    try {
      var meta = new ObjectMetadata()
      meta.setContentLength(stream.available())

      var acl = new AccessControlList()
      if (makePublic) {
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read)
        acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.FullControl)
      } else {
        acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.FullControl)
      }

      client.putObject(new PutObjectRequest(bucketName, key, stream, meta).withAccessControlList(acl))
    } catch {
      case ex: AmazonServiceException =>
        throw new PorgException(
          "Hey hey, there is an exception while uploading the object to bucket.", ex
        )
    }
  }

  private[this] def makeThumbnail(
    inputStream: S3ObjectInputStream,
    width: Int,
    height: Int
  ): ByteArrayInputStream = {
    try {
      Image.fromStream(inputStream)
        .fit(width, height).stream(JpegWriter().withCompression(50))
    } catch {
      case ex: Exception =>
        throw new PorgException(
          "Guys, there was an exception while compressing image.", ex
        )
    }
  }

  private[this] def makeThumbnail(
    inputStream: S3ObjectInputStream
  ): ByteArrayInputStream = {
    try {
      Image.fromStream(inputStream).stream(JpegWriter().withCompression(50))
    } catch {
      case ex: Exception =>
        throw new PorgException(
          "Guys, there was an exception while compressing image.", ex
        )
    }
  }

  private[this] def getObject(key: String): S3ObjectInputStream = {
    try {
      client.getObject(bucketName, key).getObjectContent
    } catch {
      case ex: AmazonServiceException =>
        throw new PorgException(
          "Yoo, guys there is an error while getting object from AWS: ", ex
        )

      case ex: Exception =>
        throw new PorgException(
          "Shit happens, the Rebellion got us, this is an unknown exception: ", ex
        )
    }
  }
}
