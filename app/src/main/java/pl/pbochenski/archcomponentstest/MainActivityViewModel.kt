package pl.pbochenski.archcomponentstest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import pl.pbochenski.archcomponentstest.framework.map
import timber.log.Timber

/**
 * Created by Pawel Bochenski on 22.06.2017.
 */

data class Post(val id: Long,
                val someText: String,
                val url: String?)


class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val postRepo = getApplication<App>().postRepo //poor man DI :D
    private val LOAD_ITEM_COUNT = 15

    fun getPosts(): LiveData<List<Post>> {
        return postRepo.posts.map {
            Timber.d("have posts")
            it.map { Post(it.id, it.title, it.url) }
        }
    }

    fun refresh() {
        Timber.d("refresh")
        postRepo.load(LOAD_ITEM_COUNT)
    }

    fun loadMore() {
        Timber.d("loadmore")
        postRepo.loadMore(LOAD_ITEM_COUNT)
    }
}