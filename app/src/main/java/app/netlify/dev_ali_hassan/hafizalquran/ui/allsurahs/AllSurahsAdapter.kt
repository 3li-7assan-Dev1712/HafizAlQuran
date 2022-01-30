package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.netlify.dev_ali_hassan.hafizalquran.data.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.SurahListItemBinding

class AllSurahsAdapter : ListAdapter<Surah, AllSurahsAdapter.SurahViewHolder>(SurahDiffUtil()) {

    inner class SurahViewHolder(private val binding: SurahListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(surah: Surah) {
            binding.surahNumberTextView.text = "4"
            binding.surahNameTextView.text = surah.surahName
        }
    }

    /*
    * SurahDiffUtil class will be used to find the differences when updating the suah in the
    * database to notify the adapter and then update it in the UI*/
    class SurahDiffUtil : DiffUtil.ItemCallback<Surah>() {
        override fun areItemsTheSame(oldItem: Surah, newItem: Surah): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Surah, newItem: Surah): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val binding = SurahListItemBinding.inflate(LayoutInflater.from(parent.context))
        return SurahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        val surah = getItem(position)
        holder.bind(surah)
    }
}