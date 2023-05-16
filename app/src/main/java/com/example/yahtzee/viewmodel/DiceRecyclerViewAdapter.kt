package com.example.yahtzee.viewmodel

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yahtzee.databinding.ItemDiceBinding
import com.example.yahtzee.model.Dice

private const val TAG = "DiceRecyclerViewAd"

class DiceRecyclerViewAdapter(private var diceList: List<Dice>, private val listener: OnDiceClickListener)
    : RecyclerView.Adapter<DiceViewHolder>(){

    interface OnDiceClickListener {
        fun onSaveClick(diceItem: Dice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiceViewHolder {
        val binding = ItemDiceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DiceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return diceList.size
    }

    override fun onBindViewHolder(holder: DiceViewHolder, position: Int) {
        holder.bind(diceList[position], listener)
    }

    fun loadNewDices(newDices: List<Dice>) {
        Log.d(TAG, "loadNewDices: starts with $newDices and old dices are $diceList")
        diceList = newDices
        notifyDataSetChanged()
    }
}