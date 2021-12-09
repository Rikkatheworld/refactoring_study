import java.time.Clock
import java.util.*

data class ExtractInvoices(
    val customer: String,
    val performances: List<PerformancesData>,
    val orders: List<ExtractOrder>,
    var dueDate: Date
)

data class ExtractOrder(val amount: Int)

class Printer() {
    fun printOwing(invoice: ExtractInvoices) {
        printBanner()

        val outstanding = calculateOutstanding(invoice)

        recordDueData(invoice)
        printDetails(invoice, outstanding)
    }

    private fun calculateOutstanding(invoice: ExtractInvoices): Int {
        return invoice.orders.fold(0) { acc, extractOrder ->
            acc + extractOrder.amount
        }
    }

    private fun printBanner() {
        print("***********************")
        print("**** Customer Owes ****")
        print("***********************")
    }

    private fun printDetails(invoice: ExtractInvoices, outstanding: Int) {
        print("name: ${invoice.customer}")
        print("amount: $outstanding")
        print("due: ${invoice.dueDate.time}")
    }

    private fun recordDueData(invoices: ExtractInvoices) {
        val today = Date()
        invoices.dueDate = Date(today.year, today.month, today.date + 30)
    }
}

