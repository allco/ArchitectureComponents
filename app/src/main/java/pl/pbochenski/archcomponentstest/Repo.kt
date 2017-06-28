package pl.pbochenski.archcomponentstest

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import pl.pbochenski.archcomponentstest.hackernews.Api
import pl.pbochenski.archcomponentstest.hackernews.Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    fun load(size: Int) {
        itemIds = emptyList()
        posts.value = emptyList()
        api.topStories().enqueue(object : Callback<List<Long>?> {
            override fun onResponse(call: Call<List<Long>?>?, response: Response<List<Long>?>?) {
                if (response?.isSuccessful ?: false) {
                    itemIds = response?.body() ?: emptyList()
                    loadMore(0, size)
                }
            }

            override fun onFailure(call: Call<List<Long>?>?, t: Throwable?) {
                Timber.e(t)
            }
        })
    }

    fun loadMore(position: Int, size: Int) {
        Timber.d("loadmore $position - $size")

        (position..minOf(position + size, itemIds.size))
                .map {
                    api.getItem(itemIds[it]).enqueue(object : Callback<Item?> {
                        override fun onResponse(call: Call<Item?>?, response: Response<Item?>?) {
                            posts.value = posts.value
                                    ?.plus(response?.body())
                                    ?.toSet()
                                    ?.filter { it != null }
                                    ?.map { it as Item }
                        }

                        override fun onFailure(call: Call<Item?>?, t: Throwable?) {
                            Timber.e(t)
                        }
                    })
                }
    }
}

fun <T, R> LiveData<T>.map(f: (T) -> R): LiveData<R> = Transformations.map(this, f)
fun <T, R> LiveData<T>.flatMap(f: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, f)