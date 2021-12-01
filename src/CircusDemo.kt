import kotlin.math.floor

data class PlayInfo(val name: String, val type: String)

val playInfos: MutableMap<String, PlayInfo> = mutableMapOf(
    Pair("西红市首富", PlayInfo("西红市首富", "comedy")),
    Pair("夏洛的烦恼", PlayInfo("夏洛的烦恼", "comedy")),
    Pair("新宿事件", PlayInfo("新宿事件", "tragedy"))
)

data class PerformancesData(
    var playId: String = "",
    var audience: Int = 0,
    var playInfo: PlayInfo = PlayInfo("", ""), //该表演的信息
    var amount: Int = 0,  // 一场表演所需要的费用
    var volumeCredits: Int = 0 //一场表演产生的信用点
)

data class Invoices(val customer: String, val performances: List<PerformancesData>)

val invoices = Invoices(
    "中船戏院", listOf(
        PerformancesData("西红市首富", 55),
        PerformancesData("夏洛的烦恼", 35),
        PerformancesData("新宿事件", 48)
    )
)

data class StatementData(
    var customer: String = "", // 客户名称
    var performances: List<PerformancesData> = emptyList(),  // 所有表演信息
    var totalAmount: Int = 0, // 客户总共需要缴费
    var totalVolumeCredits: Int = 0 // 客户总共获得的信用分
)

/**
 * 演出计算器
 */
abstract class PerformanceCalculator(private val aPerf: PerformancesData, val playInfo: PlayInfo) {

    abstract fun amount(): Int

    open fun volumeCredits(): Int {
        var result = 0
        result += (aPerf.audience - 30).coerceAtLeast(0)
        return result
    }
}

/**
 * 戏剧计算器
 */
class ComedyCalculator(private val perf: PerformancesData, playInfo: PlayInfo) :
    PerformanceCalculator(perf, playInfo) {

    override fun amount(): Int {
        var result: Int = 30000
        if (perf.audience > 20) {
            result += 10000 + 500 * (perf.audience - 20)
        }
        result += 300 * perf.audience
        return result
    }

    override fun volumeCredits(): Int {
        return super.volumeCredits() + floor(perf.audience / 5f).toInt()
    }
}

/**
 * 悲剧计算器
 */
class TragedyCalculator(private val perf: PerformancesData, playInfo: PlayInfo) :
    PerformanceCalculator(perf, playInfo) {
    override fun amount(): Int {
        var result: Int = 40000
        if (perf.audience > 30) {
            result += 1000 * (perf.audience - 30)
        }
        return result
    }
}


/**
 * 计算 StatementData
 *
 */
class StatementAdapter(private val invoice: Invoices, private val plays: MutableMap<String, PlayInfo>) {
    fun createStatementData(): StatementData {
        return StatementData().apply {
            customer = invoice.customer
            performances = invoice.performances.toMutableList().map { aPerf -> enrichPerformance(aPerf) }
            totalAmount = totalAmount(this)
            totalVolumeCredits = totalVolumeCredits(this)
        }
    }

    /**
     * 填充 单个 Performance其他数据， 用于计算逻辑
     */
    private fun enrichPerformance(aPerf: PerformancesData): PerformancesData {
        return PerformancesData().apply {
            // 原有数据
            audience = aPerf.audience
            playId = aPerf.playId
            // 新增计算数据
            val calculator = createPerformanceCalculator(aPerf, playFor(aPerf))
            playInfo = calculator.playInfo
            amount = calculator.amount()
            volumeCredits = calculator.volumeCredits()
        }
    }

    private fun createPerformanceCalculator(aPerf: PerformancesData, play: PlayInfo): PerformanceCalculator {
        return when (play.type) {
            "tragedy" -> TragedyCalculator(aPerf, play)
            "comedy" -> ComedyCalculator(aPerf, play)
            else -> throw  java.lang.Exception("unknown type:${play.type}")
        }
    }

    private fun amountFor(aPerf: PerformancesData): Int {
        return createPerformanceCalculator(aPerf, playFor(aPerf)).amount()
    }

    private fun totalAmount(statementData: StatementData): Int {
        return statementData.performances.fold(0) { totalAmount, performancesData ->
            totalAmount + performancesData.amount
        }
    }

    private fun totalVolumeCredits(statementData: StatementData): Int {
        return statementData.performances.fold(0) { totalVolumeCredits, performancesData ->
            totalVolumeCredits + performancesData.volumeCredits
        }
    }

    private fun volumeCreditsFor(aPerf: PerformancesData): Int {
        return createPerformanceCalculator(aPerf, playFor(aPerf)).volumeCredits()
    }

    private fun playFor(perf: PerformancesData): PlayInfo =
        plays[perf.playId] ?: PlayInfo("", "")

}

class Statement(private val invoice: Invoices, private val plays: MutableMap<String, PlayInfo>) {

    fun getStatement(): String {
        val adapter = StatementAdapter(invoice, plays)
        return renderPlainText(adapter.createStatementData())
    }

    /**
     * 生成纯文本的方法
     */
    private fun renderPlainText(data: StatementData): String {
        var result = "这里您的账单: ${data.customer} \n"
        data.performances.map { perf ->
            result += " ${perf.playInfo.name}: ${perf.amount / 100f} (${perf.audience} 观众) \n "
        }

        result += "所需缴费：￥${data.totalAmount / 100f}\n"
        result += "你获取了 ${data.totalVolumeCredits} 信用分 \n"
        return result
    }

    fun getHtmlStatement(): String {
        val adapter = StatementAdapter(invoice, plays)
        return renderHtml(adapter.createStatementData())
    }

    /**
     * 生成 HTML文本的方法
     */
    private fun renderHtml(data: StatementData): String {
        var result = "<h1>这里您的账单: ${data.customer}</h1>\n"
        result += "<table>\n"
        result += "<tr><th>演出</th><th>座位</th><th>花费</th></tr>"
        for (perf in data.performances) {
            result += "<tr><td>${perf.playInfo.name}</td><td>(${perf.audience} 观众)</td>"
            result += "<td>${perf.amount / 100f}</td></tr>\n?"
        }
        result += "</table>\n"
        result += "<p>所需缴费：<em>￥${data.totalAmount / 100f}</e></p>\n"
        result += "<p>你获取了 <em>${data.totalVolumeCredits}</em> 信用分</p> \n"
        return result
    }
}

fun main() {
    val statement = Statement(invoices, playInfos)
    print(statement.getStatement())
}