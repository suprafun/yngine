/**
 * Texturing for fragment shader.
 */

const int REPLACE  = 0;
const int MODULATE = 1;
const int DECAL    = 2;
const int BLEND    = 3;
const int ADD      = 4;
const int COMBINE  = 5;

void applyTexture2D(in sampler2D texUnit, in int type, in int index, inout vec4 color)
{
    // Read from the texture
    vec4 texture = texture2D(texUnit, gl_TexCoord[index].st);

    if (type == REPLACE)
    {
        color = texture;
    }
    else if (type == MODULATE)
    {
        color *= texture;
    }
    else if (type == DECAL)
    {
        vec3 temp = mix(color.rgb, texture.rgb, texture.a);

        color = vec4(temp, color.a);
    }
    else if (type == BLEND)
    {
        vec3 temp = mix(color.rgb, gl_TextureEnvColor[index].rgb, texture.rgb);

        color = vec4(temp, color.a * texture.a);
    }
    else if (type == ADD)
    {
        color.rgb += texture.rgb;
        color.a   *= texture.a;

        color = clamp(color, 0.0, 1.0);
    }
    else
    {
        color = clamp(texture * color, 0.0, 1.0);
    }
}
