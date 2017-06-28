package pl.pbochenski.archcomponentstest

import android.app.Application
import pl.pbochenski.archcomponentstest.hackernews.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber

/**
 * Created by Pawel Bochenski on 22.06.2017.
 */
class App : Application() {
    val postRepo by lazy {
        PostRepo(Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java))
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}