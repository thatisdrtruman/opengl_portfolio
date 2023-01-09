import Basic.ShaderProg;
import Basic.Transform;
import Basic.Vec4;
import Objects.SCube;
import Objects.SObject;
import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jogamp.opengl.GL3.*;

public class CGCW03 {

	final GLWindow window; //Define a window
	final FPSAnimator animator=new FPSAnimator(60, true);
	final Renderer renderer = new Renderer();

	public CGCW03() {
		GLProfile glp = GLProfile.get(GLProfile.GL3);
		GLCapabilities caps = new GLCapabilities(glp);
		window = GLWindow.create(caps);

		window.addGLEventListener(renderer); //Set the canvas to listen GLEvents

		animator.add(window);

		window.setTitle("Coursework 3");
		window.setSize(500,500);
		window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
		window.setVisible(true);

		animator.start();
	}

	public static void main(String[] args) {
		new CGCW03();

	}

	class Renderer implements GLEventListener {
		private Texture texture;
		private Transform T = new Transform(); //model_view transform

		//VAOs and VBOs parameters
		private int idPoint=0, numVAOs = 2;
		private int idBuffer=0, numVBOs = 2;
		private int idElement=0, numEBOs = 2;
		private int[] VAOs = new int[numVAOs];
		private int[] VBOs = new int[numVBOs];
		private int[] EBOs = new int[numEBOs];

		//Model parameters
		private int[] numElements = new int[numEBOs];
		private long vertexSize;
		private long normalSize;
		private long texSize;

		private int vPosition;
		private int vNormal;
		private int vTexCoord;

		//Transformation parameters
		private int ModelView;
		private int NormalTransform;
		private int Projection;

		//texture parameters
		ByteBuffer texImg;
		private int texWidth, texHeight;
		private int texName[] = new int[1];
		//Lighting parameter
		private int AmbientProduct;
		private int DiffuseProduct;
		private int SpecularProduct;
		private int Shininess;

		private float[] ambient1;
		private float[] diffuse1;
		private float[] specular1;
		private float  materialShininess1;

		//Set the rotation angle along the x axis to -90 degrees, meaning 90 degrees clockwise
		private float rotateXangle = -85;
		private float rotateYangle = -120;
		private float rotateZangle = -65;

		@Override
		public void display(GLAutoDrawable drawable) {
			GL3  gl = drawable.getGL().getGL3(); // Get the GL pipeline object this
			gl.glEnable(GL_DEPTH_TEST);
			gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

			//Transformation for the  object (a cube)
			T.initialize();
			T.scale(0.7f, 0.7f, 0.7f);
			T.translate(-0.5f, -0.5f, 0.0f);
			T.rotateX(rotateXangle);
			T.rotateY(rotateYangle);
			T.rotateZ(rotateZangle);
			//Add code here to transform the first object (the sphere) into the position
			//matrix to column major one, which is required when vertices'
			//location vectors are pre-multiplied by the model_view matrix.
			//Note that the normal transformation matrix is the inverse-transpose
			//matrix of the vertex transformation matrix
			gl.glUniformMatrix4fv( ModelView, 1, true, T.getTransformv(), 0 );
			gl.glUniformMatrix4fv( NormalTransform, 1, true, T.getInvTransformTv(), 0 );

			//send other uniform variables to shader
			gl.glUniform4fv( AmbientProduct, 1, ambient1,0 );
			gl.glUniform4fv( DiffuseProduct, 1, diffuse1, 0 );
			gl.glUniform4fv( SpecularProduct, 1, specular1, 0 );
			gl.glUniform1f( Shininess, materialShininess1);

			idPoint=0;
			idBuffer=0;
			idElement=0;
			bindObject(gl);
			gl.glDrawElements(GL_TRIANGLES, numElements[idElement], GL_UNSIGNED_INT, 0);
//			rotateXangle += 1f;
//			rotateYangle += 3f;
//			rotateZangle += 1.5f;
		}


		@Override
		public void init(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this

			System.out.print("GL_Version: " + gl.glGetString(GL_VERSION));


			//Step 0: read in texture image
			//Please note the texture image "WelshDragon.jpg" should be in the current directory
			try {
				texture = TextureIO.newTexture(new File("WelshDragon.jpg"), false);
			} catch (IOException ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			}

			//Step 2: Indicate how the texture is to be applied to each pixel
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);  //must be specified

			//compile and use the shader program
			ShaderProg shaderproc = new ShaderProg(gl, "Texture.vert", "Texture.frag");
			int program = shaderproc.getProgram();
			gl.glUseProgram(program);

			// Initialize the vertex position and normal attribute in the vertex shader
			vPosition = gl.glGetAttribLocation( program, "vPosition" );
			vNormal = gl.glGetAttribLocation( program, "vNormal" );
			vTexCoord = gl.glGetAttribLocation( program, "vTexCoord" );

			// Get connected with the ModelView, NormalTransform, and Projection matrices
			// in the vertex shader
			ModelView = gl.glGetUniformLocation(program, "ModelView");
			NormalTransform = gl.glGetUniformLocation(program, "NormalTransform");
			Projection = gl.glGetUniformLocation(program, "Projection");

			// Set the value of the fragment shader texture sampler variable
			//   ("texture") to the the appropriate texture unit. In this case,
			//   zero, for GL_TEXTURE0 which was previously set by calling
			//   glActiveTexture().
			gl.glUniform1i( gl.glGetUniformLocation(program, "tex"), 0 );

