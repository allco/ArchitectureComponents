package pl.pbochenski.archcomponentstest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import pl.pbochenski.archcomponentstest.framework.map

/**
 * Created by Pawel Bochenski on 22.06.2017.
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val postRepo = getApplication<App>().postRepo //poor man DI :D
    private var posts = emptyList<ItemData>()
    private val LOAD_ITEM_COUNT = 15


    fun getPosts(): LiveData<Pair<List<ItemData>, List<ItemData>>> {
        return postRepo.getPosts().map {
            val old = ArrayList(posts)
            posts = it.map { ItemData.Post(Post(it.id, it.title, it.url)) } + ItemData.Spinner
            Pair(old.toList(), posts)
        }
    }

    fun getItemCount() = posts.size
    fun getItemType(position: Int) = posts[position].type
    fun getItem(position: Int) = posts[position]
            .also {
                when (position) {
                    posts.size - 2 -> postRepo.loadMore(position, LOAD_ITEM_COUNT)
                }
            }

    fun refresh() {
        postRepo.load(LOAD_ITEM_COUNT)
    }
}