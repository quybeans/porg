// Copyright (C) 2018, Dau Van Quy, All rights reserved.

package com.porg.aws

final class PorgException(
  val title: String,
  val exception: Exception
) extends Exception(exception.getMessage)
