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
typealias ViewModelReturnType = Pair<List<ItemData>, List<ItemData>>

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
    fun shouldGetPostsAndHaveACopyOfIt() {
        //given
        val returnedItem = createDummyItem()
        val observer = mock<Observer<ViewModelReturnType>>()
        model.getPosts().observeForever(observer)

        //when
        liveData.value = listOf(returnedItem)

        //than
        verify(observer).onChanged(
                ViewModelReturnType(
                        emptyList(),
                        listOf(ItemData.Post(Post(returnedItem.id, returnedItem.title, returnedItem.url)),
                                ItemData.Spinner)))
    }

    private fun createDummyItem() = Item(1, false, Type.story, "", 123, "123", false, null, null, emptyList(), null, 1, "test", null, null)
}