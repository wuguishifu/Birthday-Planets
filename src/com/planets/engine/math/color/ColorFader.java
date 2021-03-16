package com.planets.engine.math.color;

import com.planets.engine.math.Vector3f;

import java.awt.*;

public class ColorFader {

    /**
     * the 3 color fade values
     */
    private Vector3f c1, c2, c3;

    // slopes
    private float mr1, mr2, mg1, mg2, mb1, mb2;

    // offsets
    private float br1, br2, bg1, bg2, bb1, bb2;

    private static final float x1 = 0.0f, x2 = 0.5f, x3 = 1.0f;

    public ColorFader(Color c1, Color c2, Color c3) {
        this.c1 = new Vector3f(c1);
        this.c2 = new Vector3f(c2);
        this.c3 = new Vector3f(c3);
    }

    /**
     * default constructor
     * @param c1 - color 1
     * @param c2 - color 2
     * @param c3 - color 3
     */
    public ColorFader(Vector3f c1, Vector3f c2, Vector3f c3) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;

        // calculate the slopes
        this.mr1 = (c2.getX() - c1.getX())/(x2-x1);
        this.mr2 = (c3.getX() - c2.getX())/(x3-x2);
        this.mg1 = (c2.getY() - c1.getY())/(x2-x1);
        this.mg2 = (c3.getY() - c2.getY())/(x3-x2);
        this.mb1 = (c2.getZ() - c1.getZ())/(x2-x1);
        this.mb2 = (c3.getZ() - c2.getZ())/(x3-x2);

        // calculate the offsets
        br1 = c1.getX();
        br2 = c2.getX();
        bg1 = c1.getY();
        bg2 = c2.getY();
        bb1 = c1.getZ();
        bb2 = c2.getZ();
    }

    public Vector3f getColor(float x) {

        float r, g, b;

        // calculate red component
        if (x < x2) {
            r = mr1 * x + br1;
        } else {
            r = mr2 * x + br2;
        }

        // calculate green component
        if (x < x2) {
            g = mg1 * x + bg1;
        } else {
            g = mg2 * x + bg2;
        }

        // calculate blue component
        if (x < x2) {
            b = mb1 * x + bb1;
        } else {
            b = mb2 * x + bb2;
        }

        return new Vector3f(r, g, b);
    }

}
