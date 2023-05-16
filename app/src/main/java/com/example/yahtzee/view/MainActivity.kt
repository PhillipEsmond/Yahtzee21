package com.example.yahtzee.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yahtzee.R
import com.example.yahtzee.databinding.ActivityMainBinding
import com.example.yahtzee.databinding.ContentMainBinding
import com.example.yahtzee.model.Dice
import com.example.yahtzee.model.Player
import com.example.yahtzee.model.Set
import com.example.yahtzee.model.YahtzeeConstants
import com.example.yahtzee.viewmodel.DiceRecyclerViewAdapter
import com.example.yahtzee.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"
private const val DIALOGUE_NEW_GAME = 1

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NewGameDialogue.NewGameDialogEvents, DiceRecyclerViewAdapter.OnDiceClickListener {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var contentMainBinding: ContentMainBinding

    private val diceRecyclerViewAdapter = DiceRecyclerViewAdapter(ArrayList(), this)

    private var currentPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainActivityBinding.root
        setContentView(view)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setSupportActionBar(findViewById(R.id.toolbar))

        contentMainBinding = ContentMainBinding.bind(view)

        contentMainBinding.rvDices.layoutManager = LinearLayoutManager (
            this,
            LinearLayoutManager.HORIZONTAL,
            false
                )
        contentMainBinding.rvDices.adapter = diceRecyclerViewAdapter

        contentMainBinding.btnRoll.setOnClickListener {
            mainViewModel.rollDices()
        }

        mainViewModel.currentPlayersLD.observe(this
        ) { players ->
            setFirstPlayer(players[0])
            setSecondPlayer(players[1])
            currentPlayer = players.find { it.playerTurn }

            if (currentPlayer == players[0]) {
                changeStateFirstPlayerBoxScores(true)
                changeStateSecondPlayerBoxScores(false)
            } else if (currentPlayer == players[1]) {
                changeStateSecondPlayerBoxScores(true)
                changeStateFirstPlayerBoxScores(true)
            }
            setPlayerTurnOn(currentPlayer!!)
        }

        mainViewModel.toastSetLD.observe(this) {
            it?.let {
                if (it) {
                    Toast.makeText(this, getString(R.string.toast_set_filled), Toast.LENGTH_SHORT).show()
                    mainViewModel.onSetToastShown()
                }
            }
        }

        mainViewModel.winnerLD.observe(this) {
            if (it.isNotEmpty()) {
                contentMainBinding.tvWinnerResult.text = getString(R.string.tv_winner_message, it)
            } else {
                contentMainBinding.tvWinnerResult.text = ""
            }
        }

        val setListener = intializeSetScoreListener()

        contentMainBinding.apply {
            etDiceOneFirstPlayer.setOnClickListener(setListener)
            etDiceTwoFirstPlayer.setOnClickListener(setListener)
            etDiceThreeFirstPlayer.setOnClickListener(setListener)
            edDiceFourFirstPlayer.setOnClickListener(setListener)
            etDicefiveFirstPlayer.setOnClickListener(setListener)
            etDiceSixFirstPlayer.setOnClickListener(setListener)
            etThreeOfKindFirstPlayer.setOnClickListener(setListener)
            etFourOfKindFirstPlayer.setOnClickListener(setListener)
            etFullHouseFirstPlayer.setOnClickListener(setListener)
            etSmallStraightFirstPlayer.setOnClickListener(setListener)
            etLargeStraightFirstPlayer.setOnClickListener(setListener)
            etYahtzeeFirstPlayer.setOnClickListener(setListener)
            etChanceFirstPlayer.setOnClickListener(setListener)

            etDiceOneSecondPlayer.setOnClickListener(setListener)
            etDiceTwoSecondPlayer.setOnClickListener(setListener)
            etDiceThreeSecondPlayer.setOnClickListener(setListener)
            edDiceFourSecondPlayer.setOnClickListener(setListener)
            etDicefiveSecondPlayer.setOnClickListener(setListener)
            etDiceSixSecondPlayer.setOnClickListener(setListener)
            etThreeOfKindSecondPlayer.setOnClickListener(setListener)
            etFourOfKindSecondPlayer.setOnClickListener(setListener)
            etFullHouseSecondPlayer.setOnClickListener(setListener)
            etSmallStraightSecondPlayer.setOnClickListener(setListener)
            etLargeStraightSecondPlayer.setOnClickListener(setListener)
            etYahtzeeSecondPlayer.setOnClickListener(setListener)
            etChanceSecondPlayer.setOnClickListener(setListener)
        }

        mainViewModel.createGame(emptyArray())

        Log.d(TAG, "onCreate:ends")
    }

    override fun onCreateDialogResults(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onCreateDialogResult: called with dialogId $dialogId")
        if(dialogId == DIALOGUE_NEW_GAME) {
            val players = args.getStringArray(NEW_GAME_DIALOGUE_PLAYERS_NAME)
            Log.d(
                TAG,
                "onCreateDialogResult: called with dialogId $dialogId, players name $players"
            )
            clearForm( contentMainBinding.linearLayoutScores)
            mainViewModel.createGame(players!!)
        }
        Log.d(TAG, "onCreateDialogResult: ends")
    }

    private fun clearForm(group: ViewGroup) {
        var i = 0
        val count = group.childCount
        while (i < count) {
            val view = group.getChildAt(i)
            if(view is EditText) {
                view.setText("")
            }
            if (view is ViewGroup && view.childCount > 0) clearForm(view)
            ++i
        }
    }

    override fun onSaveClick(diceItem: Dice) {
        Log.d(TAG, "onSaveClick: called with $diceItem")
        mainViewModel.savePlayerDice(diceItem)
        Log.d(TAG, "onSaveClick: ends with $diceItem")
    }

    private fun changeStateFirstPlayerBoxScores(enable: Boolean) {

        contentMainBinding.apply {
            enable.let {

                etDiceOneFirstPlayer.isEnabled = it
                etDiceTwoFirstPlayer.isEnabled = it
                etDiceThreeFirstPlayer.isEnabled = it
                edDiceFourFirstPlayer.isEnabled = it
                etDicefiveFirstPlayer.isEnabled = it
                etDiceSixFirstPlayer.isEnabled = it
                etThreeOfKindFirstPlayer.isEnabled = it
                etFourOfKindFirstPlayer.isEnabled = it
                etFullHouseFirstPlayer.isEnabled = it
                etSmallStraightFirstPlayer.isEnabled = it
                etLargeStraightFirstPlayer.isEnabled = it
                etYahtzeeFirstPlayer.isEnabled = it
                etChanceFirstPlayer.isEnabled = it

            }
        }
    }

    private fun changeStateSecondPlayerBoxScores(enable: Boolean) {

        contentMainBinding.apply {
            enable.let {

                etDiceOneSecondPlayer.isEnabled = it
                etDiceTwoSecondPlayer.isEnabled = it
                etDiceThreeSecondPlayer.isEnabled = it
                edDiceFourSecondPlayer.isEnabled = it
                etDicefiveSecondPlayer.isEnabled = it
                etDiceSixFirstPlayer.isEnabled = it
                etThreeOfKindSecondPlayer.isEnabled = it
                etFourOfKindSecondPlayer.isEnabled = it
                etFullHouseSecondPlayer.isEnabled = it
                etSmallStraightSecondPlayer.isEnabled = it
                etLargeStraightSecondPlayer.isEnabled = it
                etYahtzeeSecondPlayer.isEnabled = it
                etChanceSecondPlayer.isEnabled = it

            }
        }
    }

    private fun setPlayerTurnOn(player: Player) {
        diceRecyclerViewAdapter.loadNewDices(player.diceToRoll)
        when {
            player.rollCount <= 0 -> {
                contentMainBinding.btnRoll.isEnabled = false
            } else -> {
                contentMainBinding.btnRoll.isEnabled = true
            }
        }

        contentMainBinding.btnRoll.text = getString(
            R.string.btn_roll_in_game,
            player.name,
            player.rollCount.toString()
        )
    }

    private fun setFirstPlayer(firstPlayer: Player) {

        contentMainBinding.apply {
            firstPlayer.name.let {
                tvFirstPlayerLeft.text = it
                tvFirstPlayerRight.text = it
                tvFirstPlayerName.text = it
            }

                firstPlayer.setScores.forEach {
                    when (it.key) {
                        Set.ACES -> {
                            etDiceOneFirstPlayer.setText(it.value.toString())
                        }
                        Set.TWOS -> {
                            etDiceTwoFirstPlayer.setText(it.value.toString())
                        }
                        Set.THREES -> {
                            etDiceThreeFirstPlayer.setText(it.value.toString())
                        }
                        Set.FOURS -> {
                            edDiceFourFirstPlayer.setText(it.value.toString())
                        }
                        Set.FIVES -> {
                            etDicefiveFirstPlayer.setText(it.value.toString())
                        }
                        Set.SIXES -> {
                            etDiceSixFirstPlayer.setText(it.value.toString())
                        }
                        Set.THREE_OF_A_KIND -> {
                            etFourOfKindFirstPlayer.setText(it.value.toString())
                        }
                        Set.FOUR_OF_A_KIND -> {
                            etFourOfKindFirstPlayer.setText(it.value.toString())
                        }
                        Set.FULL_HOUSE -> {
                            etFullHouseFirstPlayer.setText(it.value.toString())
                        }
                        Set.SMALL_STRAIGHT -> {
                            etSmallStraightFirstPlayer.setText(it.value.toString())
                        }
                        Set.LARGE_STRAIGHT -> {
                            etLargeStraightFirstPlayer.setText(it.value.toString())
                        }
                        Set.YAHTZEE -> {
                            etYahtzeeFirstPlayer.setText(it.value.toString())
                        }
                        Set.CHANCE -> {
                            etChanceFirstPlayer.setText(it.value.toString())
                        }
                    }
                }

                if(firstPlayer.rollCount != YahtzeeConstants.GameValues.ROLLING_ALLOWED) {
                    etDiceOneFirstPlayer.hint = firstPlayer.chooseSet(Set.ACES).toString()
                    etDiceTwoFirstPlayer.hint = firstPlayer.chooseSet(Set.TWOS).toString()
                    etDiceThreeFirstPlayer.hint = firstPlayer.chooseSet(Set.THREES).toString()
                    edDiceFourFirstPlayer.hint = firstPlayer.chooseSet(Set.FOURS).toString()
                    etDicefiveFirstPlayer.hint = firstPlayer.chooseSet(Set.FIVES).toString()
                    etDiceSixFirstPlayer.hint = firstPlayer.chooseSet(Set.SIXES).toString()
                    etThreeOfKindFirstPlayer.hint = firstPlayer.chooseSet(Set.THREE_OF_A_KIND).toString()
                    etFourOfKindFirstPlayer.hint = firstPlayer.chooseSet(Set.FOUR_OF_A_KIND).toString()
                    etFullHouseFirstPlayer.hint = firstPlayer.chooseSet(Set.FULL_HOUSE).toString()
                    etSmallStraightFirstPlayer.hint = firstPlayer.chooseSet(Set.SMALL_STRAIGHT).toString()
                    etLargeStraightFirstPlayer.hint = firstPlayer.chooseSet(Set.LARGE_STRAIGHT).toString()
                    etYahtzeeFirstPlayer.hint = firstPlayer.chooseSet(Set.YAHTZEE).toString()
                    etChanceFirstPlayer.hint = firstPlayer.chooseSet(Set.CHANCE).toString()
                } else {
                    etDiceOneFirstPlayer.hint = ""
                    etDiceTwoFirstPlayer.hint = ""
                    etDiceThreeFirstPlayer.hint = ""
                    edDiceFourFirstPlayer.hint = ""
                    etDicefiveFirstPlayer.hint = ""
                    etDiceSixFirstPlayer.hint = ""
                    etThreeOfKindFirstPlayer.hint = ""
                    etFourOfKindFirstPlayer.hint = ""
                    etSmallStraightFirstPlayer.hint = ""
                    etLargeStraightFirstPlayer.hint = ""
                    etFullHouseFirstPlayer.hint = ""
                    etYahtzeeFirstPlayer.hint = ""
                    etChanceFirstPlayer.hint = ""
                }

                etBonusFirstPlayer.setText(
                    getString(
                        R.string.et_bonus_value,
                        firstPlayer.getBonusValue().toString()
                    )
                )

                tvFirstPlayerResult.text = firstPlayer.getTotalResult().toString()
        }
    }

    private fun setSecondPlayer(secondPlayer: Player) {

        contentMainBinding.apply {
            secondPlayer.name.let {
                tvSecondPlayerLeft.text = it
                tvSecondPlayerRight.text = it
                tvSecondPlayerName.text = it
            }

                secondPlayer.setScores.forEach {
                    when (it.key) {
                        Set.ACES -> {
                            etDiceOneSecondPlayer.setText(it.value.toString())
                        }
                        Set.TWOS -> {
                            etDiceTwoSecondPlayer.setText(it.value.toString())
                        }
                        Set.THREES -> {
                            etDiceThreeSecondPlayer.setText(it.value.toString())
                        }
                        Set.FOURS -> {
                            edDiceFourSecondPlayer.setText(it.value.toString())
                        }
                        Set.FIVES -> {
                            etDicefiveSecondPlayer.setText(it.value.toString())
                        }
                        Set.SIXES -> {
                            etDiceSixSecondPlayer.setText(it.value.toString())
                        }
                        Set.THREE_OF_A_KIND -> {
                            etFourOfKindSecondPlayer.setText(it.value.toString())
                        }
                        Set.FOUR_OF_A_KIND -> {
                            etFourOfKindSecondPlayer.setText(it.value.toString())
                        }
                        Set.FULL_HOUSE -> {
                            etFullHouseSecondPlayer.setText(it.value.toString())
                        }
                        Set.SMALL_STRAIGHT -> {
                            etSmallStraightSecondPlayer.setText(it.value.toString())
                        }
                        Set.LARGE_STRAIGHT -> {
                            etLargeStraightSecondPlayer.setText(it.value.toString())
                        }
                        Set.YAHTZEE -> {
                            etYahtzeeSecondPlayer.setText(it.value.toString())
                        }
                        Set.CHANCE -> {
                            etChanceSecondPlayer.setText(it.value.toString())
                        }
                    }
                }

                if(secondPlayer.rollCount != YahtzeeConstants.GameValues.ROLLING_ALLOWED) {
                    etDiceOneSecondPlayer.hint = secondPlayer.chooseSet(Set.ACES).toString()
                    etDiceTwoSecondPlayer.hint = secondPlayer.chooseSet(Set.TWOS).toString()
                    etDiceThreeSecondPlayer.hint = secondPlayer.chooseSet(Set.THREES).toString()
                    edDiceFourSecondPlayer.hint = secondPlayer.chooseSet(Set.FOURS).toString()
                    etDicefiveSecondPlayer.hint = secondPlayer.chooseSet(Set.FIVES).toString()
                    etDiceSixSecondPlayer.hint = secondPlayer.chooseSet(Set.SIXES).toString()
                    etThreeOfKindSecondPlayer.hint = secondPlayer.chooseSet(Set.THREE_OF_A_KIND).toString()
                    etFourOfKindSecondPlayer.hint = secondPlayer.chooseSet(Set.FOUR_OF_A_KIND).toString()
                    etFullHouseSecondPlayer.hint = secondPlayer.chooseSet(Set.FULL_HOUSE).toString()
                    etSmallStraightSecondPlayer.hint = secondPlayer.chooseSet(Set.SMALL_STRAIGHT).toString()
                    etLargeStraightSecondPlayer.hint = secondPlayer.chooseSet(Set.LARGE_STRAIGHT).toString()
                    etYahtzeeSecondPlayer.hint = secondPlayer.chooseSet(Set.YAHTZEE).toString()
                    etChanceSecondPlayer.hint = secondPlayer.chooseSet(Set.CHANCE).toString()
                } else {
                    etDiceOneSecondPlayer.hint = ""
                    etDiceTwoSecondPlayer.hint = ""
                    etDiceThreeSecondPlayer.hint = ""
                    edDiceFourSecondPlayer.hint = ""
                    etDicefiveSecondPlayer.hint = ""
                    etDiceSixSecondPlayer.hint = ""
                    etThreeOfKindSecondPlayer.hint = ""
                    etFourOfKindSecondPlayer.hint = ""
                    etSmallStraightSecondPlayer.hint = ""
                    etLargeStraightSecondPlayer.hint = ""
                    etFullHouseSecondPlayer.hint = ""
                    etYahtzeeSecondPlayer.hint = ""
                    etChanceSecondPlayer.hint = ""
                }

                etBonusSecondPlayer.setText(
                    getString(
                        R.string.et_bonus_value,
                        secondPlayer.getBonusValue().toString()
                    )
                )

                tvSecondPlayerResult.text = secondPlayer.getTotalResult().toString()
        }
    }

    private fun intializeSetScoreListener() : View.OnClickListener {
        return View.OnClickListener { v ->

            if(currentPlayer?.rollCount != YahtzeeConstants.GameValues.ROLLING_ALLOWED) {
                contentMainBinding.apply {
                    when (v) {
                        etDiceOneFirstPlayer, etDiceOneSecondPlayer -> {
                            mainViewModel.scoreSet(Set.ACES)
                        }
                        etDiceTwoFirstPlayer, etDiceTwoSecondPlayer -> {
                            mainViewModel.scoreSet(Set.TWOS)
                        }
                        etDiceThreeFirstPlayer, etDiceThreeSecondPlayer -> {
                            mainViewModel.scoreSet(Set.THREES)
                        }
                        edDiceFourFirstPlayer, edDiceFourSecondPlayer -> {
                            mainViewModel.scoreSet(Set.FOURS)
                        }
                        etDicefiveFirstPlayer, etDicefiveSecondPlayer -> {
                            mainViewModel.scoreSet(Set.FIVES)
                        }
                        etDiceSixFirstPlayer, etDiceSixSecondPlayer -> {
                            mainViewModel.scoreSet(Set.SIXES)
                        }
                        etThreeOfKindFirstPlayer, etThreeOfKindFirstPlayer -> {
                            mainViewModel.scoreSet(Set.THREE_OF_A_KIND)
                        }
                        etFourOfKindFirstPlayer, etFourOfKindSecondPlayer -> {
                            mainViewModel.scoreSet(Set.FOUR_OF_A_KIND)
                        }
                        etFullHouseFirstPlayer, etFullHouseSecondPlayer -> {
                            mainViewModel.scoreSet(Set.FULL_HOUSE)
                        }
                        etSmallStraightFirstPlayer, etSmallStraightSecondPlayer -> {
                            mainViewModel.scoreSet(Set.SMALL_STRAIGHT)
                        }
                        etLargeStraightFirstPlayer, etLargeStraightSecondPlayer -> {
                            mainViewModel.scoreSet(Set.LARGE_STRAIGHT)
                        }
                        etYahtzeeFirstPlayer, etYahtzeeSecondPlayer -> {
                            mainViewModel.scoreSet(Set.YAHTZEE)
                        }
                        etChanceFirstPlayer, etChanceSecondPlayer -> {
                        mainViewModel.scoreSet(Set.CHANCE)
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_need_roll), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: starts")
        menuInflater.inflate(R.menu.menu_main, menu)
        Log.d(TAG, "onCreateOptionsMenu: ends")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: starts")
        when (item.itemId) {
            R.id.action_new_game -> {
                val dialog = NewGameDialogue()
                val args = Bundle().apply {
                    putInt(NEW_GAME_DIALOGUE_ID, DIALOGUE_NEW_GAME)
                }
                dialog.arguments = args
                dialog.show(supportFragmentManager, "newGame")
            }
        }
        Log.d(TAG, "onOptionsItemSelected: ends")
        return super.onOptionsItemSelected(item)
    }
}