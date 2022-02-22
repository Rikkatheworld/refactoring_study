private data class Adapter(val id: Int)

fun main() {
    val listOrigin =
        listOf(Adapter(5), Adapter(6), Adapter(100), Adapter(1), Adapter(3), Adapter(4), Adapter(2), Adapter(-123))
    val sortList = listOf(1, 4, 5, 6, 7)

    val sort = listOrigin.sortByList(sortList) { adapter: Adapter -> { i: Int -> adapter.id == i } }
    print("before: $listOrigin  after:${sort}")
}

/**
 * 找到List第一个满足表达式的元素
 */
fun <T> List<T>.matchAtFirst(m: (T) -> Boolean): T? {
    tailrec fun find(list: List<T>): T? = when {
        list.isEmpty() -> null
        m(list.first()) -> list.first()
        else -> find(list.drop(1))
    }
    return find(this)
}

/**
 * 按照 list 的顺序对 本List 进行排序
 * @param f 等价条件表达式
 */
fun <T, U> List<T>.sortByList(sortList: List<U>, f: (T) -> (U) -> Boolean): List<T> {
    tailrec fun sort(acc: List<T>, sort: List<U>, origin: List<T>): List<T> =
        when {
            origin.isEmpty() -> acc
            sort.isEmpty() -> acc + origin
            else -> {
                val elem = origin.matchAtFirst {
                    f(it)(sort.first())
                }
                if (elem == null) sort(acc, sort.drop(1), origin)
                else sort(acc + elem, sort, origin - elem)
            }
        }
    return sort(listOf(), sortList, this)
}