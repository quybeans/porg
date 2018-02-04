// Copyright (C) 2018, Dau Van Quy, All rights reserved.

package com.porg

import java.util.UUID

object UUIDGenerator {

  def generateImageId: String = {
    UUID.randomUUID().toString.split("-").mkString("")
  }
}
