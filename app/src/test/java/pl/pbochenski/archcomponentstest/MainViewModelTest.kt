package pl.pbochenski.archcomponentstest

import android.arch.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import pl.pbochenski.archcomponentstest.hackernews.Item
import pl.pbochenski.archcomponentstest.hackernews.Type

/**
 * Created by Pawel Bochenski on 29.06.2017.
 */
class MainViewModelTest {

    val repo = mock<PostRepo>()
    lateinit var model: MainViewModel

    @Before
    fun before() {
        val app = mock<App> {
            on { postRepo } doReturn (repo)
        }
        model = MainViewModel(app)
    }

    @Test
    fun shouldLoadRepoOnRefresh() {
        model.refresh()
        verify(repo).load(15)
    }

    @Test
    fun shouldGetPostAndHaveACopyOfIt() {
        val toReturn = MutableLiveData<List<Item>>().apply {
            value = listOf(Item(1, false, Type.story, "", 123, "123", false, null, null, emptyList(), null, 1, "test", null, null))
        }
        repo.stub {
            on { getPosts() } doReturn toReturn
        }
        val value = model.getPosts().value

        Assert.assertEquals(toReturn, value)
    }
}