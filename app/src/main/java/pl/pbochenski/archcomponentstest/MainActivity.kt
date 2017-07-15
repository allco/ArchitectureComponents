package pl.pbochenski.archcomponentstest

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pl.pbochenski.archcomponentstest.framework.DefaultAdapter


class MainActivity : LifecycleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val adapter = DefaultAdapter(MainScreenVT.values().toList())
        content.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            model.refresh()
        }

        model.getPosts().observe(this, Observer {
            it?.let {
                swipeRefreshLayout.isRefreshing = false
                adapter.setItems(it.map { ItemData.Post(it) } + ItemData.Spinner)
                adapter.addOnItemBindListener(it.size, { model.loadMore() })
            }
        })

        model.refresh()
    }
}