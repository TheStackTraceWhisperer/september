#version 460 core

// Use the #include directive, a GLSL 4.60 feature.
#include "shared.glsl"

layout (location = 0) in vec3 aPos;
out vec4 vertexColor;

void main()
{
    gl_Position = vec4(aPos, 1.0);
    // Use the function from the included file.
    vertexColor = vec4(getSharedColor(), 1.0);
}
