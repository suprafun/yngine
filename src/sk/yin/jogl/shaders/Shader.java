package sk.yin.jogl.shaders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class Shader {
    private int program;

    private static final String[] VS = new String[] {
          "varying vec4 diffuse,ambient;"
	+ "varying vec3 normal,lightDir,halfVector;"
	+ ""
	+ "void main()"
	+ "{	"
	+ "	/* first transform the normal into eye space and "
	+ "	normalize the result */"
	+ "	normal = normalize(gl_NormalMatrix * gl_Normal);"
	+ "	"
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
	+ "	diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;"
	+ "	ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;"
	+ "	ambient += gl_LightModel.ambient * gl_FrontMaterial.ambient;"
	+ ""
	+ "	gl_Position = ftransform();"
	+ "}"
    };

    private static final String[] FS = new String[] {
        "varying vec4 diffuse,ambient;"
	+ "varying vec3 normal,lightDir,halfVector;"
	+ "	"
	+ "void main()"
	+ "{"
	+ "	vec3 n,halfV;"
	+ "	float NdotL,NdotHV;"
	+ "	"
	+ "	/* The ambient term will always be present */"
	+ "	vec4 color = ambient;"
	+ "	"
	+ "	/* a fragment shader can't write a varying variable, hence we need"
	+ "	a new variable to store the normalized interpolated normal */"
	+ "	n = normalize(normal);"
	+ "	"
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
	+ ""
	+ "	gl_FragColor = color;"
        + "}"

    };

    public Shader(GL gl) {
       int vs = gl.glCreateShaderObjectARB(GL.GL_VERTEX_SHADER_ARB),
               fs = gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);
       program = gl.glCreateProgramObjectARB();

       gl.glShaderSourceARB(vs, 1, VS, null);
       gl.glCompileShaderARB(vs);
       gl.glShaderSourceARB(fs, 1, FS, null);
       gl.glCompileShaderARB(fs);
       gl.glAttachObjectARB(program, vs);
       gl.glAttachObjectARB(program, fs);
       gl.glLinkProgramARB(program);
    }

    protected void printInfoLog(GL gl, int obj) {
        IntBuffer l = IntBuffer.allocate(1),
                n = IntBuffer.allocate(1);
        gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, l);
        if(l.get(0) > 0){
            ByteBuffer log = ByteBuffer.allocate(l.get(0));
            gl.glGetInfoLogARB(obj, l.get(0), n, null);
            System.out.println(log.toString());
        }

    }

    public void ad(GL gl, boolean active) {
        if(active) {
            gl.glUseProgramObjectARB(program);
            gl.glEnable(GL.GL_SHADER_OBJECT_ARB);
        } else {
            gl.glDisable(GL.GL_SHADER_OBJECT_ARB);
        }
    }
}
