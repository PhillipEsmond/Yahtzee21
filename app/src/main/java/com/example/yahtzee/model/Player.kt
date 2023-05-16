package com.example.yahtzee.model

import android.util.Log
import java.util.UUID

private const val TAG = "Player"

class Player (
    var id: UUID = UUID.randomUUID(),
    var name: String = "Player",
    var diceToRoll: ArrayList<Dice> = arrayListOf(Dice(), Dice(), Dice(), Dice(), Dice(), Dice()),
    var setScores: MutableMap<Set, Int> = mutableMapOf(),
    var playerTurn: Boolean = false,
    var rollCount: Int = YahtzeeConstants.GameValues.ROLLING_ALLOWED
        ) {

    override fun toString(): String {
        return "${this.id}, ${this.name}, ${this.diceToRoll}, ${this.setScores}, ${this.playerTurn}, ${this.rollCount},"
    }

    fun getSetsFullfillment(): Int {
        Log.d(TAG, "getSetsFullfillment: starts")
        return setScores.keys.count()
    }

    private fun getNewDice() {
        Log.d(TAG, "getNewDice: starts with $diceToRoll")
        val diceList = kotlin.collections.ArrayList<Dice>()
        for(i in 1..6) {
            diceList.add(Dice())
        }
        diceToRoll = diceList
        Log.d(TAG, "getNewDice: ends with $diceToRoll")
    }

    fun resetEndOfRound() {
        Log.d(TAG, "resetEndOfRound: starts")
        rollCount = YahtzeeConstants.GameValues.ROLLING_ALLOWED
        getNewDice()
        playerTurn = false
    }

    fun saveDice(dice: Dice) {
        Log.d(TAG, "saveDice: starts with $dice")
        dice.savedDie = dice.savedDie != true
        Log.d(TAG, "saveDice: ends with $dice")
    }

    fun getTotalResult(): Int {
        Log.d(TAG,"getTotalResult: starts")
        return setScores.map { it.value }.sum() + getBonusResult()
    }

    fun rollDice() {
        Log.d(TAG, "rollDice: starts with $diceToRoll")
        diceToRoll.filter { !it.savedDie }.forEach { die ->
            die.randomizeResult()

            Log.d(TAG, "rollDice: rolled dice is $die")
        }
        rollCount--
        Log.d(TAG, "rollDice: ends with $diceToRoll")
    }

    fun saveScore(scoreSet: Set): Boolean {
        Log.d(TAG, "saveScore: starts with $scoreSet")
        val result = chooseSet(scoreSet)
        return if(setScores[scoreSet] == null) {
            setScores[scoreSet] = result
            Log.d(TAG, "saveScore: ends with $scoreSet and $result, true")
            true
        } else {
            Log.d(TAG, "saveScore: ends with $scoreSet and $result, false")
            false
        }
    }

    fun getBonusValue(): Int {
        Log.d(TAG, "getBonusValue: starts")
        val result = setScores.filter { it.key == Set.ACES || it.key == Set.TWOS
                || it.key == Set.THREES || it.key == Set.FOURS
                || it.key == Set.FIVES || it.key == Set.SIXES
        }.map { it.value }.sum()
        Log.d(TAG, "getBonusValue: ends with $result")
        return result
    }

    private fun getBonusResult(): Int {
        Log.d(TAG, "getBonusResult: starts")
        val result = if(getBonusValue() >= YahtzeeConstants.GameValues.BONUS_IS_ACHIEVED) {
            YahtzeeConstants.GameValues.BONUS_EXTRA_POINTS
        } else
            YahtzeeConstants.GameValues.ZERO_POINTS
            Log.d(TAG, "getBonusResult: ends with $result")
            return result
        }

    private fun sumDiceNumber(number: Int): Int {
        Log.d(TAG, "sumDiceNumber: starts with $number")
        val result = diceToRoll.map { it.result.value }.filter { it == number }.sum()
        Log.d(TAG, "sumDiceNumber: ends with $result")
        return result
    }

    fun chooseSet(set: Set): Int {
        Log.d(TAG, "chooseSet: starts with $set")
        val result = when(set) {
            Set.ACES -> {
                sumDiceNumber(DieResult.ONE.value)
            }
            Set.TWOS -> {
                sumDiceNumber(DieResult.TWO.value)
            }
            Set.THREES -> {
                sumDiceNumber(DieResult.THREE.value)
            }
            Set.FOURS -> {
                sumDiceNumber(DieResult.FOUR.value)
            }
            Set.FIVES -> {
                sumDiceNumber(DieResult.FIVE.value)
            }
            Set.SIXES -> {
                sumDiceNumber(DieResult.SIX.value)
            }
            Set.THREE_OF_A_KIND -> {
                if (checkForEqualsDice(Set.THREE_OF_A_KIND) || checkForEqualsDice(
                        Set.FOUR_OF_A_KIND
                    )
                ) {
                    diceToRoll.sumBy { it.result.value }
                } else YahtzeeConstants.GameValues.ZERO_POINTS
            }
            Set.FOUR_OF_A_KIND -> {
                if (checkForEqualsDice(Set.FOUR_OF_A_KIND)) {
                    diceToRoll.sumBy { it.result.value }
                }
                else YahtzeeConstants.GameValues.ZERO_POINTS
            }
            Set.FULL_HOUSE -> {
                if(checkFullHouse()) YahtzeeConstants.GameValues.FULL_HOUSE_POINTS else YahtzeeConstants.GameValues.ZERO_POINTS
            }
            Set.SMALL_STRAIGHT -> {
                if(checkSmallStraight()) YahtzeeConstants.GameValues.SMALL_STRAIGHT_POINTS else YahtzeeConstants.GameValues.ZERO_POINTS
            }
            Set.LARGE_STRAIGHT -> {
                if(checkLargeStraight()) YahtzeeConstants.GameValues.LARGE_STRAIGHT_POINTS else YahtzeeConstants.GameValues.ZERO_POINTS
            }
            Set.YAHTZEE -> {
                if(checkYahtzee()) YahtzeeConstants.GameValues.YAHTZEE_POINTS else YahtzeeConstants.GameValues.ZERO_POINTS
            }
            Set.CHANCE -> {
                diceToRoll.sumOf { it.result.value }
            }
        }
        Log.d(TAG, "chooseSet: ends with $set and result $result")
        return result
    }

    private fun checkForEqualsDice(setType: Set): Boolean {
        Log.d(TAG, "checkForEqualsDice: starts with $setType")
        val countedResults = diceToRoll.map { it.result.value }.groupingBy { it }.eachCount()
        val result = when(setType) {
            Set.THREE_OF_A_KIND -> {
                countedResults.values.any { it >= 3 }
            }
            Set.FOUR_OF_A_KIND -> {
                countedResults.values.any { it >= 4 }
            }
            else -> false
        }
        Log.d(TAG, "checkForEqualsDice: ends with $setType and result $result")
        return result
    }

    private fun checkFullHouse(): Boolean {
        Log.d(TAG, "checkFullHouse: starts")
        val result = if(checkForEqualsDice(Set.FOUR_OF_A_KIND) || checkForEqualsDice(
                Set.THREE_OF_A_KIND
        )) {
            val countedResults = diceToRoll.map { it.result.value }.groupingBy { it }.eachCount()
            val ejectValue = countedResults.values.find { it >= 3 }
            val remainList = countedResults.values.minusElement(ejectValue)
            val findResult = remainList.any { it!! >= 2 }
            findResult
        } else false
        Log.d(TAG, "checkFullHouse: ends with $result")
        return result
    }

    private fun checkSmallStraight(): Boolean {
        Log.d(TAG, "checkSmallStraight: starts")
        val diceResults = diceToRoll.map { it.result.value }
        val firstCase = mutableListOf(1,2,3,4)
        val secondCase = mutableListOf(2,3,4,5)
        val thirdCase = mutableListOf(3,4,5,6)
        val result = diceResults.containsAll(firstCase) || diceResults.containsAll(secondCase) || diceResults.containsAll(thirdCase)
        Log.d(TAG, "checkSmallStraight: ends with $result")
        return result
    }

    private fun checkLargeStraight(): Boolean {
        Log.d(TAG, "checkLargeStraight: starts")
        val diceResults = diceToRoll.map { it.result.value }
        val firstCase = mutableListOf(1,2,3,4,5)
        val secondCase = mutableListOf(2,3,4,5,6)
        val result = diceResults.containsAll(firstCase) || diceResults.containsAll(secondCase)
        Log.d(TAG, "checkLargeStraight: ends with $result")
        return result
    }

    private fun checkYahtzee(): Boolean {
        Log.d(TAG, "checkYahtzee: starts")

        val countedResults = diceToRoll.map { it.result.value }.groupingBy { it }.eachCount()
        val result = countedResults.any { it.value >= 5 }

        Log.d(TAG, "checkYahtzee: ends with $result")
        return result
    }
}