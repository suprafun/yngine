/**
 * Main fragment shader
 */


varying vec3 normal;
varying vec3 vertex;

void calculateLighting(in int numLights, in vec3 N, in vec3 V, in float shininess,
                       inout vec4 ambient, inout vec4 diffuse, inout vec4 specular);
void applyTexel(inout vec4 color, in vec4 texel, in int texFunc, in int index);

const int ENV_REPLACE  = 0;
const int ENV_MODULATE = 1;
const int ENV_DECAL    = 2;
const int ENV_BLEND    = 3;
const int ENV_ADD      = 4;
const int ENV_COMBINE  = 5;

uniform sampler2D TexUnit0;
uniform int texFunc0 = ENV_MODULATE;
uniform int texEnable0 = 0;

void main() {
    vec4 color = gl_Color;
    // Normalize the normal. A varying variable CANNOT
    // be modified by a fragment shader. So a new variable
    // needs to be created.
    vec3 n = normalize(normal);

    vec4 ambient  = vec4(0.0);
    vec4 diffuse  = vec4(0.0);
    vec4 specular = vec4(0.0);

    // In this case the built in uniform gl_MaxLights is used
    // to denote the number of lights. A better option may be passing
    // in the number of lights as a uniform or replacing the current
    // value with a smaller value.
    calculateLighting(gl_MaxLights, n, vertex, gl_FrontMaterial.shininess,
                      ambient, diffuse, specular);
    if(diffuse.r < 0.0) {
        diffuse.r = -diffuse.r;
    }

    vec4 light = gl_FrontLightModelProduct.sceneColor  +
                 (ambient  * gl_FrontMaterial.ambient) +
                 (diffuse  * gl_FrontMaterial.diffuse);
    vec4 spec = (specular * gl_FrontMaterial.specular);

    if (texEnable0 != 0) {
        vec4 texel = texture2D(TexUnit0, gl_TexCoord[0].st);
        applyTexel(color, texel, texFunc0, 0);
    }
    color = color * light + spec;

    gl_FragColor = clamp(color, 0.0, 1.0);
}

