package pl.pbochenski.archcomponentstest

import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item.view.*
import timber.log.Timber

/**
 * Created by Pawel Bochenski on 22.06.2017.
 */
sealed class ItemData {
    class Spinner : ItemData()
    data class Post(val post: pl.pbochenski.archcomponentstest.Post) : ItemData()
}

enum class ViewTypes(val createVH: (ViewGroup?) -> RecyclerView.ViewHolder,
                     val bind: (ItemData, RecyclerView.ViewHolder) -> Unit) {
    NORMAL({ vg -> ListVH(inflate(vg, R.layout.item)) },
            { data, vh -> (vh as ListVH).bind((data as ItemData.Post).post) }),
    SPINNER({ vh -> SpinnerVH(inflate(vh, R.layout.spinner_item)) },
            { _, vh -> (vh as SpinnerVH).bind() })
}

class ListVH(view: View) : RecyclerView.ViewHolder(view) {

    val text = view.text!!

    fun bind(item: Post) {
        text.text = item.someText
        if (item.url != null) {
            itemView.setOnClickListener {
                ContextCompat.startActivity(itemView.context, Intent(Intent.ACTION_VIEW, Uri.parse(item.url)), null)
            }
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorPrimaryDark))
        }
    }

}

class SpinnerVH(view: View) : RecyclerView.ViewHolder(view) {
    fun bind() {
        Timber.d("Spinner bind")
    }
}