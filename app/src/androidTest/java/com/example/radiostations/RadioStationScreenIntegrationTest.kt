package com.example.radiostations

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalComposeUiApi
@RunWith(AndroidJUnit4::class)
class RadioStationScreenIntegrationTest {


    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testRadioStationScreenLoadingAndStationsDisplay() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        rule.waitForIdle()
        rule.onNodeWithContentDescription("Loading Progress").isDisplayed()
        rule.waitUntil(5000) {
            rule.onNodeWithContentDescription("Radio Stations List").isDisplayed()
        }
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(0)
            .assertTextContains("Station: _ 101.1 ALGERİA FM")
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(1)
            .assertTextContains("Station: _ 101.1 RADİO TURKEY FM")
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(2)
            .assertTextContains("Station: _ 101.1 SJK POP FM")
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(3)
            .assertTextContains("Station: _ 101.5 AEK POP FM")
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(4)
            .assertTextContains("Station: _ BARBADOS FM")
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(5)
            .assertTextContains("Station: _01 ARABESKİN MERKEZİ FM")
        activityScenario.close()
    }

    @Test
    fun testRadioStationScreenDisplayAndPerformClick() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        rule.waitForIdle()
        rule.onNodeWithContentDescription("Loading Progress").isDisplayed()
        rule.waitUntil(5000) {
            rule.onNodeWithContentDescription("Radio Stations List").isDisplayed()
        }
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(0)
            .assertTextContains("Station: _ 101.1 ALGERİA FM")
        rule.onNodeWithContentDescription("Radio Stations List").onChildAt(0)
            .performSemanticsAction(SemanticsActions.OnClick)

        activityScenario.close()
    }
}