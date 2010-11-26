package sk.yin.jogl.shaders;

import javax.media.opengl.GL;
import sk.yin.jogl.shaders.ShaderProgramBuilder.ShaderType;

/**
 *
 * @author yin
 */
public class ShaderFactory {
    private static ShaderFactory instance;

    private static final String DEFAULT_VERTEX_SHADER =
          "varying vec4 diffuse, ambient;"
        + "varying vec3 normal, lightDir, halfVector;"
        + "void main() {"
        + "	/* first transform the normal into eye space and "
        + "	normalize the result */"
        + "	normal = normalize(gl_NormalMatrix * gl_Normal);"
        + ""
        + "	/* now normalize the light's direction. Note that "
        + "	according to the OpenGL specification, the light "
        + "	is stored in eye space. Also since we're talking about "
        + "	a directional light, the position field is actually direction */"
        + "	lightDir = normalize(vec3(gl_LightSource[0].position));"
        + ""
        + "	/* Normalize the halfVector to pass it to the fragment shader */"
        + "	halfVector = normalize(gl_LightSource[0].halfVector.xyz);"
        + ""
        + "	/* Compute the diffuse, ambient and globalAmbient terms */"
        + "	diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse * gl_Color;"
        + "	ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;"
        + "	ambient += gl_LightModel.ambient * gl_FrontMaterial.ambient;"
        + "     ambient = ambient * gl_Color;"
        + "     gl_FrontColor = gl_Color;"
        + ""
        + "	gl_Position = ftransform();"
        + "}";

    private static final String DEFAULT_FRAGMENT_SHADER =
          "varying vec4 diffuse,ambient;"
        + "varying vec3 normal,lightDir,halfVector;"
        + "void main() {"
        + "	vec3 n,halfV;"
        + "	float NdotL,NdotHV;"
        + ""
        + "	/* The ambient term will always be present */"
        + "	vec4 color = ambient;"
        + ""
        + "	/* a fragment shader can't write a varying variable, hence we need"
        + "	a new variable to store the normalized interpolated normal */"
        + "	n = normalize(normal);"
        + ""
        + "	/* compute the dot product between normal and ldir */"
        + "	NdotL = max(dot(n,lightDir),0.0);"
        + "	if (NdotL > 0.0) {"
        + "		color += diffuse * NdotL;"
        + "		halfV = normalize(halfVector);"
        + "		NdotHV = max(dot(n,halfV),0.0);"
        + "		color += gl_FrontMaterial.specular * "
        + "				gl_LightSource[0].specular * "
        + "				pow(NdotHV, gl_FrontMaterial.shininess);"
        + "	}"
        + "	gl_FragColor = color;"
        + "}";

    private ShaderFactory() {
    }

    public static ShaderFactory getInstance() {
        if(instance == null)
            instance = new ShaderFactory();
        return instance;
    }

    public ShaderProgram createShaderProgram(GL gl) {
        ShaderProgramBuilder spb = new ShaderProgramBuilder();
        spb.addShaderSource(ShaderType.VERTEX, DEFAULT_VERTEX_SHADER);
        spb.addShaderSource(ShaderType.FRAGMENT, DEFAULT_FRAGMENT_SHADER);
        return spb.buildShaderProgram(gl);
    }
}
