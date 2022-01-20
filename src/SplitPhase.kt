class SplitPhase {


    fun calOrderPrice(orderString: String, priceMap: Map<String, Int>): Int {
        val orderRecord = parseOrder(orderString)
        return price(orderRecord, priceMap)
    }

    private fun parseOrder(aString: String): Pair<String, Int> {
        val values = aString.split(",")
        return Pair(values[0].split("-")[0], values[1].toInt())
    }

    private fun price(order: Pair<String, Int>, priceMap: Map<String, Int>): Int {
        return order.second * (priceMap[order.first] ?: 1)
    }


}