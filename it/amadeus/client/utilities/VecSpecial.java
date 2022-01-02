package it.amadeus.client.utilities;


import com.sun.istack.internal.Nullable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;


public class VecSpecial {
    public static final VecSpecial ZERO = new VecSpecial(0.0D, 0.0D, 0.0D);

    /**
     * X coordinate of VecSpecial
     */
    public final double x;

    /**
     * Y coordinate of VecSpecial
     */
    public final double y;

    /**
     * Z coordinate of VecSpecial
     */
    public final double z;

    public VecSpecial(double xIn, double yIn, double zIn) {
        if (xIn == -0.0D) {
            xIn = 0.0D;
        }

        if (yIn == -0.0D) {
            yIn = 0.0D;
        }

        if (zIn == -0.0D) {
            zIn = 0.0D;
        }

        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public VecSpecial(Vec3i vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * returns a VecSpecial from given pitch and yaw degrees as Vec2f
     */
    public static VecSpecial fromPitchYawVector(Vec2float p_189984_0_) {
        return fromPitchYaw(p_189984_0_.x, p_189984_0_.y);
    }

    /**
     * returns a VecSpecial from given pitch and yaw degrees
     */
    public static VecSpecial fromPitchYaw(float p_189986_0_, float p_189986_1_) {
        float f = MathHelper.cos(-p_189986_1_ * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-p_189986_1_ * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-p_189986_0_ * 0.017453292F);
        float f3 = MathHelper.sin(-p_189986_0_ * 0.017453292F);
        return new VecSpecial(f1 * f2, f3, f * f2);
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public VecSpecial subtractReverse(VecSpecial vec) {
        return new VecSpecial(vec.x - this.x, vec.y - this.y, vec.z - this.z);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public VecSpecial normalize() {
        double d0 = MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? ZERO : new VecSpecial(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dotProduct(VecSpecial vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public VecSpecial crossProduct(VecSpecial vec) {
        return new VecSpecial(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public VecSpecial subtract(VecSpecial vec) {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    public VecSpecial subtract(double x, double y, double z) {
        return this.addVector(-x, -y, -z);
    }

    public VecSpecial add(VecSpecial vec) {
        return this.addVector(vec.x, vec.y, vec.z);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public VecSpecial addVector(double x, double y, double z) {
        return new VecSpecial(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(VecSpecial vec) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(VecSpecial vec) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double squareDistanceTo(double xIn, double yIn, double zIn) {
        double d0 = xIn - this.x;
        double d1 = yIn - this.y;
        double d2 = zIn - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public VecSpecial scale(double p_186678_1_) {
        return new VecSpecial(this.x * p_186678_1_, this.y * p_186678_1_, this.z * p_186678_1_);
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector() {
        return MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Nullable

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public VecSpecial getIntermediateWithXValue(VecSpecial vec, double x) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;

        if (d0 * d0 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (x - this.x) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new VecSpecial(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    @Nullable

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public VecSpecial getIntermediateWithYValue(VecSpecial vec, double y) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;

        if (d1 * d1 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (y - this.y) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new VecSpecial(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    @Nullable

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public VecSpecial getIntermediateWithZValue(VecSpecial vec, double z) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;

        if (d2 * d2 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (z - this.z) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new VecSpecial(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof VecSpecial)) {
            return false;
        } else {
            VecSpecial VecSpecial = (VecSpecial) p_equals_1_;

            if (Double.compare(VecSpecial.x, this.x) != 0) {
                return false;
            } else if (Double.compare(VecSpecial.y, this.y) != 0) {
                return false;
            } else {
                return Double.compare(VecSpecial.z, this.z) == 0;
            }
        }
    }

    public int hashCode() {
        long j = Double.doubleToLongBits(this.x);
        int i = (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.y);
        i = 31 * i + (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.z);
        i = 31 * i + (int) (j ^ j >>> 32);
        return i;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public VecSpecial rotatePitch(float pitch) {
        float f = MathHelper.cos(pitch);
        float f1 = MathHelper.sin(pitch);
        double d0 = this.x;
        double d1 = this.y * (double) f + this.z * (double) f1;
        double d2 = this.z * (double) f - this.y * (double) f1;
        return new VecSpecial(d0, d1, d2);
    }

    public VecSpecial rotateYaw(float yaw) {
        float f = MathHelper.cos(yaw);
        float f1 = MathHelper.sin(yaw);
        double d0 = this.x * (double) f + this.z * (double) f1;
        double d1 = this.y;
        double d2 = this.z * (double) f - this.x * (double) f1;
        return new VecSpecial(d0, d1, d2);
    }
}
