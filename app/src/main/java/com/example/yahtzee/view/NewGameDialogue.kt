package com.example.yahtzee.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.yahtzee.R
import com.example.yahtzee.databinding.ActivityNewGameDialogueBinding

private const val TAG = "NewGameDialogue"

const val NEW_GAME_DIALOGUE_ID = "ID"
const val NEW_GAME_DIALOGUE_PLAYERS_NAME = "PLAYERS NAME"

class NewGameDialogue : AppCompatDialogFragment() {

    private lateinit var binding: ActivityNewGameDialogueBinding
    private var dialogId = 0

    interface NewGameDialogEvents {
        fun onCreateDialogResults(dialogId: Int, args: Bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NewGameDialogueStyle)

        val arguments = arguments

        if(arguments != null) {

            dialogId = arguments.getInt(NEW_GAME_DIALOGUE_ID)
        }

        Log.d(TAG, "onCreate: ends, arguments: dialog in $dialogId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        binding = ActivityNewGameDialogueBinding.inflate(inflater, container, false)

        Log.d(TAG, "onCreateView: ends")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: starts")
        super.onViewCreated(view, savedInstanceState)
        dialog?.setTitle("New Game")

        with(binding) {
            createButton.setOnClickListener {
                when {
                    etFirstPlayerName.text?.isEmpty()!! -> {
                        etFirstPlayerName.error = getString(R.string.error_valid_nickname)
                    }
                    etSecondPlayerName.text?.isEmpty()!! -> {
                        etSecondPlayerName.error = getString(R.string.error_valid_nickname)
                    } else -> {
                        var args = Bundle().apply {
                            putStringArray(NEW_GAME_DIALOGUE_PLAYERS_NAME,
                            arrayOf(etFirstPlayerName.text.toString(), etSecondPlayerName.text.toString())
                            )
                        }
                    Log.d(TAG, "setOnClickListener: starts with $args")
                    (activity as NewGameDialogEvents).onCreateDialogResults(dialogId, args)
                    dismiss()
                    }
                }
            }
            cancelButton.setOnClickListener { dismiss() }
        }
        Log.d(TAG, "onViewCreated: ends")
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach called: context is $context")
        super.onAttach(context)

        if (context !is NewGameDialogEvents) {
            throw java.lang.RuntimeException("$context must implement NewGameDialog.NewGameDialogEvents interface")
        }
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach called")
        super.onDetach()
    }
}