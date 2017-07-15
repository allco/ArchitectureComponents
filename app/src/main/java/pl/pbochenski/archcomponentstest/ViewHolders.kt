package pl.pbochenski.archcomponentstest

import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item.view.*
import pl.pbochenski.archcomponentstest.framework.VHBinder
import pl.pbochenski.archcomponentstest.framework.ViewHolderType
import pl.pbochenski.archcomponentstest.framework.inflate

/**
 * Created by Pawel Bochenski on 22.06.2017.
 */
//data -> view holder type mapping
sealed class ItemData(override val type: Int) : ViewHolderType {
    data class Post(val post: pl.pbochenski.archcomponentstest.Post) : ItemData(0)
    object Spinner : ItemData(1)
}

//view holderBinders
enum class MainScreenVT : VHBinder<ItemData> {
    NORMAL {
        override val type: Int = 0
        override val createVH: (ViewGroup?) -> RecyclerView.ViewHolder = { vg -> ListVH(inflate(vg, R.layout.item)) }
        override val bind: (ItemData, RecyclerView.ViewHolder) -> Unit = { data, vh -> (vh as ListVH).bind((data as ItemData.Post).post) }
    },
    SPINNER {
        override val type: Int = 1
        override val createVH: (ViewGroup?) -> RecyclerView.ViewHolder = { vg -> SpinnerVH(inflate(vg, R.layout.spinner_item)) }
        override val bind: (ItemData, RecyclerView.ViewHolder) -> Unit = { _, _ -> }
    }
}

//view holders
class ListVH(view: View) : RecyclerView.ViewHolder(view) {

    val text = view.text!!

    fun bind(item: Post) {
        text.text = item.someText
        if (item.url != null) {
            itemView.setOnClickListener {
                ContextCompat.startActivity(itemView.context, Intent(Intent.ACTION_VIEW, Uri.parse(item.url)), null)
            }
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.white))
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorPrimaryDark))
        }
    }

}

class SpinnerVH(view: View) : RecyclerView.ViewHolder(view)