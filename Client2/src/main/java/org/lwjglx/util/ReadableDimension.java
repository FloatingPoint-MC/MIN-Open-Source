package org.lwjglx.util;

import java.awt.Dimension;

/**
 * Readonly interface for Dimensions
 * 
 * @author $Author$
 * @version $Revision$ $Id$
 */
public interface ReadableDimension {

    static ReadableDimension of(int width, int height) {
        return new ReadableDimension() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void getSize(WritableDimension dest) {
                dest.setSize(width, height);
            }
        };
    }

    static ReadableDimension of(Dimension minSize) {
        return of(minSize.width, minSize.height);
    }

    /**
     * Get the width
     * 
     * @return int
     */
    int getWidth();

    /**
     * Get the height
     * 
     * @return int
     */
    int getHeight();

    /**
     * Copy this ReadableDimension into a destination Dimension
     * 
     * @param dest The destination
     */
    void getSize(WritableDimension dest);
}
