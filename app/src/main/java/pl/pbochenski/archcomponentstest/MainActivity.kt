package pl.pbochenski.archcomponentstest

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pl.pbochenski.archcomponentstest.framework.autoUpdate
import pl.pbochenski.archcomponentstest.framework.createAdapter


class MainActivity : LifecycleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)

        content.adapter = createAdapter(
                { model.getItemCount() },
                { position -> model.getItemType(position) },
                { group, type -> ViewTypes.values()[type].createVH(group) },
                { vh, position -> ViewTypes.values().first { it.ordinal == vh.itemViewType }.bind(model.getItem(position), vh) }
        )

        swipeRefreshLayout.setOnRefreshListener {
            model.refresh()
        }

        model.getPosts().observe(this, Observer {
            it?.let {
                swipeRefreshLayout.isRefreshing = false
                content.adapter.autoUpdate(it.first, it.second, { o1, o2 -> o1.id == o2.id })
            }
        })
    }
}