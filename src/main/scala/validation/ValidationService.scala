package validation

class ValidationService() {

  import ValidationImplicits._
  def getValidatorId: ValidationRules[cats.Id] = new ValidationRules[cats.Id]()
  def getValidatorOption: ValidationRules[Option] = new ValidationRules[Option]()

}
