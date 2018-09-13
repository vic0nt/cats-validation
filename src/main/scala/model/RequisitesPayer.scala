package model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import validation.ValidationContext

import scala.language.higherKinds

case class RequisitesPayer[W[_]](
  inn: W[String],
  kpp: Option[String],
  account: W[String],
  name: W[String],
  bic: W[String],
  bankName: W[String],
  bankCorrespondentAccount: W[String],
  bankSettlementType: W[String],
  bankCity: W[String]
) {
  def pack(implicit ctx: ValidationContext[W]) = RequisitesPayer[Option](
    ctx.toOption(inn),
    kpp,
    ctx.toOption(account),
    ctx.toOption(name),
    ctx.toOption(bic),
    ctx.toOption(bankName),
    ctx.toOption(bankCorrespondentAccount),
    ctx.toOption(bankSettlementType),
    ctx.toOption(bankCity)
  )
}

object RequisitesPayer {
  implicit val decoderId: Decoder[RequisitesPayer[cats.Id]] = deriveDecoder
  implicit val encoderId: Encoder[RequisitesPayer[cats.Id]] = deriveEncoder
  implicit val decoderOption: Decoder[RequisitesPayer[Option]] = deriveDecoder
  implicit val encoderOption: Encoder[RequisitesPayer[Option]] = deriveEncoder

  val empty: RequisitesPayer[Option] = RequisitesPayer[Option](None, None, None, None, None, None, None, None, None)
}
