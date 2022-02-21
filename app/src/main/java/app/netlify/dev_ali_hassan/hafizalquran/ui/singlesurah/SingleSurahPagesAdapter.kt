package app.netlify.dev_ali_hassan.hafizalquran.ui.singlesurah

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.PageListItemBinding

class SingleSurahPagesAdapter(
    private val selectedSurah: Surah,
    val context: Context,
    val listener: OnPageIsClick
) :
    ListAdapter<Page, SingleSurahPagesAdapter.PageViewHolder>(PageDiffUtil()) {

    interface OnPageIsClick {
        fun onClickPage(clickedPage: Page)
    }

    inner class PageViewHolder(private val binding: PageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            // set a listener when the user select a specific Surah.
            binding.root.setOnClickListener {
                listener.onClickPage(getItem(adapterPosition))
            }
        }

        fun bind(page: Page) {

            binding.apply {
                pageSurahName.text = selectedSurah.surahName
                pageSurahNumber.text = page.pageNumber.toString()
                lockImageView.isVisible = !page.pageIsMemorized
                downloadStateImageView.isVisible = !page.isDownloaded
            }
        }
    }

    /*
    * SurahDiffUtil class will be used to find the differences when updating the suah in the
    * database to notify the adapter and then update it in the UI*/
    class PageDiffUtil : DiffUtil.ItemCallback<Page>() {
        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean =
            oldItem.pageNumber == newItem.pageNumber

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = PageListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val page = getItem(position)
        holder.bind(page)
    }
}