package com.example.speedtest_rework.ui.main.languages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.common.supportedLanguages
import com.example.speedtest_rework.databinding.ItemLanguageBinding

class LanguageAdapter(private val listener: TouchLanguageListener) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private val mLanguageList = supportedLanguages()
    private var selectedLanguageIndex = -1


    fun setCurrentLanguage(language: String) {
        mLanguageList.forEachIndexed { index, l ->
            if (l.language == language) {
                selectedLanguageIndex = index
                notifyDataSetChanged()
            }
        }
    }

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        with(holder) {
            if (this.adapterPosition == selectedLanguageIndex) {
                binding.imvChecked.visibility = View.VISIBLE
            } else {
                binding.imvChecked.visibility = View.GONE
            }
            with(mLanguageList[position]) {
                binding.tvCountry.text = this.displayLanguage
                binding.root.setOnClickListener {
                    selectedLanguageIndex = adapterPosition
                    listener.onClickLanguage(this)
                    notifyDataSetChanged()
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return mLanguageList.size
    }


}