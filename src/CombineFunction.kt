class CombineFunction {

}

data class CombineFunReading(val charge: Int) {
    fun base() {}
    fun taxableCharge() {}
    fun calculateBaseCharge() {}
}

fun base(reading: CombineFunReading): Int {
    return 1
}

fun taxableCharge(reading: CombineFunReading): Int {
    return 1
}

fun enrichReading(argReading: CombineFunReading): CombineFunReadingWrapper {
    val readingWrapper = CombineFunReadingWrapper(argReading)
    readingWrapper.base = base(argReading)
    readingWrapper.taxableCharge = taxableCharge(argReading)
    return readingWrapper
}

data class CombineFunReadingWrapper(val reading: CombineFunReading, var base: Int = 0, var taxableCharge: Int = 0)

fun calculateBaseCharge(reading: CombineFunReading) {

}