package model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import validation.ValidationContext

import scala.language.higherKinds

case class Budgetary[W[_]](
  statusIndicator: String,
  cbc: W[String],
  oktmo: W[Int],
  payReason: W[String],
  typeBudgetPayment: W[String],
  taxDocNumber: W[Int],
  taxDateDay: W[Int],
  taxDateMonth: W[Int],
  taxDateYear: W[Int],
  taxPeriodDay: W[String],
  taxPeriodMonth: W[String],
  taxPeriodYear: W[Int],
  customsCode: W[Int]
) {
  def pack(implicit ctx: ValidationContext[W]) = Budgetary[Option](
    statusIndicator,
    ctx.toOption(cbc),
    ctx.toOption(oktmo),
    ctx.toOption(payReason),
    ctx.toOption(typeBudgetPayment),
    ctx.toOption(taxDocNumber),
    ctx.toOption(taxDateDay),
    ctx.toOption(taxDateMonth),
    ctx.toOption(taxDateYear),
    ctx.toOption(taxPeriodDay),
    ctx.toOption(taxPeriodMonth),
    ctx.toOption(taxPeriodYear),
    ctx.toOption(customsCode)
  )
}

object Budgetary {
  implicit val decoderId: Decoder[Budgetary[cats.Id]] = deriveDecoder
  implicit val encoderId: Encoder[Budgetary[cats.Id]] = deriveEncoder
  implicit val decoderOption: Decoder[Budgetary[Option]] = deriveDecoder
  implicit val encoderOption: Encoder[Budgetary[Option]] = deriveEncoder
}