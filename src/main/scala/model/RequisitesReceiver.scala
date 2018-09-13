package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import validation.ValidationContext

import scala.language.higherKinds

case class RequisitesReceiver[W[_]](
  inn: Option[String],
  kpp: Option[String],
  account: Option[String],
  name: W[String],
  bic: W[String],
  bankName: W[String],
  bankCorrespondentAccount: W[String],
  bankSettlementType: W[String],
  bankCity: W[String]
) {
  def pack(implicit ctx: ValidationContext[W]) = RequisitesReceiver[Option](
    inn,
    kpp,
    account,
    ctx.toOption(name),
    ctx.toOption(bic),
    ctx.toOption(bankName),
    ctx.toOption(bankCorrespondentAccount),
    ctx.toOption(bankSettlementType),
    ctx.toOption(bankCity)
  )
}

object RequisitesReceiver {
  implicit val decoderId: Decoder[RequisitesReceiver[cats.Id]] = deriveDecoder
  implicit val encoderId: Encoder[RequisitesReceiver[cats.Id]] = deriveEncoder
  implicit val decoderOption: Decoder[RequisitesReceiver[Option]] = deriveDecoder
  implicit val encoderOption: Encoder[RequisitesReceiver[Option]] = deriveEncoder

  val empty: RequisitesReceiver[Option] = RequisitesReceiver[Option](None, None, None, None, None, None, None, None, None)
}

