package com.ariskk.flink4s

import org.apache.flink.api.common.typeinfo.TypeInformation

trait TypeInfo:
  given intTypeInfo: TypeInformation[Int]       = TypeInformation.of(classOf[Int])
  given stringTypeInfo: TypeInformation[String] = TypeInformation.of(classOf[String])
end TypeInfo

object TypeInfo extends TypeInfo
