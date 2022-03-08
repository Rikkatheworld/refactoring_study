import com.sun.org.apache.xpath.internal.operations.Bool
import java.lang.IllegalStateException
import java.lang.reflect.Type

sealed class MyList<out A> {
    abstract fun isEmpty(): Boolean

    fun cons(a: @UnsafeVariance A): MyList<A> = Cons(a, this)

    abstract val length: Int

    // 扩展类在列表类内定义，并成为私有类
    internal object Nil : MyList<Nothing>() {
        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"

        override val length: Int = 0
    }

    internal class Cons<out A>(
        internal val head: A,
        internal val tail: MyList<A>
    ) : MyList<A>() {
        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}NIL]"

        override val length: Int = tail.length + 1

        tailrec fun toString(acc: String, list: MyList<@UnsafeVariance A>): String = when (list) {
            is Nil -> acc
            is Cons -> toString("$acc${list.head}, ", list.tail)
        }
    }

//    fun cons(a: A): MyList<A> = Cons(a, this)

    fun setHead(a: @UnsafeVariance A): MyList<A> = when (this) {
        Nil -> throw IllegalStateException("setHead called on an empty list")
        is Cons -> tail.cons(a)
    }

    fun drop(n: Int): MyList<A> {
        tailrec fun drop(n: Int, list: MyList<A>): MyList<A> =
            if (n <= 0) list
            else when (list) {
                is Cons -> drop(n - 1, list.tail)
                is Nil -> list
            }
        return drop(n, this)
    }

    fun dropWhile(p: (A) -> Boolean): MyList<A> = dropWhile(this, p)

    fun concat(list: MyList<@UnsafeVariance A>): MyList<A> = concat(this, list)

    companion object {
        private fun <A> concat(list1: MyList<A>, list2: MyList<A>): MyList<A> = when (list1) {
            Nil -> list2
            is Cons -> concat(list1.tail, list2).cons(list1.head)
        }

        private tailrec fun <A> dropWhile(list: MyList<A>, p: (A) -> Boolean): MyList<A> = when (list) {
            Nil -> list
            is Cons -> if (p(list.head)) dropWhile(list.tail, p) else list
        }

        // foldRight 的第一个参数是将 Nil 显示转化为 list<A>
        operator fun <A> invoke(vararg az: A): MyList<A> =
            az.foldRight(Nil as MyList<A>) { a, acc ->
                Cons(a, acc)
            }

        fun <A, B> foldRight(list: MyList<A>, identity: B, f: (A) -> (B) -> B): B =
            when (list) {
                Nil -> identity
                is Cons -> f(list.head)(foldRight(list.tail, identity, f))
            }

        tailrec fun <A, B> foldLeft(acc: B, list: MyList<A>, f: (B) -> (A) -> B): B = when (list) {
            Nil -> acc
            is Cons -> foldLeft(f(acc)(list.head), list.tail, f)
        }

        private tailrec fun <A, B> coFoldRight(acc: B, list: MyList<A>, identity: B, f: (A) -> (B) -> B): B =
            when (list) {
                Nil -> acc
                is Cons -> coFoldRight(f(list.head)(acc), list.tail, identity, f)
            }
    }

    fun <B> coFoldRight(identity: B, f: (A) -> (B) -> B): B = coFoldRight(identity, this.reverse(), identity, f)

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B = foldLeft(identity, this, f)

    fun <B> foldRight(identity: B, f: (A) -> (B) -> B): B = foldRight(this, identity, f)

    fun <B> foldRightViaFoldLeft(identity: B, f: (A) -> (B) -> B) =
        this.reverse().foldLeft(identity) { x -> { y -> f(y)(x) } }

//    fun length(): Int = foldRight(0) { { it + 1 } }

    fun length(): Int = foldLeft(0) { i -> { i + 1 } }

//    fun reverse(): MyList<A> {
//        tailrec fun <A> reverse(acc: MyList<A>, list: MyList<A>): MyList<A> = when (list) {
//            Nil -> acc
//            is Cons -> reverse(acc.cons(list.head), list.tail)
//        }
//        return reverse(MyList.invoke(), this)
//    }

    fun reverse(): MyList<A> = foldLeft(invoke()) { acc -> { acc.cons(it) } }


    fun init(): MyList<A> = reverse().drop(1).reverse()

    fun <A> concatViaFoldRight(list1: MyList<A>, list2: MyList<A>): MyList<A> =
        foldRight(list1, list2, { x -> { y -> Cons(x, y) } })

    fun <A> concatViaLeft(list1: MyList<A>, list2: MyList<A>): MyList<A> =
        list1.reverse().foldLeft(list2) { x -> { y -> x.cons(y) } }

//    fun <A> flatten(list: MyList<A>): MyList<A> = list.foldRight(Nil) { x -> x::concat }
}

fun sum(list: MyList<Int>): Int = list.foldLeft(0) { x -> { y -> x + y } }
fun product(list: MyList<Double>): Double = list.foldLeft(1.0) { x -> { y -> x * y } }

//fun sum(list: MyList<Int>): Int = MyList.foldRight(list, 0) { x -> { y -> x + y } }
//fun product(list: MyList<Double>): Double = MyList.foldRight(list, 1.0) { x -> { y -> x * y } }

//fun product(ints: MyList<Type>): Type = when (ints) {
//    MyList.Nil -> identity
//    is MyList.Cons -> ints.head operator operation(ints.tail)
//}
//
//fun sum(ints: MyList<Type>): Type = when (ints) {
//    MyList.Nil -> identity
//    is MyList.Cons -> ints.head operator operation(ints.tail)
//}

fun main() {
    val list = MyList("a", "b", "c", "d", "e")
    val aList = listOf(1, 2, 3, 4, 5)
    aList.map { }
    print(list)
}

//sealed class EmptyMyList<A> {
//    fun cons(a: A): EmptyMyList<A> = Cons(a, this)
//
//    abstract class Empty<A> : EmptyMyList<A>() {
//    }
//
//    object Nil : Empty<Nothing>() {
//        override fun funconcat(list: EmptyMyList<Nothing>): EmptyMyList<Nothing> = list
//
//    }
//
//    class Cons<A>(
//        internal val head: A,
//        internal val tail: EmptyMyList<A>
//    ) : EmptyMyList<A>() {
//        override fun funconcat(list: EmptyMyList<A>): EmptyMyList<A> = Cons(this.head, list.concat(this.tail))
//    }
//
//    fun concat(list: EmptyMyList<A>): EmptyMyList<A> = concat(this, list)
//
//    abstract fun funconcat(list: EmptyMyList<A>): EmptyMyList<A>
//
//    companion object {
//        private fun <A> concat(list1: EmptyMyList<A>, list2: EmptyMyList<A>): EmptyMyList<A> = when (list1) {
//            Nil -> list2
//            is Cons -> concat(list1.tail, list2).cons(list1.head)
//        }
//    }
//}