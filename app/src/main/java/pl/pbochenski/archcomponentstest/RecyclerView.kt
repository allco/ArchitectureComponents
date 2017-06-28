package pl.pbochenski.archcomponentstest

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Pawel Bochenski on 09.06.2017.
 */

fun <T> RecyclerView.Adapter<*>.autoUpdate(old: List<T>, new: List<T>, compare: (T, T) -> Boolean = { o1, o2 -> o1 == o2 }) {


    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return compare(old[oldItemPosition], new[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size
    })
    
    diff.dispatchUpdatesTo(this)
}

fun createAdapter(
        count: () -> Int,
        getItemType: (Int) -> Int,
        createVH: (ViewGroup?, Int) -> RecyclerView.ViewHolder,
        bind: (RecyclerView.ViewHolder, Int) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder> {

    return object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            bind(holder, position)
        }

        override fun getItemCount(): Int {
            return count()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return createVH(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            return getItemType(position)
        }
    }
}

fun inflate(parent: ViewGroup?, @LayoutRes layoutId: Int): View {
    return LayoutInflater.from(parent?.context).inflate(layoutId, parent, false)
}