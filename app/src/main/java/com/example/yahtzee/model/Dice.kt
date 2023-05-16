package com.example.yahtzee.model

import android.util.Log
import java.util.UUID

private const val TAG = "Dice"

enum class DieResult(val value: Int) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    NOT_ROLLED(-1)
}

class Dice (
    var id: UUID = UUID.randomUUID(),
    var result: DieResult = DieResult.NOT_ROLLED,
    var savedDie: Boolean = false,
        ) {

    fun randomizeResult() {
        Log.d(TAG, "randomizeResult: starts")
        this.result = when((1..6).random()) {
            1 -> DieResult.ONE
            2 -> DieResult.TWO
            3 -> DieResult.THREE
            4 -> DieResult.FOUR
            5 -> DieResult.FIVE
            6 -> DieResult.SIX
            else -> DieResult.NOT_ROLLED
        }
        Log.d(TAG, "randomizeResult: ends with $result")
    }

    override fun toString(): String {
        return "${this.id}, ${this.result}, ${this.savedDie}"
    }
}