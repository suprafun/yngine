#! /usr/bin/python

from sk.yin.yngine.render.config import AnisotropicConfig
from sk.yin.yngine.render.shaders import ShaderFactory
from sk.yin.yngine.scene.io import TextureLoader

__author__ = "Matej 'Yin' Gagyi"
__date__ = "$6.4.2011 22:11:51$"

def setupTextures(gl):
    """Loads a texture and sets up texturing."""
    AnisotropicConfig.instance().setMaxAnisotropy(gl)

    textures = TextureLoader.getInstance().loadResource([
                                                        "tex07.png",
                                                        "tex06.2.png",
                                                        "tex05-c.png",
                                                        "tex05.png",
                                                        "tex04.2.png",
                                                        "tex04.png",
                                                        "tex03.png",
                                                        "tex2.png",
                                                        "tex1.png"])
    return textures

def setupShaders(gl):
    """Loads shaders and configures them."""
    if (DISABLE_SHADERS):
        ShaderProgram.disableShaders(gl)
    shader = ShaderFactory.getInstance().loadShader(gl, "ffpe", True)
    print "Python) Shader to use in scene: " + shader.toString()
    return shader


if __name__ == "__main__":
    """Main initialization"""
    #try:
    textures = setupTextures(gl)
    
    shader = setupShaders(gl)
    #except Exception, e:
        
