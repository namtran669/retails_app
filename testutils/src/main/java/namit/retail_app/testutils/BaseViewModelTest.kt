package namit.retail_app.testutils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
abstract class BaseViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    open fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    open fun reset() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}