data class InlineDriver(val numberOfLateDeliveries: Int)
data class InlineCustomer(val name: String, val location: String)

class InlineFun {

    fun getRating(driver: InlineDriver): Int {
        return if (driver.numberOfLateDeliveries > 5) 2 else 1
    }

    fun reportLines(customer: InlineCustomer): Map<String, String> {
        return mapOf("name" to  customer.name, "location" to customer.location)
    }

    private fun gatherCustomerData(map: MutableMap<String, String>, customer: InlineCustomer) {
        map["name"] = customer.name
        map["location"] = customer.location
    }
}