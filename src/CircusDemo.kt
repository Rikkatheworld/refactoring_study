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

fun statement(invoice: Invoices, plays: MutableMap<String, PlayInfo>): String {
    var totalAmount = 0
    var volumeCredits = 0
    var result = "这里您的账单: ${invoice.customer} \n"

    for (perf in invoice.performances) {
        val playInfo = plays[perf.playId] ?: continue
        var thisAmount = 0

        when (playInfo.type) {
            "tragedy" -> {
                thisAmount = 40000
                if (perf.audience > 30) {
                    thisAmount += 1000 * (perf.audience - 30)
                }
            }
            "comedy" -> {
                thisAmount = 30000
                if (perf.audience > 20) {
                    thisAmount += 10000 + 500 * (perf.audience - 20)
                }
                thisAmount += 300 * perf.audience
            }
        }
        volumeCredits += (perf.audience - 30).coerceAtLeast(0)
        if (playInfo.type == "comedy") volumeCredits += floor(perf.audience / 5f).toInt()
        result += " ${playInfo.name}: ${thisAmount / 100f} (${perf.audience} 观众) \n "
        totalAmount += thisAmount
    }
    result += "所欠金额：￥${totalAmount / 100f}\n"
    result += "你获取了 $volumeCredits 信用分 \n"
    return result
}

fun main() {
    print(statement(invoices, playInfos))
}