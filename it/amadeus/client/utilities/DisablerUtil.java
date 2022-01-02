package it.amadeus.client.utilities;

import java.util.Random;

public class DisablerUtil {

    public static float randomFloatValue() {
        return (float) getRandomInRange(2.96219E-7, 9.13303E-6);
    }

    public static double getRandomInRange(final double min, final double max) {
        final Random random = new Random();
        final double range = max - min;
        final double scaled = random.nextDouble() * range;
        final double shifted = scaled + min;
        return shifted;
    }

}
