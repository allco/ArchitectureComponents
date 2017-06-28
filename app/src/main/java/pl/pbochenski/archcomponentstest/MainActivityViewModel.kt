package pl.pbochenski.archcomponentstest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import timber.log.Timber

/**
 * Created by Pawel Bochenski on 22.06.2017.
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val postRepo = getApplication<App>().postRepo //poor man DI :D
    private var posts = emptyList<Post>()
    private val LOAD_ITEM_COUNT = 15


    fun getPosts(): LiveData<Pair<List<Post>, List<Post>>> {
        return postRepo.getPosts().map {
            val old = ArrayList(posts)
            posts = it.map {
                Post(it.id, it.title, it.url)
            }
            Pair(old.toList(), posts)
        }
    }

    fun getItemCount() = posts.size + 1 // +1 for spinner item

    fun getItem(position: Int): ItemData {
        Timber.d("getItem $position")
        return if (position == posts.size) {
            if (position == 0) {
                Timber.d("start loading")
                postRepo.load(LOAD_ITEM_COUNT)
            }
            ItemData.Spinner()
        } else {
            if (position == posts.size - 1 && position > LOAD_ITEM_COUNT - 1) {
                Timber.d("load more please")
                postRepo.loadMore(position, LOAD_ITEM_COUNT)
            }
            ItemData.Post(posts[position])
        }
    }

    fun getItemType(position: Int): Int {
        return if (position == posts.size) {
            ViewTypes.SPINNER.ordinal
        } else {
            ViewTypes.NORMAL.ordinal
        }
    }

    fun refresh() {
        postRepo.load(LOAD_ITEM_COUNT)
    }
}