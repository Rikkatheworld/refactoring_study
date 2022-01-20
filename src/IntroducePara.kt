data class IntroduceParaStation(val name: String, val reading: List<IntroduceParaTemp>)
data class IntroduceParaTemp(val temp: Int, val time: String)
data class IntroduceParaRange(val temperatureFloor: Int, var temperatureCeiling: Int)
class IntroducePara {

    companion object {
        private val STATION = IntroduceParaStation(
            name = "SZ",
            reading = listOf(
                IntroduceParaTemp(temp = 15, time = "2021-12-10 09:00"),
                IntroduceParaTemp(temp = 16, time = "2021-12-10 10:00"),
                IntroduceParaTemp(temp = 19, time = "2021-12-10 11:00"),
                IntroduceParaTemp(temp = 21, time = "2021-12-10 12:00")
            )
        )
    }

    data class IntroduceNumberRange(val min: Int, val max: Int)

    fun call(operationPlan: IntroduceParaRange) {
        val range = IntroduceNumberRange(operationPlan.temperatureFloor, operationPlan.temperatureCeiling)
        val alerts = readingOutsideRange(STATION, range)
    }

    private fun readingOutsideRange(station: IntroduceParaStation, range: IntroduceNumberRange): List<IntroduceParaTemp> {
        return station.reading.filter { r ->
            r.temp !in (range.min..range.max)
        }
    }


}