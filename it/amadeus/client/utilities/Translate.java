package it.amadeus.client.utilities;

public class Translate {
    private float x;

    private float y;

    private long lastMS;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
        this.lastMS = System.currentTimeMillis();
    }

    public void interpolate(float targetX, float targetY, int xSpeed, int ySpeed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        int deltaX = (int)(Math.abs(targetX - this.x) * 0.51F);
        int deltaY = (int)(Math.abs(targetY - this.y) * 0.51F);
        this.x = calculateCompensation(targetX, this.x, delta, deltaX);
        this.y = calculateCompensation(targetY, this.y, delta, deltaY);
    }

    public void interpolate(float targetX, float targetY, double speed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        double deltaX = 0.0D;
        double deltaY = 0.0D;
        if (speed != 0.0D) {
            deltaX = (Math.abs(targetX - this.x) * 0.35F) / 10.0D / speed;
            deltaY = (Math.abs(targetY - this.y) * 0.35F) / 10.0D / speed;
        }
        this.x = calculateCompensation(targetX, this.x, delta, deltaX);
        this.y = calculateCompensation(targetY, this.y, delta, deltaY);
    }

    public void interpolate(float target, float targetY, double doubleYSpeed, double speed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        if (this.x < target)
            this.x = (float)(this.x + speed * delta);
        double deltaY = 0.0D;
        if (speed != 0.0D)
            deltaY = (Math.abs(targetY - this.y) * 0.35F) / 10.0D / doubleYSpeed;
        this.y = calculateCompensation(targetY, this.y, delta, deltaY);
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public static float calculateCompensation(float target, float current, long delta, double speed) {
        float diff = current - target;
        if (delta < 1L)
            delta = 1L;
        if (delta > 1000L)
            delta = 16L;
        if (diff > speed) {
            double xD = Math.max(speed * delta / 16.66666603088379D, 0.5D);
            current = (float)(current - xD);
            if (current < target)
                current = target;
        } else if (diff < -speed) {
            double xD = Math.max(speed * delta / 16.66666603088379D, 0.5D);
            current = (float)(current + xD);
            if (current > target)
                current = target;
        } else {
            current = target;
        }
        return current;
    }
}
