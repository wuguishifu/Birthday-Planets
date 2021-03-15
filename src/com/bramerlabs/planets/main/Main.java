package com.bramerlabs.planets.main;

import com.bramerlabs.engine.graphics.Camera;
import com.bramerlabs.engine.graphics.Renderer;
import com.bramerlabs.engine.graphics.Shader;
import com.bramerlabs.engine.io.window.Input;
import com.bramerlabs.engine.io.window.Window;
import com.bramerlabs.engine.math.Vector3f;
import com.bramerlabs.engine.objects.shapes.Sphere;
import org.lwjgl.opengl.GL46;

public class Main implements Runnable {

    // main rendering variables
    private Window window; // the window
    private Shader shader; // the shader device
    private Renderer renderer; // used to render objects

    // input handling variables
    private Input input = new Input(); // used to handle inputs

    // camera variables
    public Camera camera = new Camera(new Vector3f(0, 0, 2), new Vector3f(0, 0, 0), input); // the camera
    public static final Vector3f LOOKING_AT = new Vector3f(0);
    private Vector3f lightPosition = new Vector3f(0, 100, 0);

    // objects to render
    private Sphere sphere;

    /**
     * main method
     * @param args - java vm arguments
     */
    public static void main(String[] args) {
        new Main().start();
    }

    /**
     * begins the thread
     */
    public void start() {
        Thread main = new Thread(this, "Planets");
        main.start();
    }

    /**
     * runs the application
     */
    @Override
    public void run() {
        init();
        while (!window.shouldClose()) {
            update();
            render();
        }
    }

    /**
     * initialize the program
     */
    public void init() {
        // create the OpenGL window
        window = new Window(input);
        window.create();

        // set the camera's arcball orbit focus
        camera.setLookingAt(LOOKING_AT);

        // create the renderer
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        renderer = new Renderer(window, shader);

        // initialize the shader
        shader.create();

        // initialize the objects
        sphere = Sphere.makeSphere(new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f), 0.5f);
    }

    /**
     * updates the application
     */
    private void update() {
        // update the window
        window.update();

        // update the objects

        // clear the screen
        GL46.glClearColor(Window.bgc.getX(), Window.bgc.getY(), Window.bgc.getZ(), 1);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        // handle inputs
        camera.updateArcball();
    }

    /**
     * renders the scene
     */
    private void render() {
        renderer.renderMesh(sphere, camera, lightPosition, false);

        // called at the end of rendering
        window.swapBuffers();
    }

}
