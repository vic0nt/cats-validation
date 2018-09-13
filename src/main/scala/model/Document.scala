package model

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}
import validation.{ValidationContext, ValidationRules}
import validation.ValidationRules.Result

import scala.language.higherKinds


case class Document[W[_]](
  id: Document.Id,

  organizationId: W[UUID],
  userId: W[UUID],
  accountId: W[UUID],

  documentInfo: W[DocumentInfo[W]],
  operationType: W[String],
  currencyOperationType: W[Int],

  paymentCode: W[Int],
  paymentKind: W[String],
  paymentPurpose: W[String],
  paymentPriority: W[Int],

  vatSum: W[BigDecimal],
  vatRate: W[BigDecimal],
  vatCalculationRule: W[String],

  documentSum: W[BigDecimal],
  uip: Option[String],

  payer: W[RequisitesPayer[W]],
  receiver: W[RequisitesReceiver[W]]

) {

  def unpack(implicit v: ValidationRules[W]): Result[Document[cats.Id]] = v.all(this)

  def pack(implicit ctx: ValidationContext[W]): Document[Option] = Document[Option](
    id,
    ctx.toOption(organizationId),
    ctx.toOption(userId),
    ctx.toOption(accountId),
    ctx.toOption(documentInfo).map(_.pack),
    ctx.toOption(operationType),
    ctx.toOption(currencyOperationType),
    ctx.toOption(paymentCode),
    ctx.toOption(paymentKind),
    ctx.toOption(paymentPurpose),
    ctx.toOption(paymentPriority),
    ctx.toOption(vatSum),
    ctx.toOption(vatRate),
    ctx.toOption(vatCalculationRule),
    ctx.toOption(documentSum),
    uip,
    ctx.toOption(payer).map(_.pack),
    ctx.toOption(receiver).map(_.pack)
  )
}

object Document {

  implicit val decoderId: Decoder[Document[cats.Id]] = deriveDecoder
  implicit val encoderId: Encoder[Document[cats.Id]] = deriveEncoder
  implicit val decoderOption: Decoder[Document[Option]] = deriveDecoder
  implicit val encoderOption: Encoder[Document[Option]] = deriveEncoder

  val FieldsForSort = Map(
    "id" -> "id",
    "operationType" -> "operation_type"
  )

  def empty(
    id: Document.Id
  ): Document[Option] = Document[Option](
    id,
    None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None
  )

  /*** Id ***/
  case class Id(value: UUID)

  object Id extends UUIDWrapperCompanion[Id] {
    override protected def create(value: UUID): Id = Id(value)

    implicit val format: Encoder[Id] with Decoder[Id] =
      new Encoder[Id] with Decoder[Id] {
        override def apply(id: Id): Json = Encoder.encodeString.apply(id.value.toString)
        override def apply(hCursor: HCursor): Decoder.Result[Id] =
          Decoder.decodeString
          .emap { s =>
            Id.fromString(s).toEither.left.map(throwable => s"Error parse Id $s. ${throwable.toString}")
          }
          .apply(hCursor)
      }
  }

}
