package pl.pbochenski.archcomponentstest.framework

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
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


interface VHBinder<in T> : ViewHolderType {
    val createVH: (ViewGroup?) -> RecyclerView.ViewHolder
    val bind: (T, RecyclerView.ViewHolder) -> Unit
}

interface ViewHolderType {
    val type: Int
}

typealias Func0 = () -> Unit

class DefaultAdapter<in K : ViewHolderType, out T : VHBinder<K>>(private val binder: List<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var bindActions = SparseArray<Func0>()
    private var items: List<K> = emptyList()

    fun setItems(newItems: List<K>) {
        val old = items
        autoUpdate(old, newItems)
        items = newItems
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        binder.first { it.type == items[position].type }.bind(items[position], holder)
        bindActions[position]?.invoke()
    }

    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = binder.first { it.type == viewType }.createVH(parent)
    override fun getItemViewType(position: Int) = items[position].type
    fun addOnItemBindListener(position: Int, action: Func0) = bindActions.put(position, action)
    fun removeOnItemBindListener(position: Int) = bindActions.delete(position)
}

fun inflate(parent: ViewGroup?, @LayoutRes layoutId: Int): View {
    return LayoutInflater.from(parent?.context).inflate(layoutId, parent, false)
}