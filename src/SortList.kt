data class SortedClass(
    val name: String,
    val pri: Boolean
)

fun main() {
    val list = listOf(
        SortedClass("aaa", false),
        SortedClass("bbb", true),
        SortedClass("ccc", false),
        SortedClass("ddd", false),
        SortedClass("eee", true),
        SortedClass("ggg", false)
    )
    print(list.sortedByDescending { it.pri })
}