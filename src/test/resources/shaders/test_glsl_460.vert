#version 460 core

layout (location = 0) in vec3 aPos;

void main()
{
    // Use gl_BaseVertex, a built-in variable introduced in GLSL 4.60.
    // This will fail to compile on any true 4.50 compiler.
    gl_Position = vec4(aPos, 1.0) + vec4(gl_BaseVertex, 0.0, 0.0, 0.0);
}