			// Get connected with uniform variables AmbientProduct, DiffuseProduct,
			// SpecularProduct, and Shininess in the vertex shader
			AmbientProduct = gl.glGetUniformLocation(program, "AmbientProduct");
			DiffuseProduct = gl.glGetUniformLocation(program, "DiffuseProduct");
			SpecularProduct = gl.glGetUniformLocation(program, "SpecularProduct");
			Shininess = gl.glGetUniformLocation(program, "Shininess");

			// Prevents colour on back face from displaying
			gl.glDepthFunc(GL_LEQUAL);
			gl.glEnable(GL_DEPTH_TEST);

			// Generate VAOs, VBOs, and EBOs
			gl.glGenVertexArrays(numVAOs,VAOs,0);
			gl.glGenBuffers(numVBOs, VBOs,0);
			gl.glGenBuffers(numEBOs, EBOs,0);

			// Initialize shader lighting parameters
			float[] lightPosition = {5f, 8f, -4.0f, -3.0f};
			Vec4 lightAmbient = new Vec4(5.0f, 5.0f, 5.0f, 1.0f);
			Vec4 lightDiffuse = new Vec4(-20.0f, -20.0f, -20.0f, -2.0f);
			Vec4 lightSpecular = new Vec4(3.0f, 4.0f, -4.0f, 5.0f);

			gl.glUniform4fv( gl.glGetUniformLocation(program, "LightPosition"),
					1, lightPosition, 0 );

			//create the first object: a cube
			SObject cube = new SCube(1,2,2);
			idPoint=0;
			idBuffer=0;
			idElement=0;
			createObject(gl, cube);

			// Set Cube material
			Vec4 materialAmbient1 = new Vec4(0.25f, 0.2f, 0.2f, 0.922f);
			Vec4 materialDiffuse1 = new Vec4(1.0f, 0.83f, 0.83f, 0.922f);
			Vec4 materialSpecular1 = new Vec4(0.3f, 0.3f, 0.296f, 0.922f);
			materialShininess1 = 15.25f;

			Vec4 ambientProduct = lightAmbient.times(materialAmbient1);
			ambient1 = ambientProduct.getVector();
			Vec4 diffuseProduct = lightDiffuse.times(materialDiffuse1);
			diffuse1 = diffuseProduct.getVector();
			Vec4 specularProduct = lightSpecular.times(materialSpecular1);
			specular1 = specularProduct.getVector();

			// This is necessary. Otherwise,  The color on back face may display
		    gl.glDepthFunc(GL_LESS);
			gl.glEnable(GL_DEPTH_TEST);
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int w,
							int h) {

			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this

			gl.glViewport(x, y, w, h);

			T.initialize();

			//projection
			if(h<1){h=1;}
			if(w<1){w=1;}
			float a = (float) w/ h;   //aspect
			if (w < h) {
				T.ortho(-1, 1, -1/a, 1/a, -1, 1);
			}
			else{
				T.ortho(-1*a, 1*a, -1, 1, -1, 1);
			}

			// Convert right-hand to left-hand coordinate system
			T.reverseZ();
			gl.glUniformMatrix4fv( Projection, 1, true, T.getTransformv(), 0 );

		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			System.exit(0);

		}

		public void createObject(GL3 gl, SObject obj) {

			float [] vertexArray = obj.getVertices();
			float [] normalArray = obj.getNormals();
			int [] vertexIndexs =obj.getIndices();
			//added texture array
			float [] textureArray = obj.getTextures();
			numElements[idElement] = obj.getNumIndices();
			//bind the object before introducing the FloatBuffers
			bindObject(gl);

			FloatBuffer vertices = FloatBuffer.wrap(vertexArray);
			FloatBuffer normals = FloatBuffer.wrap(normalArray);
			FloatBuffer textures = FloatBuffer.wrap(textureArray);
// And after introducting the floatBuffers
			bindObject(gl);
			// Create an empty buffer with the size we need
			// and a null pointer for the data values
			vertexSize = vertexArray.length*(Float.SIZE/8);
			normalSize = normalArray.length*(Float.SIZE/8);
			texSize = textureArray.length*(Float.SIZE/8);

			gl.glBufferData(GL_ARRAY_BUFFER, vertexSize +normalSize + texSize ,
					null, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8

			// Load the real data separately.  We put the colors right after the vertex coordinates,
			// so, the offset for colors is the size of vertices in bytes
			gl.glBufferSubData( GL_ARRAY_BUFFER, 0, vertexSize, vertices );
			gl.glBufferSubData( GL_ARRAY_BUFFER, vertexSize, normalSize, normals );
			gl.glBufferSubData( GL_ARRAY_BUFFER, vertexSize, texSize, textures );

			IntBuffer elements = IntBuffer.wrap(vertexIndexs);

			long indexSize = vertexIndexs.length*(Integer.SIZE/8);
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize,
					elements, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8
			//Introduces the texture, position and normal coordinates to the textured cube
			gl.glEnableVertexAttribArray(vPosition);
			gl.glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, 0, 0L);
			gl.glEnableVertexAttribArray(vNormal);
			gl.glVertexAttribPointer(vNormal, 3, GL_FLOAT, false, 0, vertexSize);
			gl.glEnableVertexAttribArray( vTexCoord );
			gl.glVertexAttribPointer( vTexCoord, 2, GL_FLOAT, false, 0, vertexSize);

		}

		public void bindObject(GL3 gl){
			gl.glBindVertexArray(VAOs[idPoint]);
			gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[idBuffer]);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOs[idElement]);
		}

	}
}