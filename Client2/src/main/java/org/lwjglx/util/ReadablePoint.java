package org.lwjglx.util;

/**
 * Readonly interface for Points
 * 
 * @author $Author$
 * @version $Revision$ $Id$
 */
public interface ReadablePoint {

    static ReadablePoint of(int x, int y) {
        return new ReadablePoint() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }

            @Override
            public void getLocation(WritablePoint dest) {
                dest.setLocation(x, y);
            }
        };
    }

    /**
     * @return int
     */
    int getX();

    /**
     * @return int
     */
    int getY();

    /**
     * Copy this ReadablePoint into a destination Point
     * 
     * @param dest The destination Point, or null, to create a new Point
     */
    void getLocation(WritablePoint dest);
}
