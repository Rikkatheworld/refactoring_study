import java.lang.IllegalArgumentException

class KotlinCap3 {
    private fun square(n: Int) = n * n
    private fun triple(n: Int) = n * 3

//    private fun compose(a: (Int) -> Int, b: (Int) -> Int): (Int) -> Int = { x -> a(b(x)) }

    private val add: (Int) -> (Int) -> Int = { a -> { b -> a + b } }

    /**
     * 更加通用的复合函数
     */
    private fun <T, U, V> compose(f: (U) -> V, g: (T) -> U): (T) -> V = { f(g(it)) }

    fun <T, U, V> higherCompose() = { f: (U) -> V ->
        { g: (T) -> U ->
            { x: T ->
                f(g(x))
            }
        }
    }

    val cosValue: Double = compose({ x: Double -> Math.PI / 2 - x }, Math::sin)(2.0)

    val higherCosValue = higherCompose<Double, Double, Double>()() { x: Double -> Math.PI / 2 - x }(Math::sin)

    fun <T, U, V> higherComposeAndThen() = { f: (T) -> U ->
        { g: (U) -> V ->
            { x: T ->
                g(f(x))
            }
        }
    }

    private val squareOfTriple = compose(::square, ::triple)

    fun <T, U, V> tCompose() =
        { f: (U) -> V ->
            { g: (T) -> U ->
                { x: T ->
                    f(g(x))
                }
            }
        }

    fun main() {
        print(squareOfTriple(2))

        print(add(3)(5))
//        val valueCompose: ((Int) -> Int) -> ((Int) -> Int) -> ((Int) -> Int) = { x -> { y -> { z -> x(y(z)) } } }
        val valueCompose: (IntUnaryOp) -> (IntUnaryOp) -> IntUnaryOp = { x -> { y -> { z -> x(y(z)) } } }
        val square: IntUnaryOp = { it * it }
        val triple: IntUnaryOp = { it * 3 }
        val squareOfTriple = valueCompose(square)(triple)
        print(squareOfTriple(2))

        val squareOfTriple2 = tCompose<Int, Int, Int>()(square)(triple)

        val q = higherCompose<Int, Int, Int>()(square)(triple)
        val add: (Int, Int) -> Int = { a, b -> a + b }
//        val f: (Double) -> Double = { Math.PI / 2 - it }
//        val sin:(Double) -> Double = Math::sin
//        val cosValue = compose(f, sin)(2.0)
        val cosValue = tCompose<Double, Double, Double>()() { x: Double -> Math.PI / 2 - x }(Math::sin)(2.0)
        print(cosValue)
    }

    operator fun plus(p: Int) = 2
}
typealias IntBinOp = (Int) -> (Int) -> Int

private val add: IntBinOp = { a -> { b -> a + b } }

typealias IntUnaryOp = (Int) -> Int

data class Product(val name: String, val price: Price, val weight: Weight)

data class OrderLine(val product: Product, val cnt: Int) {
    fun weight(): Weight = product.weight * cnt
    fun amount(): Price = product.price * cnt
}

data class Price private constructor(private val value: Double) {

    override fun toString(): String = value.toString()
    operator fun plus(price: Price) = Price(this.value + price.value)
    operator fun times(num: Int) = Price(this.value * num)

    companion object {
        val identity = Price(0.0)

        operator fun invoke(value: Double): Price {
            return if (value > 0) {
                Price(value)
            } else
                throw IllegalArgumentException("Price must be more than 0")
        }
    }
}

data class Weight private constructor(val value: Double) {
    override fun toString(): String = value.toString()
    operator fun plus(weight: Weight) = Weight(this.value + weight.value)
    operator fun times(num: Int) = Weight(this.value * num)

    companion object {
        val identity = Weight(0.0)

        operator fun invoke(value: Double): Weight {
            return if (value > 0) {
                Weight(value)
            } else
                throw IllegalArgumentException("Weight must be more than 0")
        }
    }
}

fun main() {
    val iPhone = Product("iPhone", Price(3.5), Weight(0.5))
    val xiaomi = Product("xiaomi", Price(1.5), Weight(0.2))
    val orderLines = listOf(OrderLine(iPhone, 2), OrderLine(xiaomi, 3))

    val weight: Weight = orderLines.fold(Weight.identity) { accelerate, b ->
        accelerate + b.weight()
    }
    val amount: Price = orderLines.fold(Price.identity) { accelerate, b ->
        accelerate + b.amount()
    }
    print("weight:$weight, amount:$amount")
}