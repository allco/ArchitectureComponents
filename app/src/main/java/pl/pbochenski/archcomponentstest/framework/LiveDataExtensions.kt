package pl.pbochenski.archcomponentstest.framework

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations

/**
 * Created by Pawel Bochenski on 28.06.2017.
 */
fun <T, R> LiveData<T>.map(f: (T) -> R): LiveData<R> = Transformations.map(this, f)

fun <T, R> LiveData<T>.switchMap(f: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, f)