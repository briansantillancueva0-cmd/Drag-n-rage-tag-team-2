package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.NavTab
import com.example.viewmodel.DbzTagTeamViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DBZ Tag Team", appName)
    }

    @Test
    fun `verify initial view model state`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DbzTagTeamViewModel(app)

        assertEquals(NavTab.HOME, viewModel.selectedTab.value)
        assertTrue(viewModel.isGameDataVerified.value)
        assertTrue(viewModel.texturePacks.value.isNotEmpty())
        assertTrue(viewModel.modsList.value.isNotEmpty())
        assertTrue(viewModel.cheatsList.value.isNotEmpty())

        viewModel.setUnlockEverything(true)
        assertTrue(viewModel.unlockEverything.value)
    }

    @Test
    fun `verify Room savestates dao operations`() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = com.example.data.AppDatabase.getDatabase(app)
        val dao = db.saveStateDao()

        val state = com.example.data.model.SaveStateEntity(
            slotNumber = 3,
            romFileName = "test_game.iso",
            gameTitle = "Dragon Ball Z: Tenkaichi Tag Team",
            title = "Test Battle Save",
            statePath = "/storage/test.ppst"
        )

        val id = dao.insertSaveState(state)
        assertTrue(id > 0)

        val retrieved = dao.getSaveStateById(id)
        org.junit.Assert.assertNotNull(retrieved)
        assertEquals("test_game.iso", retrieved?.romFileName)
        assertEquals(3, retrieved?.slotNumber)

        dao.deleteSaveStateById(id)
        val deleted = dao.getSaveStateById(id)
        org.junit.Assert.assertNull(deleted)
    }
}
