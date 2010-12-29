package sk.yin.yngine.render.shaders;

import javax.media.opengl.GL;

/**
 * This is an adaptor to uniform and attribute variables of shader programs.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class ShaderProgramVariables {
    private final String[] uniformNames, attributeNames;
    private final int[] uniformLocations, attributeLoctions;

    public ShaderProgramVariables(String[] uniformNames, String[] attributeNames,
            int[] uniformLocations, int[] attributeLoctions) {
        this.uniformNames = uniformNames;
        this.attributeNames = attributeNames;
        this.uniformLocations = uniformLocations;
        this.attributeLoctions = attributeLoctions;
    }

    public void setUniform1i(GL gl, String name, int value) {
        int location = gl.glGetUniformLocation(0, name);
        gl.glUniform1i(location, value);
    }
}
