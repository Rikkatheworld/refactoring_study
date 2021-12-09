data class ExtractVarOrder(val quantity: Double, val itemPrice: Double)

fun cal(order: ExtractVarOrder): Double {
    val basePrice = order.quantity * order.itemPrice
    val quantityDiscount = (0.0).coerceAtLeast(order.quantity - 500) * order.itemPrice * 0.05
    val shipping = (order.quantity * order.itemPrice * 0.1).coerceAtMost(100.0)
    return basePrice - quantityDiscount + shipping

}

data class ExtractRecord(val quantity: Double, val itemPrice: Double)
private class Order(private val data: ExtractRecord) {
    fun getQuantity(): Double = data.quantity
    fun getItemPrice(): Double = data.itemPrice

    fun getBasePrice(): Double = getQuantity() * getItemPrice()
    fun getQuantityDiscount(): Double = (0.0).coerceAtLeast(getQuantity() - 500) * getItemPrice() * 0.05
    fun getShipping(): Double = (getQuantity() * getItemPrice() * 0.1).coerceAtMost(100.0)

    fun getPrice(): Double {
        return getBasePrice() - getQuantityDiscount() + getShipping()
    }
}