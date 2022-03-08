import java.io.IOException
import java.io.Serializable

sealed class Result<out A> : Serializable {

    abstract fun <B> map(f: (A) -> B): Result<B>
    abstract fun <B> flatMap(f: (A) -> Result<B>): Result<B>

    internal class Success<out A>(internal val data: A) : Result<A>() {

        override fun <B> map(f: (A) -> B): Result<B> = try {
            Success(f(data))
        } catch (e: RuntimeException) {
            Failure(e)
        } catch (e: Exception) {
            Failure(RuntimeException(e))
        }

        override fun <B> flatMap(f: (A) -> Result<B>): Result<B> = try {
            f(data)
        } catch (e: RuntimeException) {
            Failure(e)
        } catch (e: Exception) {
            Failure(RuntimeException(e))
        }
    }

    internal object Empty : Result<Nothing>() {
        override fun <B> map(f: (Nothing) -> B): Result<B> = Empty

        override fun <B> flatMap(f: (Nothing) -> Result<B>): Result<B> = Empty
    }

    internal class Failure(val exception: RuntimeException) : Result<Nothing>() {
        override fun <B> map(f: (Nothing) -> B): Result<B> = Failure(exception)

        override fun <B> flatMap(f: (Nothing) -> Result<B>): Result<B> = Failure(exception)
    }

    /**
     * 没有 / 错误 返回一个 default, 不能为空, 如果需要空, 使用 [getOrNull]
     */
    fun getOrElse(defaultValue: () -> @UnsafeVariance A): A = when (this) {
        is Success -> this.data
        else -> defaultValue()
    }

    /**
     * 没有 / 错误 返回一个 Result-default, 不能为空
     */
    fun orElse(defaultValue: () -> Result<@UnsafeVariance A>): Result<A> = when (this) {
        is Success -> this
        else -> try {
            defaultValue()
        } catch (e: RuntimeException) {
            Result.failure<A>(e)
        } catch (e: Exception) {
            Result.failure<A>(RuntimeException(e))
        }
    }

    companion object {
        operator fun <A> invoke(a: A? = null): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> Success(a)
        }

        operator fun <A> invoke(): Result<A> = Empty

        fun <A> failure(message: String): Result<A> = Failure(IllegalStateException(message))

        fun <A> failure(exception: RuntimeException): Result<A> = Failure(exception)

        fun <A> failure(exception: Exception): Result<A> = Failure(IllegalStateException(exception))
    }
}

fun <K, V> Map<K, V>.getResult(key: K) = when {
    this.containsKey(key) -> Result(this[key])
    else -> Result.Empty
}

fun main() {
    print("aaa")
    val toons: Map<String, Toon> = mapOf(
        "Joseph" to Toon("Joseph", "Joestar", "joseph@jojo.com"),
        "Jonathan" to Toon("Jonathan", "Joestar"),
        "Jotaro" to Toon("Jotaro", "Kujo", "jotaro@jojo.com")
    )
    print("bbb")
    val toon = getName()
        .flatMap(toons::getResult)
        .flatMap(Toon::email)
    print("ccc")
    print(toon)
}

fun getName(): Result<String> = try {
    validate(readLine())
} catch (e: IOException) {
    Result.failure(e)
}

fun validate(name: String?): Result<String> = when {
    name?.isNotEmpty() ?: false -> Result(name)
    else -> Result.failure(IOException())
}