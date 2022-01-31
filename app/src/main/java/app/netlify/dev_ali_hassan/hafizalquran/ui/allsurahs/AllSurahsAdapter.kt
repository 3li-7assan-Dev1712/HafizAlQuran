package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.SurahListItemBinding

class AllSurahsAdapter(val context: Context) :
    ListAdapter<Surah, AllSurahsAdapter.SurahViewHolder>(SurahDiffUtil()) {

    inner class SurahViewHolder(private val binding: SurahListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(surah: Surah) {

            binding.apply {
                surahNumberTextView.text = surah.id.toString()
                surahNameTextView.text = surah.surahName
                pageCountTextView.text = surah.surahPagesCount.toString()
                if (surah.isSurahMakia) {
                    makiaMadaniaImageViewIndicator.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.makia_img,
                            context.theme
                        )
                    )
                } else {
                    makiaMadaniaImageViewIndicator.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.madania_img,
                            context.theme
                        )
                    )
                }

                when (surah.surahState) {
                    1 -> {
                        surahIsNotMemorizedImageView.setBackgroundColor(
                            context.resources.getColor(R.color.red)
                        )
                    }
                    2 -> {
                        surahIsNotMemorizedImageView.setBackgroundColor(
                            context.resources.getColor(R.color.orange)
                        )
                        surahNeedsRevisionImageView.setBackgroundColor(
                            context.resources.getColor(R.color.orange)
                        )
                    }
                    3 -> {
                        surahIsNotMemorizedImageView.setBackgroundColor(
                            context.resources.getColor(R.color.green)
                        )
                        surahNeedsRevisionImageView.setBackgroundColor(
                            context.resources.getColor(R.color.green)
                        )
                        surahIsMemorizedImageView.setBackgroundColor(
                            context.resources.getColor(R.color.green)
                        )
                    }
                }
            }
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