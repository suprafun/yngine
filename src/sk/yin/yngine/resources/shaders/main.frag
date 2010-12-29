/**
 * Main fragment shader
 */

varying vec3 normal;
varying vec3 vertex;

void calculateLighting(in int numLights, in vec3 N, in vec3 V, in float shininess,
                       inout vec4 ambient, inout vec4 diffuse, inout vec4 specular);
void applyTexture2D(in sampler2D texUnit, in int type, in int index, inout vec4 color);

uniform sampler2D TexUnit0;
uniform int TexturingType;

void main()
{
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
//    calculateLighting(gl_MaxLights, n, vertex, gl_FrontMaterial.shininess,
//                      ambient, diffuse, specular);

//    vec4 color = gl_FrontLightModelProduct.sceneColor  +
//                 (ambient  * gl_FrontMaterial.ambient) +
//                 (diffuse  * gl_FrontMaterial.diffuse) +
//                 (specular * gl_FrontMaterial.specular);

//    color = clamp(color, 0.0, 1.0);

//    gl_FragColor = color;

    vec4 color = gl_Color;
    applyTexture2D(TexUnit0, TexturingType, 0, color);
    gl_FragColor = color;

}

