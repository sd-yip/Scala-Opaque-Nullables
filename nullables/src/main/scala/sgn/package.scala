
package object sgn {
  type InherentNullness[+A] = Null <:< A


  type Null = Nullable[Nothing] with Null.Tag

  type NonNull[+A] = Nullable[A] with NonNull.Tag

  type Nullable[+A] = Any with Nullable.Tag


  object Null {
    private[sgn] trait Tag extends Nullable.Tag

    def apply(): Null =
      null

    def unapply(arg: Nullable[_]): Boolean =
      arg == null
  }

  object NonNull {
    private[sgn] trait Tag extends Nullable.Tag

    def apply[A](value: A): NonNull[A] = {
      val r =
        value match {
          case n: LiftedNullness => LiftedNullness(n)
          case null => LiftedNullness(null)
          case _ => value
        }

      r.asInstanceOf[NonNull[A]]
    }

    def unapply[A](value: NonNull[A]): Nullable[A] =
      value
  }

  object Nullable {
    private[sgn] trait Tag extends Any

    def fromInherentNullable[A : InherentNullness](value: A): Nullable[A] =
      value.asInstanceOf[Nullable[A]]

    def toInherentNullable[A : InherentNullness](value: Nullable[A]): A =
      value.asInstanceOf[A]
  }
}
