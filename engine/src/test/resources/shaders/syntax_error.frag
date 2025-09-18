#version 330 core
out vec4 FragColor;

// This shader has a syntax error - missing semicolon
void main()
{
    FragColor = vec4(1.0, 0.0, 0.0, 1.0)  // Missing semicolon here
}