#version 460 core

out vec4 FragColor;

void main()
{
    // Output a solid color. This shader has no inputs, so it is compatible
    // with any vertex shader that provides a gl_Position.
    FragColor = vec4(1.0, 0.0, 1.0, 1.0); // Magenta
}
