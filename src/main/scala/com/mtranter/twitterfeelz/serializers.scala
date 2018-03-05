package com.mtranter.twitterfeelz

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.{lang, util}

import com.sksamuel.avro4s._
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}


trait Serializers {
  def serdeFor[T : SchemaFor : ToRecord : FromRecord]: Serde[T] =
    Serdes.serdeFrom(AvroSerializer[T](), AvroDeserializer[T]())

  val stringSerde: Serde[String] = Serdes.String()
  val intSerde: Serde[Int] = Serdes.Integer().asInstanceOf[Serde[Int]]
  val doubleSerde: Serde[Double] = Serdes.Double().asInstanceOf[Serde[Double]]
}

object AvroSerializer {
  def apply[T : SchemaFor : ToRecord](): AvroSerializer[T] = new AvroSerializer[T]()
}

class AvroSerializer[T : SchemaFor : ToRecord] extends Serializer[T] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  override def serialize(topic: String, data: T): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val output = AvroOutputStream.binary[T](baos)
    output.write(data)
    output.close()
    baos.toByteArray
  }

  override def close(): Unit = ()
}

object AvroDeserializer {
  def apply[T : SchemaFor : FromRecord](): AvroDeserializer[T] = new AvroDeserializer[T]()
}

class AvroDeserializer[T : SchemaFor : FromRecord] extends Deserializer[T] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  override def close(): Unit = ()

  override def deserialize(topic: String, data: Array[Byte]): T = {
    if(data == null) {
      return null.asInstanceOf[T]
    }
    val in = new ByteArrayInputStream(data)
    val input = AvroInputStream.binary[T](in)
    input.iterator.toSeq.head
  }
}