package validation

import io.circe.generic.JsonCodec

@JsonCodec
case class ValidationCheck(
  checkId: String,
  fields: Seq[String],
  severityLevel: SeverityLevel,
  messageInEnglish: Option[String],
  messageInRussian: Option[String]
)

@JsonCodec
sealed trait SeverityLevel
object SeverityLevel {
  case object Block extends SeverityLevel
  case object Error extends SeverityLevel
  case object Warning extends SeverityLevel
}
