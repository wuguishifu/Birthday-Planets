#version 330 core

// input vectors
in vec4 passColor;
in vec4 passNormal;
in vec3 passFragPos;

// the lighting values for shading
uniform vec3 lightPos;
uniform float lightLevel;

// the light color
uniform vec3 lightColor;

// the position of the camera
uniform vec3 viewPos;

// if the objet is selected
//uniform float alpha;

// the out color
out vec4 outColor;


void main() {

    vec3 color = vec3(passColor.xyz);
    float alpha = passColor.w;

    vec3 normal = vec3(passNormal);

    // the color of the light
//    vec3 lightColor = vec3(1.0, 0.8, 1.0);

    // ambient lighting
    vec3 ambientLight = lightLevel * lightColor; // create the ambient light level

    // diffusion light
    vec3 lightDir = normalize(lightPos - passFragPos); // find the direction of the light
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuseLight = diff * lightColor;

    // specular light
    float specularStrength = 1;
    vec3 viewDir = normalize(viewPos - passFragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = specularStrength * spec * lightColor;

    // combine the ambient and diffusion light into the final fragment color
    vec3 colorResult = (ambientLight + diffuseLight + specular) * color; // combine the light components

//    vec3 colorResult = (ambientLight + diffuseLight) * color;

    outColor = vec4(colorResult, alpha);
//    outColor = vec4(passColor); // test renderer with just full color
}