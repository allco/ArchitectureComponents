package pl.pbochenski.archcomponentstest.hackernews

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Pawel Bochenski on 19.06.2017.
 */
data class Item(val id: Long,
                val deleted: Boolean,
                val type: Type,
                val by: String,
                val time: Long,
                val text: String?,
                val dead: Boolean?,
                val parent: Long?,
                val poll: Long?,
                val kids: List<Long>,
                val url: String?,
                val score: Int,
                val title: String,
                val parts: List<Long>?,
                val descendants: Int?)

enum class Type {
    job, story, comment, poll, pollopt
}


//base url: https://hacker-news.firebaseio.com/v0/
interface Api {
    @GET("maxitem.json")
    fun maxItem(): Call<Long>

    @GET("topstories.json")
    fun topStories(): Call<List<Long>>

    @GET("item/{id}.json")
    fun getItem(@Path("id") id: Long): Call<Item>
}