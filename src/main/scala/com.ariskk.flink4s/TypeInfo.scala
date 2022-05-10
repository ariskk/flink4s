package com.ariskk.flink4s

import org.apache.flink.api.common.typeinfo.TypeInformation

trait TypeInfo {
  implicit val intTypeInfo: TypeInformation[Int]       = TypeInformation.of(classOf[Int])
  implicit val stringTypeInfo: TypeInformation[String] = TypeInformation.of(classOf[String])
}

object TypeInfo extends TypeInfo
