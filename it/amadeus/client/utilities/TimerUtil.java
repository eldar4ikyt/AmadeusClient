package it.amadeus.client.utilities;

import java.util.concurrent.TimeUnit;

public class TimerUtil {

    public long lastMS = System.currentTimeMillis();

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public boolean delay(float milliSec) {
        return ((float) (getTime() - this.lastMS) >= milliSec);
    }

    public long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public boolean track(double milliseconds) {
        return ((getCurrentMS() - this.lastMS) >= milliseconds);
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean sleep(long time) {
        return sleep(time, TimeUnit.MILLISECONDS);
    }

    public boolean sleep(long time, TimeUnit timeUnit) {
        long convert = timeUnit.convert(System.nanoTime() - lastMS, TimeUnit.NANOSECONDS);
        if (convert >= time)
            reset();
        return convert >= time;
    }

    public long getDifference() {
        return getCurrentMS() - this.lastMS;
    }

    public boolean hasReached(long milliseconds) {
        return (getDifference() >= milliseconds);
    }

    public boolean hasPassed(double milli) {
        return ((getTime() - this.lastMS) >= milli);
    }
}