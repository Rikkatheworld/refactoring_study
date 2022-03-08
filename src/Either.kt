sealed class Either<E, out A> {

    /**
     * Either<E, A> -> Either<E, B>
     */
    abstract fun <B> map(f: (A) -> B): Either<E, B>

    /**
     * (A) -> Either<E, B>
     */
    abstract fun <B> flatmap(f: (A) -> Either<E, B>): Either<E, B>

    fun getOrElse(default: () -> @UnsafeVariance A): A = when (this) {
        is Right -> this.value
        is Left -> default()
    }

    fun orElse(default: () -> Either<E, @UnsafeVariance A>): Either<E, A> = map { this }.getOrElse(default)

    /**
     * 错误信息
     */
    internal class Left<E, out A>(internal val value: E) : Either<E, A>() {
        override fun <B> map(f: (A) -> B): Either<E, B> = Left(value)

        override fun <B> flatmap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B> = Left(value)
    }

    /**
     * 正常信息
     */
    internal class Right<E, out A>(internal val value: A) : Either<E, A>() {
        override fun <B> map(f: (A) -> B): Either<E, B> = Right(f(value))

        override fun <B> flatmap(f: (A) -> Either<E, B>): Either<E, B> = f(value)
    }

    companion object {
        fun <E, A> left(value: E): Either<E, A> = Left(value)
        fun <E, A> right(value: A): Either<E, A> = Right(value)
    }
}