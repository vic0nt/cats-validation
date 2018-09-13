package util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import io.circe.Decoder.Result
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}
import money.{Currency, Money}

import scala.util.Try

object CirceImplicits {

  implicit val timestampFormat: Encoder[ZonedDateTime] with Decoder[ZonedDateTime] = {
    val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    new Encoder[ZonedDateTime] with Decoder[ZonedDateTime] {
      override def apply(zonedDateTime: ZonedDateTime): Json =
        Encoder.encodeString.apply(zonedDateTime.format(formatter))
      override def apply(hCursor: HCursor): Result[ZonedDateTime] =
        Decoder.decodeString
          .emap { s =>
            Try(ZonedDateTime.parse(s, formatter))
              .toEither
              .left
              .map(throwable => s"Error parse DateTime $s. ${throwable.toString}")
          }
          .apply(hCursor)
    }
  }

  implicit val currencyFormat: Encoder[Currency] with Decoder[Currency] =
    new Encoder[Currency] with Decoder[Currency] {
      override def apply(currency: Currency): Json = Encoder.encodeString.apply(currency.getCode)
      override def apply(hCursor: HCursor): Result[Currency] =
        Decoder.decodeString
          .emap { s =>
            Try(Currency.apply(s)).toEither.left.map(throwable => s"Error parse Currency $s. ${throwable.toString}")
          }
          .apply(hCursor)
    }

  implicit val uuidFormat: Encoder[UUID] with Decoder[UUID] =
    new Encoder[UUID] with Decoder[UUID] {
      override def apply(uuid: UUID): Json = Encoder.encodeString.apply(uuid.toString)
      override def apply(hCursor: HCursor): Result[UUID] =
        Decoder.decodeString
          .emap { s =>
            Try(UUID.fromString(s)).toEither.left.map(throwable => s"Error parse UUID $s. ${throwable.toString}")
          }
          .apply(hCursor)
    }


  implicit val decoderMoney: Decoder[Money] = deriveDecoder
  implicit val encoderMoney: Encoder[Money] = deriveEncoder
}
