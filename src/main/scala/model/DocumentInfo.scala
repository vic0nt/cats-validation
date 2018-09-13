package model

import java.time.ZonedDateTime

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import validation.ValidationContext
import util.CirceImplicits._

import scala.language.higherKinds

case class DocumentInfo[W[_]](
  number: W[String],
  date: W[ZonedDateTime],
  note: W[String],
  linkedDocuments: Seq[String]
) {
  def pack(implicit ctx: ValidationContext[W]) = DocumentInfo[Option](
    ctx.toOption(number),
    ctx.toOption(date),
    ctx.toOption(note),
    linkedDocuments
  )
}

object DocumentInfo {
  implicit val decoderId: Decoder[DocumentInfo[cats.Id]] = deriveDecoder
  implicit val encoderId: Encoder[DocumentInfo[cats.Id]] = deriveEncoder
  implicit val decoderOption: Decoder[DocumentInfo[Option]] = deriveDecoder
  implicit val encoderOption: Encoder[DocumentInfo[Option]] = deriveEncoder

  val empty: DocumentInfo[Option] = DocumentInfo[Option](None, None, None, Nil)
}
