import kotlin.math.floor

data class PlayInfo(val name: String, val type: String)

val playInfos: MutableMap<String, PlayInfo> = mutableMapOf(
    Pair("西红市首富", PlayInfo("西红市首富", "comedy")),
    Pair("夏洛的烦恼", PlayInfo("夏洛的烦恼", "comedy")),
    Pair("新宿事件", PlayInfo("新宿事件", "tragedy"))
)

data class PerformancesData(val playId: String, val audience: Int)
data class Invoices(val customer: String, val performances: List<PerformancesData>)

val invoices = Invoices(
    "中船戏院", listOf(
        PerformancesData("西红市首富", 55),
        PerformancesData("夏洛的烦恼", 35),
        PerformancesData("新宿事件", 48)
    )
)

/**
 * 用来计算一次账单的工具
 */
class Statement(private val invoice: Invoices, private val plays: MutableMap<String, PlayInfo>) {

    fun getStatement(): String {
        var result = "这里您的账单: ${invoice.customer} \n"
        for (perf in invoice.performances) {
            result += " ${playFor(perf).name}: ${amountFor(perf) / 100f} (${perf.audience} 观众) \n "
        }

        result += "所需缴费：￥${totalAmount(invoice) / 100f}\n"
        result += "你获取了 ${totalVolumeCredits(invoice)} 信用分 \n"
        return result
    }

    private fun totalAmount(invoice: Invoices): Int {
        var result = 0
        for (perf in invoice.performances) {
            result += amountFor(perf)
        }
        return result
    }

    private fun totalVolumeCredits(invoice: Invoices): Int {
        var result = 0
        for (perf in invoice.performances) {
            result += volumeCreditsFor(perf)
        }
        return result
    }

    private fun volumeCreditsFor(perf: PerformancesData): Int {
        var result = 0
        result += (perf.audience - 30).coerceAtLeast(0)
        if (playFor(perf).type == "comedy") result += floor(perf.audience / 5f).toInt()
        return result
    }

    private fun amountFor(perf: PerformancesData): Int {
        var result: Int
        when (playFor(perf).type) {
            "tragedy" -> {
                result = 40000
                if (perf.audience > 30) {
                    result += 1000 * (perf.audience - 30)
                }
            }
            "comedy" -> {
                result = 30000
                if (perf.audience > 20) {
                    result += 10000 + 500 * (perf.audience - 20)
                }
                result += 300 * perf.audience
            }
            else -> throw Exception("unknown type:${playFor(perf).type}")
        }
        return result
    }

    private fun playFor(perf: PerformancesData): PlayInfo =
        plays[perf.playId] ?: PlayInfo("", "")
}

fun main() {
    val statement = Statement(invoices, playInfos)
    print(statement.getStatement())
}