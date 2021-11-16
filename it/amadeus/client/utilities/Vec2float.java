package it.amadeus.client.utilities;

public class Vec2float
{
    /** An immutable vector with {@code 0.0F} as the x and y components. */
    public static final Vec2float ZERO = new Vec2float(0.0F, 0.0F);

    /** An immutable vector with {@code 1.0F} as the x and y components. */
    public static final Vec2float ONE = new Vec2float(1.0F, 1.0F);

    /** An immutable vector with {@code 1.0F} as the x component. */
    public static final Vec2float UNIT_X = new Vec2float(1.0F, 0.0F);

    /** An immutable vector with {@code -1.0F} as the x component. */
    public static final Vec2float NEGATIVE_UNIT_X = new Vec2float(-1.0F, 0.0F);

    /** An immutable vector with {@code 1.0F} as the y component. */
    public static final Vec2float UNIT_Y = new Vec2float(0.0F, 1.0F);

    /** An immutable vector with {@code -1.0F} as the y component. */
    public static final Vec2float NEGATIVE_UNIT_Y = new Vec2float(0.0F, -1.0F);

    /**
     * An immutable vector with {@link Float#MAX_VALUE} as the x and y components.
     */
    public static final Vec2float MAX = new Vec2float(Float.MAX_VALUE, Float.MAX_VALUE);

    /**
     * An immutable vector with {@link Float#MIN_VALUE} as the x and y components.
     */
    public static final Vec2float MIN = new Vec2float(Float.MIN_VALUE, Float.MIN_VALUE);

    /** The x component of this vector. */
    public final float x;

    /** The y component of this vector. */
    public final float y;

    public Vec2float(float xIn, float yIn)
    {
        this.x = xIn;
        this.y = yIn;
    }
}