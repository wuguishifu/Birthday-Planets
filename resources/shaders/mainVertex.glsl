#version 460 core

// input values
layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 2) in vec3 normal;

// output values
out vec4 passColor;
out vec4 passNormal;
out vec3 passFragPos;

// the model, view, and projection matrices to render at
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    // set the position of this vertex
    gl_Position = projection * view * model * vec4(position, 1.0);

    // set the fragment position of this vertex in relation to the model and pass it to the fragment shader
    passFragPos = vec3(model * vec4(position, 1.0));

    // pass the normal vector, color, and light position for the specific vertex to the fragment shader
    passNormal = model * vec4(normalize(normal), 1.0); // normalize the vector normal to the vertex

    // pass the color
    passColor = color;
}