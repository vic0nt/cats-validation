package model

import java.util.UUID

import scala.util.Try

trait UUIDWrapperCompanion[T] {

  def random(): T = create(UUID.randomUUID())

  def fromString(str: String): Try[T] =
    for (uuid <- Try(UUID.fromString(str)))
      yield create(uuid)

  protected def create(value: UUID): T
}