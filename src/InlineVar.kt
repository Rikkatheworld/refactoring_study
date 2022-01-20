data class InlineVarOrder(val basePrice: Int)

data class InlineVarCustomer(val name: String, val address: InlineVarAddress)
data class InlineVarAddress(val state: String, val stateCode: String)
class Book() {
    private val reservations = mutableListOf<InlineVarCustomer>()

    fun addReservation(customer: InlineVarCustomer) {
        addReservation(customer, false)
    }

    fun addReservation(customer: InlineVarCustomer, isPriority: Boolean) {
        reservations.add(customer)
    }
}


class InlineVar {
    fun basePriceMoreThan1000(order: InlineVarOrder): Boolean {
        return order.basePrice > 1000
    }

    fun inNewEngland(customer: InlineVarCustomer): Boolean {
        val stateCode = customer.address.state
        return inNewEngland(stateCode)
    }

    fun inNewEngland(stateCode: String): Boolean {
        return listOf("MA", "CT", "ME", "VT", "NH", "RI").contains(stateCode)
    }

    fun filterEnglanders(customers: List<InlineVarCustomer>) {
        val newEnglanders = customers.filter {
            inNewEngland(it)
        }
    }
}