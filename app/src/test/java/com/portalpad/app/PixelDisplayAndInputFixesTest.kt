package com.portalpad.app

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.portalpad.app.data.MirrorKeepAwakeMode
import com.portalpad.app.data.PreferencesRepository
import com.portalpad.app.service.InputInjector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class PixelDisplayAndInputFixesTest {

    @Test
    fun mirrorKeepAwakeMode_roundTripsAllIdsAndDefaultsToOff() {
        for (mode in MirrorKeepAwakeMode.entries) {
            assertEquals(mode, MirrorKeepAwakeMode.fromId(mode.id))
        }
        assertEquals(MirrorKeepAwakeMode.OFF, MirrorKeepAwakeMode.fromId(null))
        assertEquals(MirrorKeepAwakeMode.OFF, MirrorKeepAwakeMode.fromId(""))
        assertEquals(MirrorKeepAwakeMode.OFF, MirrorKeepAwakeMode.fromId("unknown_mode"))
    }

    @Test
    fun preferencesRepository_persistsKeepAwakeModeAndForceSeparateDisplay() = runBlocking {
        val context: Context = ApplicationProvider.getApplicationContext()
        val prefs = PreferencesRepository(context)

        assertEquals(MirrorKeepAwakeMode.OFF, prefs.mirrorKeepAwakeMode.first())
        assertFalse(prefs.forceSeparateDisplay.first())

        prefs.setMirrorKeepAwakeMode(MirrorKeepAwakeMode.OLED_BLACK_OVERLAY)
        prefs.setForceSeparateDisplay(true)

        assertEquals(MirrorKeepAwakeMode.OLED_BLACK_OVERLAY, prefs.mirrorKeepAwakeMode.first())
        assertTrue(prefs.forceSeparateDisplay.first())

        prefs.setMirrorKeepAwakeMode(MirrorKeepAwakeMode.DIM_SCREEN)
        assertEquals(MirrorKeepAwakeMode.DIM_SCREEN, prefs.mirrorKeepAwakeMode.first())

        prefs.setMirrorKeepAwakeMode(MirrorKeepAwakeMode.KEEP_SCREEN_ON)
        assertEquals(MirrorKeepAwakeMode.KEEP_SCREEN_ON, prefs.mirrorKeepAwakeMode.first())

        prefs.setForceSeparateDisplay(false)
        prefs.setMirrorKeepAwakeMode(MirrorKeepAwakeMode.OFF)
        assertFalse(prefs.forceSeparateDisplay.first())
        assertEquals(MirrorKeepAwakeMode.OFF, prefs.mirrorKeepAwakeMode.first())
    }

    @Test
    fun scaleCoordsForMirror_scalesAndClampsAcrossMismatchedDisplays() {
        val (midX, midY) = InputInjector.scaleCoordsForMirror(
            x = 960f,
            y = 540f,
            srcW = 1920,
            srcH = 1080,
            dstW = 1080,
            dstH = 2400,
        )
        assertEquals(540f, midX, 0.01f)
        assertEquals(1200f, midY, 0.01f)

        val (clampedMinX, clampedMinY) = InputInjector.scaleCoordsForMirror(
            x = -50f,
            y = -20f,
            srcW = 1920,
            srcH = 1080,
            dstW = 1080,
            dstH = 2400,
        )
        assertEquals(0f, clampedMinX, 0.01f)
        assertEquals(0f, clampedMinY, 0.01f)

        val (clampedMaxX, clampedMaxY) = InputInjector.scaleCoordsForMirror(
            x = 3000f,
            y = 2000f,
            srcW = 1920,
            srcH = 1080,
            dstW = 1080,
            dstH = 2400,
        )
        assertEquals(1079f, clampedMaxX, 0.01f)
        assertEquals(2399f, clampedMaxY, 0.01f)

        val (fallbackX, fallbackY) = InputInjector.scaleCoordsForMirror(
            x = 412f,
            y = 289f,
            srcW = 0,
            srcH = 1080,
            dstW = 1080,
            dstH = 2400,
        )
        assertEquals(412f, fallbackX, 0.01f)
        assertEquals(289f, fallbackY, 0.01f)
    }
}
