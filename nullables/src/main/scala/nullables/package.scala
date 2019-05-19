import nullables.ops.NullableOps

package object nullables {
  type InherentNullness[+A] = scala.Null <:< A


  type Null = Nullable.Base with Null.Tag

  type NonNull[+A] = Nullable.Base with NonNull.Tag

  type Nullable[+A] = Nullable.Base with Nullable.Tag


  object Null {
    private[nullables] trait Tag extends Nullable.Tag

    def apply(): Null =
      null

    def unapply(arg: Nullable[_]): Boolean =
      arg == null
  }

  object NonNull {
    private[nullables] trait Tag extends Nullable.Tag

    def apply[A](value: A): NonNull[A] = {
      val r =
        value match {
          case n: LiftedNull => LiftedNull(n)
          case null => LiftedNull(null)
          case _ => value
        }

      r.asInstanceOf[NonNull[A]]
    }

    def unapply[A](value: Nullable[A]): NullableOps[A] =
      value
  }

  object Nullable {
    private[nullables] type Base = Any { type Tag }
    private[nullables] trait Tag extends Any

    def fromInherentNullable[A : InherentNullness](value: A): Nullable[A] =
      value.asInstanceOf[Nullable[A]]

    def fromOption[A](option: Option[A]): Nullable[A] =
      option match {
        case Some(a) => NonNull(a)
        case None => Null()
      }

    implicit def toNullableOps[A](value: Nullable[A]): NullableOps[A] =
      new NullableOps[A](value)
  }
}
