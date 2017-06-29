package pl.pbochenski.archcomponentstest

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import pl.pbochenski.archcomponentstest.hackernews.Api
import pl.pbochenski.archcomponentstest.hackernews.Item
import ru.gildor.coroutines.retrofit.await
import timber.log.Timber

/**
 * Created by Pawel Bochenski on 09.06.2017.
 */
class PostRepo(private val api: Api) {
    private val posts = MutableLiveData<List<Item>>().apply { value = emptyList() }
    private var itemIds = emptyList<Long>()

    fun getPosts(): LiveData<List<Item>> {
        return posts
    }

    fun load(size: Int) = launch(UI) {
        Timber.d("load")
        itemIds = emptyList()
        posts.value = emptyList()
        itemIds = api.topStories().await()
        loadMore(0, size)
    }

    fun loadMore(position: Int, size: Int) = launch(UI) {
        Timber.d("load more $position $size")
        posts.value = getNewValues(posts.value, position, size, itemIds)
    }

    private suspend fun getNewValues(currentItems: List<Item>?, position: Int, size: Int, itemIds: List<Long>): List<Item> {
        return ArrayList(posts.value)
                .plus(get(itemIds.subList(position, minOf(position + size, itemIds.size))))
                .toSet() //remove duplicates
                .filter { it != null }
                .map { it as Item }
    }

    private suspend fun get(positions: List<Long>): List<Item?> {
        return positions.map {
            api.getItem(it).await()
        }
    }
}
