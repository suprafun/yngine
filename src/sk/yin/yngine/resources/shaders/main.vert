/**
 * Main vertex shader
 */

void calculateLighting(in int numLights, in vec3 N, in vec3 V, in float shininess,
                       inout vec4 ambient, inout vec4 diffuse, inout vec4 specular);

varying vec3 normal;
varying vec3 vertex;

void main()
{
    // Texture coords
    gl_TexCoord[0] = gl_MultiTexCoord0;

    // Calculate the normal
    normal = normalize(gl_NormalMatrix * gl_Normal);

    // Transform the vertex position to eye space
    vertex = vec3(gl_ModelViewMatrix * gl_Vertex);

    gl_FrontColor = gl_Color;

    gl_Position = ftransform();
}