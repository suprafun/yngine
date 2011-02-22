/**
 * Texturing for fragment shader.
 */
const int ENV_REPLACE  = 0;
const int ENV_MODULATE = 1;
const int ENV_DECAL    = 2;
const int ENV_BLEND    = 3;
const int ENV_ADD      = 4;
const int ENV_COMBINE  = 5;

// App => Shader
uniform bool clampTextured = false;

/**
 * Applies texel on fragment color using a texture function, coresponding to
 * GL texture environments.
 */
void applyTexel(inout vec4 color, in vec4 texel, in int texFunc, in int index) {
    vec4 c = color;
    if (texFunc == ENV_REPLACE) {
        color = texel;

    } else if (texFunc == ENV_MODULATE) {
        color *= texel;

    } else if (texFunc == ENV_DECAL) {
        vec3 temp = mix(color.rgb, texel.rgb, texel.a);
        color = vec4(temp, color.a);

    } else if (texFunc == ENV_BLEND) {
        vec3 env = gl_TextureEnvColor[index].rgb;
        vec3 temp = mix(color.rgb, env, texel.rgb);
        color = vec4(temp, color.a * texel.a);

    } else if (texFunc == ENV_ADD) {
        color.rgb += texel.rgb;
        color.a   *= texel.a;
        if(clampTextured)
            color = clamp(color, 0.0, 1.0);

    } else if (texFunc == ENV_COMBINE) {
        color = texel * color;
        if(clampTextured)
            color = clamp(color, 0.0, 1.0);
    }
}
