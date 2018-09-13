package validation

import cats.~>
import ValidationRules.Result

import scala.language.higherKinds

trait ValidationContext[F[_]] {
  def withinContext[T,B](a: F[T])(f: T => Result[B], checkId: String): Result[B]
  def toOption: F ~> Option
  def flatMap[T, B](fa: F[T])(f: T => F[B]): F[B]
  def map[T, B](fa: F[T])(f: T ⇒ B): F[B]
}

object ValidationContext {
  implicit class ValidationOps[F[_],  T, B](val v: F[T]) extends AnyVal {
    def withinContext(f: T => Result[B], checkId: String)(implicit ctx: ValidationContext[F]): Result[B] =
      ctx.withinContext[T,B](v)(f, checkId)

    def flatMap[B](f: T => F[B])(implicit ctx: ValidationContext[F]): F[B] = ctx.flatMap(v)(f)
    def map[B](f: T ⇒ B)(implicit ctx: ValidationContext[F]): F[B] = ctx.map(v)(f)
  }
}
