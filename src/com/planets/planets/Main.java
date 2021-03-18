package com.planets.planets;

import com.planets.engine.graphics.Camera;
import com.planets.engine.graphics.Renderer;
import com.planets.engine.graphics.Shader;
import com.planets.engine.graphics.Vertex;
import com.planets.engine.io.window.Input;
import com.planets.engine.io.window.Window;
import com.planets.engine.math.Vector3f;
import com.planets.engine.objects.shapes.Sphere;
import com.planets.planets.planet_object.Atmosphere;
import com.planets.planets.planet_object.Planet;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public class Main implements Runnable {

    // input handling variables
    private final Input input = new Input(); // the input device

    // main rendering objects
    private final Window window = new Window(input); // the window to render to
    private Shader shader; // the shader to use to render
    private Renderer renderer;// the object renderer
    private final Vector3f LIGHT_POSITION = new Vector3f(0, 0, 100f); // the position of the light source


    // camera variables
    private final Camera camera = new Camera( // the camera used for viewport
            new Vector3f(0, 0, 0), // position
            new Vector3f(0, 0, 0), // rotation
            input); // input

    // objects to be rendered
    private Planet planet; // test sphere
    private Atmosphere atmosphere1; // test atmosphere

    /**
     * main method
     * @param args - jvm arguments
     */
    public static void main(String[] args) {
        // initialize final variables


        new Main().start();
    }

    /**
     * starts the program
     */
    private void start() {
        Thread main = new Thread(this, "Planets"); // initialize new thread
        main.start(); // starts the program
    }

    /**
     * runs the program
     */
    public void run() {
        init();
        while (!window.shouldClose()) {
            update();
            render();
        }
        close();
    }

    /**
     * initializes the variables
     */
    private void init() {
        // initialize the window
        window.create();

        // initialize the shader and renderer
        shader = new Shader( // the shader to use to render
                "/shaders/mainVertex.glsl",
                "/shaders/mainFragment.glsl");
        renderer = new Renderer(window, shader);

        // set the camera's arcball orbit focus to the origin
        camera.setLookingAt(new Vector3f(0));

        // create render objects here
        planet = Planet.getInstance(new Vector3f(0));
        planet.createMesh();
        atmosphere1 = Atmosphere.getInstance(new Vector3f(0));
        atmosphere1.createMesh();

        // initialize the shader
        shader.create();
    }

    /**
     * updates the program
     */
    private void update() {
        // update the window
        window.update();

        // clear the screen
        float bgcR = Window.bgc.getX(), bgcG = Window.bgc.getY(), bgcB = Window.bgc.getZ();
        GL46.glClearColor(bgcR, bgcG, bgcB, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        // update the planet
        planet.rotate(0.1f, 0f, 0);
        atmosphere1.rotate(0, 0.1f, 0);

        // update the camera
        camera.updateArcball();
    }

    /**
     * renders the program
     */
    private void render() {
        // render the render objects
        GL11.glCullFace(GL11.GL_BACK);
        renderer.renderMesh(planet, camera, LIGHT_POSITION);

//        // render the back face first
//        GL11.glCullFace(GL11.GL_FRONT);
//        renderer.renderMesh(atmosphere1, camera, LIGHT_POSITION);
//        // then render the front face
//        GL11.glCullFace(GL11.GL_BACK);
//        renderer.renderMesh(atmosphere1, camera, LIGHT_POSITION);

        // swap buffers at the end
        window.swapBuffers();
    }

    /**
     * closes the program
     */
    private void close() {
        // release the window
        window.destroy();

        // release the render objects
        planet.destroy();

        // release the shaders
        shader.destroy();
    }
}