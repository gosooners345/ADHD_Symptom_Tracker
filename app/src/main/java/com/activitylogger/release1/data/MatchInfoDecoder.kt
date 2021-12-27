package com.activitylogger.release1.data

fun calculateScore(matchInfo: ByteArray): Double {
    val info = matchInfo.toIntArray()

    val numPhrases = info[0]
    val numColumns = info[1]

    var score = 0.0
    for (phrase in 0 until numPhrases) {
        val offset = 2 + phrase * numColumns * 3
        for (column in 0 until numColumns) {
            val numHitsInRow = info[offset + 3 * column]
            val numHitsInAllRows = info[offset + 3 * column + 1]
            if (numHitsInAllRows > 0) {
                score += numHitsInRow.toDouble() / numHitsInAllRows.toDouble()
            }
        }
    }

    return score
}


fun ByteArray.toIntArray(skipSize: Int = 4): IntArray {
    val cleanedArr = IntArray(this.size / skipSize)
    for ((pointer, i) in (this.indices step skipSize).withIndex()) {
        cleanedArr[pointer] = this[i].toInt()
    }

    return cleanedArr
}