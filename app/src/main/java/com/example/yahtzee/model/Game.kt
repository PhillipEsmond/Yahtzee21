package com.example.yahtzee.model

import android.util.Log

enum class Set {
    ACES,
    TWOS,
    THREES,
    FOURS,
    FIVES,
    SIXES,
    THREE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    SMALL_STRAIGHT,
    LARGE_STRAIGHT,
    YAHTZEE,
    CHANCE
}

private const val TAG = "Game"

class Game(var players: ArrayList<Player> = arrayListOf(Player(name = "Player 1"), Player(name = "Player 2"))) {

    fun nextPlayer(): Player {
        Log.d(TAG, "nextPlayer: starts with $players")
        val result = if(!players.first().playerTurn) {
            players[1].resetEndOfRound()

            players.first().playerTurn = true
            players.first()
        } else {
            players.first().resetEndOfRound()

            players[1].playerTurn = true
            players[1]
        }

        Log.d(TAG, "nextPlayer: ends with $players, and result $result")

        return result
    }

    fun checkGameOver(): Boolean {
        Log.d(TAG, "checkGameOver: starts with $players")
        players.forEach {
            if(it.getSetsFullfillment() != YahtzeeConstants.GameValues.SETS_ALL_COMPLETED) return false
        }
        return true
    }

    fun checkWinner(): Player {
        Log.d(TAG, "checkWinner: starts with $players")
        val result = players.maxByOrNull { it.getTotalResult() }!!
        Log.d(TAG, "checkWinner: ends with winner $result, with score ${result.getTotalResult()}")
        return result
    }
}