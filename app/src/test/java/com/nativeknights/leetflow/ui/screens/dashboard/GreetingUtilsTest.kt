package com.nativeknights.leetflow.ui.screens.dashboard

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for getGreeting(hour).
 *
 * Each @Test method = one scenario.
 * No Android emulator needed — runs on your laptop JVM directly.
 */
class GreetingUtilsTest {

    // ── Morning: 0 to 11 ─────────────────────────────────────────────────────

    @Test
    fun `midnight returns Good Morning`() {
        assertEquals("Good Morning", getGreeting(hour = 0))
    }

    @Test
    fun `9am returns Good Morning`() {
        assertEquals("Good Morning", getGreeting(hour = 9))
    }

    @Test
    fun `11am is still Good Morning`() {
        assertEquals("Good Morning", getGreeting(hour = 11))
    }

    // ── Afternoon: 12 to 16 ──────────────────────────────────────────────────

    @Test
    fun `noon returns Good Afternoon`() {
        assertEquals("Good Afternoon", getGreeting(hour = 12))
    }

    @Test
    fun `3pm returns Good Afternoon`() {
        assertEquals("Good Afternoon", getGreeting(hour = 15))
    }

    @Test
    fun `4pm is still Good Afternoon`() {
        assertEquals("Good Afternoon", getGreeting(hour = 16))
    }

    // ── Evening: 17 to 20 ────────────────────────────────────────────────────

    @Test
    fun `5pm returns Good Evening`() {
        assertEquals("Good Evening", getGreeting(hour = 17))
    }

    @Test
    fun `8pm returns Good Evening`() {
        assertEquals("Good Evening", getGreeting(hour = 20))
    }

    // ── Night: 21 to 23 ──────────────────────────────────────────────────────

    @Test
    fun `9pm returns Good Night`() {
        assertEquals("Good Night", getGreeting(hour = 21))
    }

    @Test
    fun `11pm returns Good Night`() {
        assertEquals("Good Night", getGreeting(hour = 23))
    }

    // ── Boundary checks — the edges most likely to have bugs ─────────────────

    @Test
    fun `hour 11 is Morning not Afternoon`() {
        assertEquals("Good Morning", getGreeting(hour = 11))
    }

    @Test
    fun `hour 12 is Afternoon not Morning`() {
        assertEquals("Good Afternoon", getGreeting(hour = 12))
    }

    @Test
    fun `hour 16 is Afternoon not Evening`() {
        assertEquals("Good Afternoon", getGreeting(hour = 16))
    }

    @Test
    fun `hour 17 is Evening not Afternoon`() {
        assertEquals("Good Evening", getGreeting(hour = 17))
    }

    @Test
    fun `hour 20 is Evening not Night`() {
        assertEquals("Good Evening", getGreeting(hour = 20))
    }

    @Test
    fun `hour 21 is Night not Evening`() {
        assertEquals("Good Night", getGreeting(hour = 21))
    }
}
