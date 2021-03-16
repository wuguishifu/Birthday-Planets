package com.planets.engine.io.picking;

import com.planets.engine.graphics.Camera;
import com.planets.engine.io.window.Input;
import com.planets.engine.io.window.Window;
import com.planets.engine.math.Matrix4f;

public class Picker {

    // the camera of the scene
    private Camera camera;

    // the window
    private Window window;

    // the input device
    private Input input;

    // the projection and view matrices
    private Matrix4f projection, view;

    /**
     * default constructor
     * @param camera - the camera in the window
     * @param window - the display window
     * @param input - the object for handling input
     */
    public Picker(Camera camera, Window window, Input input) {
        this.camera = camera;
        this.window = window;
        this.projection = window.getProjectionMatrix();
        this.input = input;
    }
}