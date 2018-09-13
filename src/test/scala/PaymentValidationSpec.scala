import java.time.ZonedDateTime
import java.util.UUID

import cats.Id
import cats.data.Ior
import io.circe.Encoder
import io.circe.parser._
import model._
import org.scalatest.{MustMatchers, WordSpec}
import util.CirceImplicits._
import validation.{ValidationRules, ValidationService}

class PaymentValidationSpec extends WordSpec with MustMatchers {

  val validationService = new ValidationService

  implicit val validatorOption: ValidationRules[Option] = validationService.getValidatorOption
  implicit val validatorId: ValidationRules[Id] = validationService.getValidatorId


  "PaymentValidation for draft" must {

    "be correctly parsed from json to Option wrapper" in {

      val parseResult = parse(rawJsonInvalid)
      parseResult
        .flatMap(_.as[Document[Option]])
        .fold(failed => fail("Errors: " + failed), _.unpack mustBe a[Ior.Left[_]])
    }

    "be able to unpack correct common document" in {
      val parseResult = parse(rawJsonCommonValid)
      parseResult
        .flatMap(_.as[Document[Option]])
        .fold(failed => fail("Errors: " + failed), _.unpack mustBe Ior.Right(validCommonDocument))
    }

  }

  val now: ZonedDateTime = ZonedDateTime.now
  val nowString: String = Encoder[ZonedDateTime].apply(now).noSpaces


  val rawJsonCommonValid: String =
    s"""
      {
        "id": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "status": "valid",
        "organizationId": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "userId": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "accountId": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "documentInfo": {
          "number": "123456",
          "date": $nowString,
          "note": "string",
          "linkedDocuments": []
        },
        "paymentCode": 1,
        "paymentKind": "kind 1",
        "vatSum": 0,
        "vatRate": 0,
        "vatCalculationRule": "string",
        "paymentPurpose": "string",
        "operationType": "01",
        "paymentPriority": 3,
        "currencyOperationType": 0,
        "documentSum": 15000,
        "uip": null,
        "payer": {
          "inn": "012345678912",
          "kpp": "string",
          "account": "40817810099910004312",
          "name": "string",
          "bic": "123456789",
          "bankName": "string",
          "bankCorrespondentAccount": "0",
          "bankSettlementType": "string",
          "bankCity": "string"
        },
        "receiver": {
          "inn": "012345678912",
          "kpp": "string",
          "account": "40817810099910004313",
          "name": "string",
          "bic": "123456789",
          "bankName": "string",
          "bankCorrespondentAccount": "0",
          "bankSettlementType": "string",
          "bankCity": "string"
        },
        "process": {
          "status": "valid"
        }
      }
      """

  val rawJsonInvalid: String =
    s"""
      {
        "id": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "status": "invalid",
        "organizationId": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "userId": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "accountId": "234ccaee-c2dd-401c-b91e-348ad5c26d09",
        "documentInfo": {
          "number": null,
          "date": $nowString,
          "note": "string",
          "linkedDocuments": []
        },
        "paymentCode": 1,
        "paymentKind": "kind 1",
        "vatSum": 0,
        "vatRate": 0,
        "vatCalculationRule": "string",
        "paymentPurpose": "string",
        "operationType": "01",
        "paymentPriority": 1,
        "currencyOperationType": 0,
        "documentSum": 15000,
        "process": {
          "status": "valid"
        }
      }
    """

  val validCommonDocument: Document[Id] = Document[Id](
    id = Document.Id(UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09")),
    organizationId = UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09"),
    userId = UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09"),
    accountId = UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09"),
    documentInfo = DocumentInfo[Id](
      number = "123456",
      date = now,
      note = "string",
      linkedDocuments = Nil
    ),
    documentSum = BigDecimal.valueOf(15000),
    uip = None,
    operationType = "01",
    currencyOperationType = 0,
    paymentCode = 1,
    paymentKind = "kind 1",
    paymentPriority = 3,
    paymentPurpose = "string",
    payer = RequisitesPayer[Id](
      inn = "012345678912",
      kpp = Some("string"),
      account = "40817810099910004312",
      name = "string",
      bic = "123456789",
      bankName = "string",
      bankCorrespondentAccount = "0",
      bankSettlementType = "string",
      bankCity = "string"
    ),
    receiver = RequisitesReceiver[Id](
      inn = Some("012345678912"),
      kpp = Some("string"),
      account = Some("40817810099910004313"),
      name = "string",
      bic = "123456789",
      bankName = "string",
      bankCorrespondentAccount = "0",
      bankSettlementType = "string",
      bankCity = "string"
    ),
    vatCalculationRule = "string",
    vatRate = 0,
    vatSum = 0,
    budgetary = None
  )

  val invalidDocument: Document[Id] = Document[Id](
    id = Document.Id(UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09")),
    organizationId = UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09"),
    userId = UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09"),
    accountId = UUID.fromString("234ccaee-c2dd-401c-b91e-348ad5c26d09"),
    documentInfo = DocumentInfo[Id](
      number = "1123456",
      date = now,
      note = "string",
      linkedDocuments = Nil
    ),
    documentSum = BigDecimal.valueOf(15000),
    uip = None,
    operationType = "01",
    currencyOperationType = 0,
    paymentCode = 6,
    paymentKind = "kind 1",
    paymentPriority = 3,
    paymentPurpose = "string",
    payer = RequisitesPayer[Id](
      inn = "012345678912",
      kpp = Some("string"),
      account = "40817810099910004312",
      name = "string",
      bic = "123456789",
      bankName = "string",
      bankCorrespondentAccount = "0",
      bankSettlementType = "string",
      bankCity = "string"
    ),
    receiver = RequisitesReceiver[Id](
      inn = Some("012345678912"),
      kpp = Some("string"),
      account = Some("40817810099910004313"),
      name = "string",
      bic = "123456789",
      bankName = "string",
      bankCorrespondentAccount = "0",
      bankSettlementType = "string",
      bankCity = "string"
    ),
    vatCalculationRule = "string",
    vatRate = 0,
    vatSum = 0,
    budgetary = Some(Budgetary[Id]("0", "string", 0, "string", "string", 0, 0, 0, 0, "string", "string", 0, 0))
  )
}
