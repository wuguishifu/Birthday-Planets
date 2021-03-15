package com.bramerlabs.engine.io.text.font_rendering;


import com.bramerlabs.engine.math.Vector2f;
import com.bramerlabs.engine.math.Vector3f;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "resources/shaders/fontVertex.glsl";
    private static final String FRAGMENT_FILE = "resources/shaders/fontFragment.glsl";

    private int location_colour;
    private int location_translation;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_colour = super.getUniformLocation("colour");
        location_translation = super.getUniformLocation("translation");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    protected void loadColour(Vector3f colour){
        super.loadVector(location_colour, colour);
    }

    protected void loadTranslation(Vector2f translation){
        super.load2DVector(location_translation, translation);
    }


}