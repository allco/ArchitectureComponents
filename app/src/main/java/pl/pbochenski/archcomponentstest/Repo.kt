package pl.pbochenski.archcomponentstest

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.pbochenski.archcomponentstest.framework.switchMap
import pl.pbochenski.archcomponentstest.hackernews.Api
import pl.pbochenski.archcomponentstest.hackernews.Item
import timber.log.Timber

/**
 * Created by Pawel Bochenski on 09.06.2017.
 */
class PostRepo(private val api: Api) {

    private sealed class Command {
        class Reset(val size: Int) : Command()
        class LoadMore(val position: Int, val size: Int) : Command()
    }

    private var itemIds = emptyList<Long>()

    private val commands = MutableLiveData<Command>()

    val posts: LiveData<List<Item>> = commands.switchMap {
        Timber.d("switch map")
        val command = it
        LiveDataReactiveStreams.fromPublisher<List<Item>> {
            when (command) {
                is Command.Reset -> {
                    Timber.d("reset command")
                    getFirst(command.size)
                }
                is PostRepo.Command.LoadMore -> {
                    Timber.d("load more command")
                    getMore(command.position, command.size)
                }
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toFlowable()
                    .subscribe(it)
        }

    }

    private fun getMore(position: Int, size: Int): Single<List<Item>> {
        val getSingle = get(itemIds.subList(position, position + size))
        return Single.zip(getSingle, { it.map { it as Item } }).flatMap {
            val current = posts.value ?: emptyList() //static data used. maybe promote to parameter?
            val toReturn = (current + it).toSet().toList()
            Single.just(toReturn)
        }
    }

    private fun getFirst(size: Int): Single<List<Item>> {
        return api.topStories().map {
            itemIds = it //look out for side effect
            get(it.take(size))
        }.flatMap {
            Single.zip(it, { it.map { it as Item } })
        }

    }


    private fun get(positions: List<Long>): List<Single<Item>> {
        return positions.map {
            api.getItem(it)
        }
    }

    fun load(size: Int) {
        Timber.d("load")
        itemIds = emptyList()

        commands.value = Command.Reset(size)
    }

    fun loadMore(size: Int) {
        Timber.d("load more $size")

        commands.value = Command.LoadMore(posts.value?.size ?: 0, size)
    }
}
