package com.example.yahtzee.viewmodel

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.yahtzee.R
import com.example.yahtzee.databinding.ItemDiceBinding
import com.example.yahtzee.model.Dice
import com.example.yahtzee.model.DieResult

private const val TAG = "DiceViewHolder"

class DiceViewHolder(private val binding: ItemDiceBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(dice: Dice, listener: DiceRecyclerViewAdapter.OnDiceClickListener) {

        Log.d(TAG, "bind: starts with $dice and listener $listener")

        binding.ivDice.setImageResource(when(dice.result) {
            DieResult.ONE -> R.drawable.diceone
            DieResult.TWO -> R.drawable.dicetwo
            DieResult.THREE -> R.drawable.dicethree
            DieResult.FOUR -> R.drawable.dicefour
            DieResult.FIVE -> R.drawable.dicefive
            DieResult.SIX -> R.drawable.dicesix
            DieResult.NOT_ROLLED -> R.drawable.blankdice
        })

        if(dice.savedDie) {
            binding.ivDice.background = itemView.context.getDrawable(R.drawable.corner_clicked)
        } else {
            binding.ivDice.setBackgroundColor(itemView.context.getMyColor(android.R.color.transparent))
        }

        binding.ivDice.setOnClickListener {
            listener.onSaveClick(dice)
        }

        Log.d(TAG, "bind: ends with $dice and listener $listener")
    }
}