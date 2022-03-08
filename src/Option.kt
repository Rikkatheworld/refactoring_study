import java.lang.RuntimeException

data class Toon(
    val firstName: String, // 首名字
    val lastName: String, // 姓氏
    val email: Result<String>  // 可选email
) {
    companion object {
        operator fun invoke(firstName: String, lastName: String, email: String? = null) =
            Toon(firstName, lastName, Result(email))
    }
}

// 扩展函数来实现前检查模式, 以避免返回空引用
fun <K, V> Map<K, V>.getOption(key: K) = Option(this[key])

fun main() {
    val toons: Map<String, Toon> = mapOf(
        "Joseph" to Toon("Joseph", "Joestar", "joseph@jojo.com"),
        "Jonathan" to Toon("Jonathan", "Joestar"),
        "Jotaro" to Toon("Jotaro", "Kujo", "jotaro@jojo.com")
    )

    val joseph = toons["Joseph"]?.email ?: "No data"
    val jonathan = toons["Jonathan"]?.email ?: "No data"
    val jolyne = toons["Jolyne"]?.email ?: "No data"

    print("$joseph\n")
    print("$jonathan\n")
    print(jolyne)
}

fun getDefault(): Int = throw RuntimeException()

sealed class Option<out A> {
    abstract fun isEmpty(): Boolean

    internal object None : Option<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun equals(other: Any?): Boolean = other === null
        override fun hashCode(): Int = 0
    }

    internal data class Some<out A>(internal val value: A) : Option<A>() {
        override fun isEmpty(): Boolean = false
    }

    companion object {
        operator fun <A> invoke(a: A? = null): Option<A> =
            when (a) {
                null -> None
                else -> Some(a)
            }
    }

    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None
        is Some -> Some(f(this.value))
    }

    fun <B> flatmap(f: (A) -> Option<B>): Option<B> = when (this) {
        is None -> None
        is Some -> f(this.value)
    }

    fun getOrElse(default: () -> @UnsafeVariance A): A = when (this) {
        is None -> default()
        is Some -> value
    }

    fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> = map { this }.getOrElse(default)

    fun filter(p: (A) -> Boolean): Option<A> = flatmap { if (p(it)) this else None }
}