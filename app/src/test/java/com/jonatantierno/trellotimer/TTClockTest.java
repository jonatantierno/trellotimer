package com.jonatantierno.trellotimer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by jonatan on 22/04/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TTClockTest {

    public static final int TEN_SECONDS = 10 * 1000;
    public static final int TIME_ZERO = 1000 * 1000;
    public static final int TIME_TWO = 1002 * 1000;
    public static final int TIME_FOUR = 1004 * 1000;
    public static final int TIME_SIX = 1006 * 1000;
    public static final int TIME_EIGHT = 1008 * 1000;
    public static final int TIME_ELEVEN = 1011 * 1000;

    TimerActivity activity;
    private TTClock clockUnderTest;
    private Ticker ticker;

    @Before
    public void setup(){
        activity = mock(TimerActivity.class);

        ticker = mock(Ticker.class);
        clockUnderTest = new TTClock(activity, ticker);

    }

    @Test
    public void whenTickThenDecreaseTimer(){
        clockUnderTest.start(TEN_SECONDS, TIME_ZERO);
        verify(ticker).tickInAsecond();

        clockUnderTest.tick(TIME_TWO);

        verify(activity).showTime("00:08");
        verify(ticker,times(2)).tickInAsecond();

        clockUnderTest.tick(TIME_FOUR);

        verify(activity).showTime("00:06");
        verify(ticker,times(3)).tickInAsecond();

        clockUnderTest.tick(TIME_ELEVEN);

        verify(activity).timeUp();
        verifyNoMoreInteractions(ticker);

    }

    @Test
    public void whilePausedTimeDoesNotPass(){
        clockUnderTest.start(TEN_SECONDS, TIME_ZERO);
        verify(ticker).tickInAsecond();
        assertFalse(clockUnderTest.isPaused());

        clockUnderTest.tick(TIME_TWO);
        verify(ticker,times(2)).tickInAsecond();
        verify(activity).showTime("00:08");

        clockUnderTest.pause(TIME_FOUR);
        assertTrue(clockUnderTest.isPaused());

        verify(activity).showTime("00:06");

        clockUnderTest.unpause(TIME_SIX);
        assertFalse(clockUnderTest.isPaused());
        verify(ticker,times(3)).tickInAsecond();

        clockUnderTest.tick(TIME_EIGHT);
        verify(ticker,times(4)).tickInAsecond();
        verify(activity).showTime("00:04");

    }

    @Test
    public void shouldYieldElapsedTimeSinceLastPause(){
        clockUnderTest.start(TEN_SECONDS, TIME_ZERO);

        clockUnderTest.tick(TIME_TWO);

        clockUnderTest.tick(TIME_FOUR);

        assertEquals(4 * 1000, clockUnderTest.getElapsedTime());

        clockUnderTest.pause(TIME_SIX);
        clockUnderTest.unpause(TIME_EIGHT);

        clockUnderTest.tick(TIME_ELEVEN);

        assertEquals(3 * 1000, clockUnderTest.getElapsedTime());

    }

    @Test
    public void beforeStartShouldBePaused(){
        assertTrue(clockUnderTest.isPaused());
    }


    @Test
    public void afterTimeUpShouldBePaused(){
        clockUnderTest.start(TEN_SECONDS, TIME_ZERO);
        clockUnderTest.tick(TIME_ELEVEN);

        assertTrue(clockUnderTest.isPaused());
    }
}