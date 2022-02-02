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
                            ResourcesCompat.getColor(context.resources, R.color.red, context.theme)
                        )
                        surahNeedsRevisionImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.white, context.theme)
                        )
                        surahIsNotMemorizedImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.white, context.theme)
                        )
                    }
                    2 -> {
                        surahIsNotMemorizedImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.orange, context.theme)
                        )
                        surahNeedsRevisionImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.orange, context.theme)
                        )
                        surahIsMemorizedImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.white, context.theme)
                        )
                    }
                    3 -> {
                        surahIsNotMemorizedImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.green, context.theme)
                        )
                        surahNeedsRevisionImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.green, context.theme)
                        )
                        surahIsMemorizedImageView.setBackgroundColor(
                            ResourcesCompat.getColor(context.resources, R.color.green, context.theme)
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