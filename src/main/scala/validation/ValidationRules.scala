package validation

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZonedDateTime}

import cats.Id
import cats.data.{Ior, IorNel, NonEmptyList => Nel}
import cats.implicits._
import io.circe.generic.JsonCodec
import model._

import scala.language.{higherKinds, implicitConversions}

class ValidationRules[W[_] : ValidationContext] private[validation]() {

  type JBigDecimal = java.math.BigDecimal

  import ValidationContext._
  import ValidationRules._

  val patternTextFields = "^[0-9A-Za-z.!#\"'$%&() \\t*\\+,\\/\\-\\u0410-\\u044F\\u0401\\u0451]*$"
  val patternDigitsOnly = "-?[0-9]+"

  def all(doc: Document[W]): Result[Document[Id]] =
    (
      accept(doc.id),
      nonEmpty(doc.organizationId),
      nonEmpty(doc.userId),
      nonEmpty(doc.accountId),
      validateDocumentInfo(doc),
      nonEmpty(doc.operationType),
      nonEmpty(doc.currencyOperationType),
      nonEmpty(doc.paymentCode),
      nonEmpty(doc.paymentKind),
      nonEmpty(doc.paymentPurpose),
      nonEmpty(doc.paymentPriority),
      nonEmpty(doc.vatSum),
      nonEmpty(doc.vatRate),
      nonEmpty(doc.vatCalculationRule),
      nonEmpty(doc.documentSum),
      accept(doc.uip),
      validateRequisitesPayer(doc),
      validateRequisitesReceiver(doc)
    ).mapN(Document[Id])

  private def validate[T](value: T)(onError: ⇒ BusinessError)(condition: ⇒ Boolean): Result[T] =
    if (condition) value.rightIor else Ior.both(Nel.one(onError), value)

  private def nonEmpty[T](value: W[T], checkId: String = "undefined validation error"): Result[T] =
    value.withinContext(_.rightIor, checkId)


  private def validateRequisitesPayer(doc: Document[W]): Result[RequisitesPayer[Id]] = {

    def payerAccountDiffers(doc: Document[W]): Result[Document[W]] = {
      val accounts: W[(String, String)] = for {
        p ← doc.payer
        r ← doc.receiver
        aP ← p.account
        aR ← r.account
      } yield (aP, aR)
      accounts.withinContext(acc ⇒ if (acc._1 != acc._2) doc.rightIor else Ior.both(Nel.one(BusinessError("2.4.12")), doc), "2.4.12" )
    }

    def payerAccountIsNotCorresp(doc: Document[W]): Result[String] = {
      val acc = for {
        p ← doc.payer
        a ← p.account
      } yield a
      acc.withinContext(a ⇒ validate(a)(BusinessError("2.4.13"))(a.substring(0, 5) != "30101"), "2.4.13")
    }

    nonEmpty(doc.payer).flatMap { payer =>
      (
        nonEmpty(payer.inn),
        accept(payer.kpp),


        //TODO payerAccountDiffers(doc) andThen payerAccountIsNotCorresp
        nonEmpty(payer.account),

        nonEmpty(payer.name, "2.3.1"),
        nonEmpty(payer.bic, "2.6.1"),
        nonEmpty(payer.bankName, "15"),
        nonEmpty(payer.bankCorrespondentAccount, "10"),
        nonEmpty(payer.bankSettlementType, "17"),
        nonEmpty(payer.bankCity, "16")
      ).mapN(RequisitesPayer[Id])
    }
  }

  private def validateDocumentInfo(doc: Document[W]): Result[DocumentInfo[Id]] =
    nonEmpty(doc.documentInfo).flatMap { documentInfo =>
      (
        nonEmpty(documentInfo.number),
        nonEmpty(documentInfo.date),
        nonEmpty(documentInfo.note),
        accept(documentInfo.linkedDocuments)
      ).mapN(DocumentInfo[Id])
    }

  private def validateRequisitesReceiver(doc: Document[W]): Result[RequisitesReceiver[Id]] = {
    nonEmpty(doc.receiver).flatMap { receiver =>
      (
        accept(receiver.inn),
        accept(receiver.kpp),
        accept(receiver.account),
        nonEmpty(receiver.name, "3.3.1"),
        nonEmpty(receiver.bic, "3.6.1"),
        nonEmpty(receiver.bankName),
        nonEmpty(receiver.bankCorrespondentAccount),
        nonEmpty(receiver.bankSettlementType),
        nonEmpty(receiver.bankCity, "18")
      ).mapN(RequisitesReceiver[Id])
    }
  }


}

object ValidationRules {

  type Result[T] = IorNel[BusinessError, T]
  type BusinessValidation[T] = T ⇒ Result[T]

  @JsonCodec case class BusinessError(checkId: String, params: List[String] = List.empty)

  private def accept[T]: BusinessValidation[T] = value ⇒ value.rightIor

  val df: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  implicit def zdfToLocal(zdf: ZonedDateTime): LocalDate = zdf.toLocalDate

  implicit def intToJBigDecimal(i: Int): java.math.BigDecimal = java.math.BigDecimal.valueOf(i)

}
