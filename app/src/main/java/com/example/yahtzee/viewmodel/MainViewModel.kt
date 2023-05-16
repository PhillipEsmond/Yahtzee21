package com.example.yahtzee.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yahtzee.model.Dice
import com.example.yahtzee.model.Game
import com.example.yahtzee.model.Player
import com.example.yahtzee.model.Set
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val _currentPlayersMLD = MutableLiveData<ArrayList<Player>>()
    val currentPlayersLD: LiveData<ArrayList<Player>>
    get() = _currentPlayersMLD

    private val _toastSetMLD = MutableLiveData<Boolean?>()
    val toastSetLD: LiveData<Boolean?>
    get() = _toastSetMLD

    private val _winnerMLD = MutableLiveData<String>()
    val winnerLD: LiveData<String>
    get() = _winnerMLD

    private var currentGame: Game? = null

    private var currentPlayer: Player? = null

    fun createGame(players: Array<String>) {
        Log.d(TAG, "createGame: starts with $players")
        val playersList = ArrayList<Player>()
        players.forEach {
            playersList.add(Player(name = it))
        }
        currentGame = if (playersList.isNotEmpty()) {
            Game(playersList)
        } else {
            Game()
        }
        currentPlayer = currentGame!!.nextPlayer()
        _currentPlayersMLD.value = currentGame!!.players
        _winnerMLD.value = ""
        Log.d(TAG, "createGame: ends WITH ${_currentPlayersMLD.value}")
    }

    fun rollDices() {
        Log.d(TAG, "rollDices: starts with $currentPlayer")

        currentPlayer?.rollDice()

        _currentPlayersMLD.value = currentGame!!.players
        Log.d(TAG, "rollDices: ends")
    }

    fun savePlayerDice(die: Dice) {
        Log.d(TAG, "savePlayerDice: starts with $die")

        currentPlayer?.saveDice(die)

        _currentPlayersMLD.value = currentGame!!.players
        Log.d(TAG, "savePlayerDice: ends")
    }

    fun scoreSet(set: Set) {
        Log.d(TAG, "scoreSet: starts with $set and current value of player set ${currentPlayer?.setScores!![set]}")
        val isSuccessful = currentPlayer?.saveScore(set)

        if(isSuccessful!!) {
            if(currentGame?.checkGameOver()!!) {
                _winnerMLD.value = currentGame!!.checkWinner().name
            }
            currentPlayer = currentGame?.nextPlayer()
            _currentPlayersMLD.value = currentGame!!.players
        }
        else {
            _toastSetMLD.value = true
        }
        Log.d(TAG, "scoreSet: ends with ${currentPlayer?.setScores!![set]}")
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: starts")
        super.onCleared()
        Log.d(TAG, "onCleared: ends")
    }

    fun onSetToastShown() {
        Log.d(TAG, "onSetToastShown: starts with ${_toastSetMLD.value}")
        _toastSetMLD.value = null
        Log.d(TAG, "onSetToastShown: ends with ${_toastSetMLD.value}")
    }

}