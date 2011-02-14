/**
 * Main vertex shader
 */

void calculateLighting(in int numLights, in vec3 N, in vec3 V, in float shininess,
                       inout vec4 ambient, inout vec4 diffuse, inout vec4 specular);

// Vertex->Fragment shader (varyings)
varying vec3 normal;
varying vec3 vertex;

// App->Shaders (uniforms)
//  uniform int texUsed;
uniform sampler2D TexUnit0;

void main()
{
    // Texture coords
//    if (texUsed >= 1)
        gl_TexCoord[0] = gl_MultiTexCoord0;
//    if (texUsed >= 2)
  //      gl_TexCoord[1] = gl_MultiTexCoord1;
//    if (texUsed >= 3)
  //      gl_TexCoord[2] = gl_MultiTexCoord2;
//    if (texUsed >= 4)
  //      gl_TexCoord[3] = gl_MultiTexCoord3;
//    if (texUsed >= 5)
  //     gl_TexCoord[4] = gl_MultiTexCoord4;
//    if (texUsed >= 6)
  //      gl_TexCoord[5] = gl_MultiTexCoord5;
//    if (texUsed >= 7)
  //      gl_TexCoord[6] = gl_MultiTexCoord6;
//    if (texUsed >= 8)
  //      gl_TexCoord[7] = gl_MultiTexCoord7;
//    if (texUsed >= 9)
  //      gl_TexCoord[8] = gl_MultiTexCoord8;
//    if (texUsed >= 10)
  //      gl_TexCoord[9] = gl_MultiTexCoord9;
//    if (texUsed >= 11)
  //      gl_TexCoord[10] = gl_MultiTexCoord10;
//    if (texUsed >= 12)
  //      gl_TexCoord[11] = gl_MultiTexCoord11;
//    if (texUsed >= 13)
  //      gl_TexCoord[12] = gl_MultiTexCoord12;
//    if (texUsed >= 14)
  //      gl_TexCoord[13] = gl_MultiTexCoord13;
//    if (texUsed >= 15)
  //      gl_TexCoord[14] = gl_MultiTexCoord14;
//    if (texUsed >= 16)
  //      gl_TexCoord[15] = gl_MultiTexCoord15;

    // Calculate the normal
    normal = normalize(gl_NormalMatrix * gl_Normal);

    // Transform the vertex position to eye space
    vertex = vec3(gl_ModelViewMatrix * gl_Vertex);

    gl_FrontColor = gl_Color;

    gl_Position = ftransform();
}