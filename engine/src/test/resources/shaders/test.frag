#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D texture_sampler;

void main()
{
    FragColor = texture(texture_sampler, TexCoords);
}
