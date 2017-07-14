package pl.pbochenski.archcomponentstest

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.pbochenski.archcomponentstest.hackernews.Item
import pl.pbochenski.archcomponentstest.hackernews.Type

/**
 * Created by Pawel Bochenski on 29.06.2017.
 */

class MainViewModelTest {

    val repo = mock<PostRepo>()
    lateinit var liveData: MutableLiveData<List<Item>>
    lateinit var model: MainViewModel

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        reset(repo)
        val app = mock<App> {
            on { postRepo } doReturn (repo)
        }

        liveData = MutableLiveData<List<Item>>()
        repo.stub {
            on { getPosts() } doReturn liveData
        }

        model = MainViewModel(app)
    }

    @Test
    fun shouldLoadRepoOnRefresh() {
        model.refresh()
        verify(repo).load(15)
    }


    @Test
    fun shouldGetPostsAfterUpdate() {
        //given
        val returnedItem = createDummyItem()
        val observer = mock<Observer<List<Post>>>()
        model.getPosts().observeForever(observer)

        //when
        liveData.value = listOf(returnedItem)

        //than
        verify(observer).onChanged(listOf(Post(returnedItem.id, returnedItem.title, returnedItem.url)))
    }

    private fun createDummyItem() = Item(1, false, Type.story, "", 123, "123", false, null, null, emptyList(), null, 1, "test", null, null)
}