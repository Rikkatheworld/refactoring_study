import com.sun.org.apache.xpath.internal.operations.Bool
import java.lang.Exception

fun sum(n: Int): Int {
    tailrec fun sum(s: Int, i: Int): Int =
        if (i > n) s
        else sum(s + i, i + 1)
    return sum(0, 0)
}

fun inc(n: Int) = n + 1
fun dec(n: Int) = n - 1

fun add(a: Int, b: Int): Int {
    tailrec fun addRec(x: Int, y: Int): Int =
        if (y == 0) x
        else addRec(inc(x), dec(y))
    return addRec(a, b)

    var x = a
    var y = b
    while (y != 0) {
        y = dec(y)
        x = inc(x)
    }
    return x
}

fun <T> List<T>.head(): T =
    if (this.isEmpty()) throw Exception("head called on empty list")
    else this[0]

fun <T> List<T>.tail(): List<T> =
    if (this.isEmpty()) throw Exception("tail called on empty list")
    else this.drop(1)

//fun makeString(list: List<String>, delim: String): String =
//    when {
//        list.isEmpty() -> ""
//        list.tail().isEmpty() -> "${list.head()} ${makeString(list.tail(), delim)}"
//        else -> "${list.head()} $delim ${makeString(list.tail(), delim)}"
//    }
//fun sum(list: List<Int>): Int {
//    tailrec fun sumTail(list: List<Int>, acc: Int): Int =
//        if (list.isEmpty()) acc
//        else sumTail(list.tail(), acc + list.head())
//    return sumTail(list, 0)
//}

//fun <T> makeString(list: List<T>, delim: String): String {
//    tailrec fun makeString_(list: List<T>, acc: String): String = when {
//        list.isEmpty() -> acc
//        acc.isEmpty() -> makeString_(list.tail(), "${list.head()}")
//        else -> makeString_(list.tail(), "$acc $delim ${list.head()}")
//    }
//    return makeString_(list, "")
//}

fun <T, U> foldLeft(list: List<T>, z: U, f: (T, U) -> U): U {
    tailrec fun foldLeft(list: List<T>, acc: U): U =
        if (list.isEmpty()) acc
        else foldLeft(list.tail(), f(list.head(), acc))
    return foldLeft(list, z)
}

fun sum(list: List<Int>) = foldLeft(list, 0, Int::plus)

//fun string(list: List<Char>) = foldLeft(list, "") { t, acc -> acc + t }
fun <T> makeString(list: List<T>, delim: String): String = foldLeft(list, "") { t, acc ->
    if (acc.isEmpty()) "$t"
    else "$acc $delim $t"
}

//fun prepend(c: Char, s: String): String = "$c$s"
fun main() {
//    print(sum(listOf(1, 2, 3, 4, 5)))
//    print(makeString(listOf("a", "b", "c", "d", "e", "f", "g"), "123"))
//    print(string(listOf('a', 'b', 'c', 'd')))
//    print(reverseFold(listOf('a', 'b', 'c', 'd')))
}

//fun string(list: List<Char>): String = foldRight(list, "") { char, acc ->
//    prepend(char, acc)
//}

fun <T, U> foldRight(list: List<T>, identity: U, f: (T, U) -> U): U =
    if (list.isEmpty()) identity
    else f(list.head(), foldRight(list.tail(), identity, f))

fun <T> unfold(seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> {
    tailrec fun unfold_(acc: List<T>, seed: T): List<T> =
        // 非终止条件
        if (p(seed)) unfold_(acc + seed, f(seed))
        else acc
    return unfold_(listOf(), seed)
}
