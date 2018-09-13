package validation

import cats.arrow.FunctionK
import cats.data.Ior
import cats.{Id, ~>}
import ValidationRules.{BusinessError, Result}

object ValidationImplicits {

  implicit val optValidation: ValidationContext[Option] = new ValidationContext[Option] {
    override def withinContext[A, B](a: Option[A])(f: A => Result[B], checkId: String): Result[B] = a match {
      case Some(n) => f(n)
      case None => Ior.leftNel(BusinessError(checkId))
    }
    override def toOption: FunctionK[Option, Option] = λ[Option ~> Option](identity(_))
    override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa.flatMap(f)
    override def map[T, B](fa: Option[T])(f: T => B): Option[B] = fa.map(f)

  }

  implicit val idValidation: ValidationContext[Id] = new ValidationContext[Id] {
    override def withinContext[A, B](a: Id[A])(f: A => Result[B], checkId: String): Result[B] = f(a)
    override def toOption: FunctionK[Id, Option] = λ[Id ~> Option](Some(_))
    override def flatMap[A, B](a: A)(f: A => B): B = f(a)
    override def map[T, B](fa: Id[T])(f: T => B): Id[B] = f(fa)
  }

/*  implicit class ValidatedOps[E : Semigroup, A](val v: Validated[E, A]) {

    /**
      * Takes two validated and returns first if both valid
      * combines them as semigroup instances otherwise
      */
    def ~(that: Validated[E, A]): Validated[E, A] =
      (v, that) match {
        case (Valid(a), Valid(_)) => Valid(a)
        case (Valid(_), i @ Invalid(_)) => i
        case (i @ Invalid(_), Valid(_)) => i
        case (Invalid(e1), Invalid(e2)) => Invalid(Semigroup[E].combine(e1, e2))
      }
  }*/

}
