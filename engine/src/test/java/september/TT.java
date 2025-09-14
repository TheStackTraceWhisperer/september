package september;

import org.junit.jupiter.api.Test;
import org.lwjgl.BufferUtils;
import september.engine.EngineTestHarness;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.ARBTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY;
import static org.lwjgl.opengl.GL11C.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL11C.glGetFloatv;
import static org.lwjgl.opengl.GL11C.glTexParameterf;
import static org.lwjgl.opengl.ARBTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY;

public class TT extends EngineTestHarness {

  @Test
  void testAnisotropicFiltering() {
    System.out.println("Running OpenGL 4.6 Anisotropic Filtering Test...");

    // 1. Query the maximum supported anisotropy level.
    FloatBuffer maxAnisoBuffer = BufferUtils.createFloatBuffer(1);
    glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY, maxAnisoBuffer);
    float maxAniso = maxAnisoBuffer.get(0);
    System.out.println("Max anisotropy supported: " + maxAniso);

    // Check for any GL errors after querying.
    int error = glGetError();
    if (error != GL_NO_ERROR) {
      System.err.println("GL Error occurred during anisotropy query: " + error);
      throw new RuntimeException("OpenGL 4.6 feature query failed!");
    }

    // 2. Attempt to apply the maximum anisotropy setting to a dummy texture.
    int dummyTextureID = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, dummyTextureID);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY, maxAniso);
    glBindTexture(GL_TEXTURE_2D, 0);

    // Check for any GL errors after setting the parameter.
    error = glGetError();
    if (error != GL_NO_ERROR) {
      System.err.println("GL Error occurred during anisotropy setting: " + error);
      throw new RuntimeException("OpenGL 4.6 feature setting failed!");
    }

    System.out.println("OpenGL 4.6 Anisotropic Filtering Test successful!");
  }

}
