import Basic.ShaderProg;
import Basic.Transform;
import Basic.Vec4;
import Objects.SObject;
import Objects.SPly;
import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jogamp.opengl.GL3.*;

public class CGCWTask2 {

	final GLWindow window; //Define a window
	final FPSAnimator animator=new FPSAnimator(60, true);
	final Renderer renderer = new Renderer();

	public CGCWTask2() {
		GLProfile glp = GLProfile.get(GLProfile.GL3);
		GLCapabilities caps = new GLCapabilities(glp);
		window = GLWindow.create(caps);

		window.addGLEventListener(renderer); //Set the canvas to listen GLEvents

		animator.add(window);

		window.setTitle("Coursework Task 2 Feature 1");
		window.setSize(500,500);
		window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
		window.setVisible(true);

		animator.start();
	}

	public static void main(String[] args) {
		new CGCWTask2();

	}

	class Renderer implements GLEventListener {

		private Transform T = new Transform(); //model_view transform

		//VAOs and VBOs parameters
		private int idPoint=0, numVAOs = 1;
		private int idBuffer=0, numVBOs = 1;
		private int idElement=0, numEBOs = 1;
		private int[] VAOs = new int[numVAOs];
		private int[] VBOs = new int[numVBOs];
		private int[] EBOs = new int[numEBOs];

		//Model parameters
		private int[] numElements = new int[numEBOs];
		private long vertexSize;
		private long normalSize;
		private int vPosition;
		private int vNormal;

		private float[] vertexCoord;
		private int[] indicesList;
		private int vertexPly;
		private int faceNumPly;
		//Transformation parameters
		private int ModelView;
		private int NormalTransform;
		private int Projection;

		//Set the rotation angle along the x axis to -90 degrees, meaning 90 degrees clockwise
		private float rotateXangle = -0;
		private float rotateYangle = -0;
		private float rotateZangle = -0;
		//Lighting parameter
		private int AmbientProduct;
		private int DiffuseProduct;
		private int SpecularProduct;
		private int Shininess;

		private float[] ambient1;
		private float[] diffuse1;
		private float[] specular1;
		private float  materialShininess1;

		@Override
		public void display(GLAutoDrawable drawable) {
			GL3  gl = drawable.getGL().getGL3(); // Get the GL pipeline object this
			gl.glEnable(GL_DEPTH_TEST);
			gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			gl.glClearColor(1.0f,1.0f,1.0f,1.0f);
			//Transformation for the first object (a ply file)
			T.initialize();
			T.scale(0.15f, 0.15f, 0.15f);
			//moved the sphere upwards
			T.translate(0.0f, 0.0f, 0.0f);
			//Rotation so you can see the ply file in multiple angles
			T.rotateX(rotateXangle);
			T.rotateY(rotateYangle);
			T.rotateZ(rotateZangle);

			//Add code here to transform the ply file into the position
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

			rotateXangle += 1f;
			rotateYangle += 1f;
			rotateZangle += 1f;
		}


		@Override
		public void init(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this 

			System.out.print("GL_Version: " + gl.glGetString(GL_VERSION));

			//compile and use the shader program
			ShaderProg shaderproc = new ShaderProg(gl, "Gouraud.vert", "Gouraud.frag");
			int program = shaderproc.getProgram();
			gl.glUseProgram(program);

			// Initialize the vertex position and normal attribute in the vertex shader    
			vPosition = gl.glGetAttribLocation( program, "vPosition" );
			vNormal = gl.glGetAttribLocation( program, "vNormal" );

			// Get connected with the ModelView, NormalTransform, and Projection matrices
			// in the vertex shader
			ModelView = gl.glGetUniformLocation(program, "ModelView");
			NormalTransform = gl.glGetUniformLocation(program, "NormalTransform");
			Projection = gl.glGetUniformLocation(program, "Projection");

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
			float[] lightPosition = {10.0f, 15.0f, 20.0f, 1.0f};
			Vec4 lightAmbient = new Vec4(0.7f, 0.7f, 0.7f, 1.0f);
			Vec4 lightDiffuse = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			Vec4 lightSpecular = new Vec4(1.0f, 1.0f, 1.0f, 10.0f);

			gl.glUniform4fv( gl.glGetUniformLocation(program, "LightPosition"),
					1, lightPosition, 0 );
			//Load the vertex and indices from the imported ply file
			try {
				BufferedReader ply = null;
				//read the ply file
				ply = new BufferedReader(new FileReader("teddy.ply"));
//				ply = new BufferedReader(new FileReader("pyramid.ply"));
				String line;
				String[] linetemp;
				int lineCounter = 0;
				int commentnumber = 0; //how many lines are comments
				boolean elementVertexFound = false; //Whether element vertex [number] is already read
				while ((line = ply.readLine()) != null){ //while there is lines in the ply file
					lineCounter += 1;
					if (line.contains("vertex")){ //if line has first instance of vertex
						if (!elementVertexFound){
							elementVertexFound = true; //ensures all following instances of word ´vertex is dismissed
							linetemp = line.split("\\s+"); //as line is string
							vertexPly = Integer.parseInt(linetemp[2]); //convert the numbered index of this line to integer
							vertexCoord = new float[vertexPly*3];
						}
					}
					if (line.contains("face")){
						linetemp = line.split("\\s+");
						faceNumPly = Integer.parseInt(linetemp[2]);
						indicesList = new int[faceNumPly*3];
							}
					if (line.contains("comment")){
						commentnumber +=1;}


					if (lineCounter == 10 + commentnumber){//the first vertex coordinates
						int vertexCount = 0; //For the first vertex coordinates
						linetemp = line.split("\\s+");
						vertexCoord[0] = Float.parseFloat(linetemp[0]);
						vertexCoord[1] = Float.parseFloat(linetemp[1]);
						vertexCoord[2] = Float.parseFloat(linetemp[2]);
						System.out.println(vertexCoord);
						vertexCount++;
						while (vertexCount < vertexPly){ //For the remaining vertex coordinates
							// why less than vertexPly value? 1st already done, and this does the remaining
							//eg in teddy.ply with 202 vertices, this does the remaining 201
							line = ply.readLine();
							linetemp = (line.split("\\s+"));
							vertexCoord[vertexCount*3] = Float.parseFloat(linetemp[0]);
							vertexCoord[vertexCount*3+1] = Float.parseFloat(linetemp[1]);
							vertexCoord[vertexCount*3+2] = Float.parseFloat(linetemp[2]);
							System.out.println(vertexCoord);
							vertexCount++;
						}
						int indicesCount = 0;
						while (indicesCount < faceNumPly){ //For the indices connection
							line = ply.readLine();
							linetemp = (line.split("\\s+"));
							indicesList[indicesCount*3] = Integer.parseInt(linetemp[1]);
							indicesList[indicesCount*3+1] = Integer.parseInt(linetemp[2]);
							indicesList[indicesCount*3+2] = Integer.parseInt(linetemp[3]);
							//Outputs for debugs
							System.out.println(indicesList);
							indicesCount++;
						}
					}
					}
			}
			catch (IOException e) { //if nothing loaded
				e.printStackTrace();
			}
			//(This partial works)
//			SObject ply = new SPly(vertexPly,faceNumPly,vertexCoord, indicesList); //Unsmoothed ply file, as imported from ply file


			// vertexSubdivision and indiceSubdivision does the same thing, except they return their respective values,
			// because Java is only capable of returning things of one class type, i.e. float, string, int only.

			//After First Catmull Clark Subdivision (No subdividing works)
			// comment out the SPly code above and uncomment the pile of code beneath between line 256 and 275


			float[] vertexCoordSubdivision = vertexSubdivision(vertexCoord, indicesList, vertexPly, faceNumPly);
			int[] indicesListSubdivision = indiceSubdivision(vertexCoord, indicesList, vertexPly, faceNumPly);
			vertexPly *= 6;
			faceNumPly = faceNumPly*faceNumPly;

			//After Second Catmull Clark Subdivision
			vertexCoordSubdivision = vertexSubdivision(vertexCoordSubdivision, indicesListSubdivision, vertexPly, faceNumPly);
			indicesListSubdivision = indiceSubdivision(vertexCoordSubdivision, indicesListSubdivision, vertexPly, faceNumPly);
			vertexPly *= 6;
			faceNumPly = faceNumPly*faceNumPly;

			//After Third Catmull Clark Subdivision
			vertexCoordSubdivision = vertexSubdivision(vertexCoordSubdivision, indicesListSubdivision, vertexPly, faceNumPly);
			indicesListSubdivision = indiceSubdivision(vertexCoordSubdivision, indicesListSubdivision, vertexPly, faceNumPly);
			vertexPly *= 6;
			faceNumPly = faceNumPly*faceNumPly;
			//create the object: a ply file

			SObject ply = new SPly(vertexPly,faceNumPly,vertexCoordSubdivision, indicesListSubdivision); //Uncomment this and comment the SObject ply above to see the smoothen ply file.


			idPoint=0;
			idBuffer=0;
			idElement=0;
			createObject(gl, ply);

			// Set ply material
			Vec4 materialAmbient1 = new Vec4(0.5f, 0.0f, 0.0f, 1.0f);
			Vec4 materialDiffuse1 = new Vec4(0.7f, 0.0f, 0.0f, 1.0f);
			Vec4 materialSpecular1 = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			materialShininess1 = 64.0f;

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
				T.ortho(-10, 10, -10/a, 10/a, -10, 10);
			}
			else{
				T.ortho(-10*a, 10*a, -10, 10, -10, 10);
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
			numElements[idElement] = obj.getNumIndices();
			bindObject(gl);

			FloatBuffer vertices = FloatBuffer.wrap(vertexArray);
			FloatBuffer normals = FloatBuffer.wrap(normalArray);

			// Create an empty buffer with the size we need
			// and a null pointer for the data values
			vertexSize = vertexArray.length*(Float.SIZE/8);
			normalSize = normalArray.length*(Float.SIZE/8);
			gl.glBufferData(GL_ARRAY_BUFFER, vertexSize +normalSize,
					null, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8

			// Load the real data separately.  We put the colors right after the vertex coordinates,
			// so, the offset for colors is the size of vertices in bytes
			gl.glBufferSubData( GL_ARRAY_BUFFER, 0, vertexSize, vertices );
			gl.glBufferSubData( GL_ARRAY_BUFFER, vertexSize, normalSize, normals );

			IntBuffer elements = IntBuffer.wrap(vertexIndexs);

			long indexSize = vertexIndexs.length*(Integer.SIZE/8);
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize,
					elements, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8
			gl.glEnableVertexAttribArray(vPosition);
			gl.glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, 0, 0L);
			gl.glEnableVertexAttribArray(vNormal);
			gl.glVertexAttribPointer(vNormal, 3, GL_FLOAT, false, 0, vertexSize);
		}

		public void bindObject(GL3 gl){
			gl.glBindVertexArray(VAOs[idPoint]);
			gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[idBuffer]);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOs[idElement]);
		}

		public float[] vertexSubdivision(float[] vertexCoordBefore, int[] indicesListBefore, int vertexNumBefore, int faceNumBefore){
			//Attempted to use Catmull-Clark Subdivision Principles
			float[] vertexCoordNow = new float[vertexNumBefore*6*3]; //each triangle will  be made of 6 triangle
			int[] indicesListNow = new int[faceNumBefore*faceNumBefore*3]; //indices will be the square of the initial number of indices
			int k = 0;
			int j = 0;
			for (int l = 0; l < vertexNumBefore*3; l++){
				vertexCoordNow[l] = vertexCoordBefore[l]; //duplicate vertexcoordbefore into vertexcoordnow
			}
			for (int p = 0; p < indicesListBefore.length; p++){
				indicesListNow[p] = indicesListBefore[p]; //duplicate indicesListNow into indicesListNow
			}
			// Generate coordinates of facepoints and insert into vertexCoordNow
			for (j = 0; j < faceNumBefore; j++) { //each of the 400 faces
				float index0xpoint = vertexCoordBefore[indicesListBefore[3 * k] * 3];//get the vertex of first index in line
				float index0ypoint = vertexCoordBefore[indicesListBefore[3 * k] * 3 + 1];
				float index0zpoint = vertexCoordBefore[indicesListBefore[3 * k] * 3 + 2];
				float index1xpoint = vertexCoordBefore[indicesListBefore[3 * k + 1] * 3]; //get the vertex of second index in line
				float index1ypoint = vertexCoordBefore[indicesListBefore[3 * k + 1] * 3 + 1];
				float index1zpoint = vertexCoordBefore[indicesListBefore[3 * k + 1] * 3 + 2];
				float index2xpoint = vertexCoordBefore[indicesListBefore[3 * k + 2] * 3]; //get the vertex of third index in line
				float index2ypoint = vertexCoordBefore[indicesListBefore[3 * k + 2] * 3 + 1];
				float index2zpoint = vertexCoordBefore[indicesListBefore[3 * k + 2] * 3 + 2];

				float xFacePoint = (index0xpoint + index1xpoint + index2xpoint) / 3; //average of face points
				float yFacePoint = (index0ypoint + index1ypoint + index2ypoint) / 3;
				float zFacePoint = (index0zpoint + index1zpoint + index2zpoint) / 3;

				vertexCoordNow[(vertexNumBefore*3) + (3 * k)] = xFacePoint;  //vertexNumBefore*3 already used as original coordinates
				vertexCoordNow[(vertexNumBefore*3) + (3 * k + 1)] = yFacePoint;
				vertexCoordNow[(vertexNumBefore*3) + (3 * k + 2)] = zFacePoint;

				indicesListNow[(faceNumBefore*3) + (3*j)] = faceNumBefore + j; //Values of new indices
				indicesListNow[(faceNumBefore*3) + (3*j+1)] = j;
				indicesListNow[(faceNumBefore*3) + (3*j)] =  2*k;
				k++;
				}
			int a = 0;
			int neighbourfaceindex;
			int neighbourfaceindex2;
			int neighbourfaceindex3;
			int neighbourfaceindex4;
			//Find and add the edge points
			for (int i =0; i < faceNumBefore; i += 3){
				neighbourfaceindex = indicesListBefore[i]; //subject vertex index 0
				neighbourfaceindex2 = indicesListBefore[i+1]; //subject vertex index 1
				neighbourfaceindex3 = indicesListBefore[i+2]; //variable to prevents the subject triangle from being used
				//use the neighbourindexfind function to locate the other adjoining triangle facepoint
				neighbourfaceindex4 = neighbourindexfind(indicesListBefore, neighbourfaceindex, neighbourfaceindex2, neighbourfaceindex3, faceNumBefore);

				//find the endpoints of the line
				float lineEndPointAx = vertexCoordNow[(3 * neighbourfaceindex)];
				float lineEndPointAy = vertexCoordNow[(3 * neighbourfaceindex + 1)];
				float lineEndPointAz = vertexCoordNow[(3 * neighbourfaceindex + 2)];
				float lineEndPointBx = vertexCoordNow[(3 * neighbourfaceindex2)];
				float lineEndPointBy = vertexCoordNow[(3 * neighbourfaceindex2 + 1)];
				float lineEndPointBz = vertexCoordNow[(3 * neighbourfaceindex2 + 2)];

				//find the facepoints of the line´s adjacent traingles
				float facepointAx = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex3)];
				float facepointAy = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex3 + 1)];
				float facepointAz = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex3 + 2)];
				float facepointBx = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex4)];
				float facepointBy = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex4 + 1)];
				float facepointBz = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex4 + 2)];

				//find the average of the 2 endpoint and 2 face points coordinates
				float edgePointx = (lineEndPointAx + lineEndPointBx + facepointAx + facepointBx) / 4;
				float edgePointy = (lineEndPointAy + lineEndPointBy + facepointAy + facepointBy) / 4;
				float edgePointz = (lineEndPointAz + lineEndPointBz + facepointAz + facepointBz) / 4;

				vertexCoordNow[(3* (vertexNumBefore + faceNumBefore)) + (3*i)] = edgePointx;
				vertexCoordNow[(3* (vertexNumBefore + faceNumBefore)) + (3*i+1)] = edgePointy;
				vertexCoordNow[(3* (vertexNumBefore + faceNumBefore)) + (3*i+2)] = edgePointz;

				//Introducing new indices
				indicesListNow[(3*(faceNumBefore+faceNumBefore)) + (3*i)] = (faceNumBefore+neighbourfaceindex);
				indicesListNow[(3*(faceNumBefore+faceNumBefore)) + (3*i+1)] = (faceNumBefore+neighbourfaceindex2);
				indicesListNow[(3*(faceNumBefore+faceNumBefore)) + (3*i+1)] = (faceNumBefore+neighbourfaceindex3);
				}

			//generate new average vertex coordinate
			for (int i = 0; i < vertexNumBefore; i++){
				//Huge needed variables
				int pointLineCount = 0;
				float meanFaceCalcX = 0;
				float meanInitEdgeCalcX = 0;
				float meanFaceCalcY = 0;
				float meanInitEdgeCalcY = 0;
				float meanFaceCalcZ = 0;
				float meanInitEdgeCalcZ = 0;
				List indexListForEdge = new ArrayList<>();
				int edgeValue1Index = 0;
				int edgeValue2Index = 0;
				float edgeValue1x = 0;
				float edgeValue2x = 0;
				float edgeValue1y = 0;
				float edgeValue2y = 0;
				float edgeValue1z = 0;
				float edgeValue2z = 0;


				for (j = 0; j < faceNumBefore; j++){
					if ((i == indicesListBefore[3*j])
					     || i == indicesListBefore[3*j+1]
						 || i == indicesListBefore[3*j+2]){
						pointLineCount+=1; //Add 1 to pointLineCount for each index of vertex J found in file
						//Add the values of the face calculation
						meanFaceCalcX += vertexCoordNow[(3*vertexNumBefore) + (3*j)];
						meanFaceCalcY += vertexCoordNow[(3*vertexNumBefore) + (3*j+1)];
						meanFaceCalcZ += vertexCoordNow[(3*vertexNumBefore) + (3*j+2)];

						//Which index is the subject index in from the ply file of faces
						if (i == indicesListBefore[3*j]){
							edgeValue1Index = indicesListBefore[3*j+1];
							edgeValue2Index = indicesListBefore[3*j+2];
							//Attach the value of i the the two edgevalue indices
							indicesListNow[(3*(3*faceNumBefore))+(3*j)] = edgeValue1Index;
							indicesListNow[(3*(3*faceNumBefore))+(3*j)+1] = i;
							indicesListNow[(3*(3*faceNumBefore))+(3*j)+2] = edgeValue2Index;
							if (!indexListForEdge.contains(indicesListBefore[3*j])) {
								indexListForEdge.add(indicesListBefore[3 * j]);

							}
							if (!indexListForEdge.contains(indicesListBefore[3*j+1])){
								indexListForEdge.add(indicesListBefore[3*j+1]);
							}
							if (!indexListForEdge.contains(indicesListBefore[3*j+2])){
								indexListForEdge.add(indicesListBefore[3*j+2]);
							}
						}
						if (i == indicesListBefore[3*j+1]){
							edgeValue1Index = indicesListBefore[3*j];
							edgeValue2Index = indicesListBefore[3*j+2];
							indicesListNow[(3*(3*faceNumBefore))+(3*j)] = edgeValue1Index;
							indicesListNow[(3*(3*faceNumBefore))+(3*j)+1] = i;
							indicesListNow[(3*(3*faceNumBefore))+(3*j)+2] = edgeValue2Index;

							if (!indexListForEdge.contains(indicesListBefore[3*j])) {
								indexListForEdge.add(indicesListBefore[3 * j]);
							}
							if (!indexListForEdge.contains(indicesListBefore[3*j+1])){
								indexListForEdge.add(indicesListBefore[3*j+1]);
							}
							if (!indexListForEdge.contains(indicesListBefore[3*j+2])){
								indexListForEdge.add(indicesListBefore[3*j+2]);

							}
						}
						if (i == indicesListBefore[3*j+2]){
							edgeValue1Index = indicesListBefore[3*j];
							edgeValue2Index = indicesListBefore[3*j+1];
							indicesListNow[(3*(3*faceNumBefore))+(3*j)] = edgeValue1Index;
							indicesListNow[(3*(3*faceNumBefore))+(3*j)+1] = i;
							indicesListNow[(3*(3*faceNumBefore))+(3*j)+2] = edgeValue2Index;
							if (!indexListForEdge.contains(indicesListBefore[3*j])) {
								indexListForEdge.add(indicesListBefore[3 * j]);
							}
							if (!indexListForEdge.contains(indicesListBefore[3*j+1])){
								indexListForEdge.add(indicesListBefore[3*j+1]);
							}
							if (!indexListForEdge.contains(indicesListBefore[3*j+2])){
								indexListForEdge.add(indicesListBefore[3*j+2]);

							}

						}
						edgeValue1x = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue1Index])/2;
						edgeValue1y = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue1Index+1])/2;
						edgeValue1z = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue1Index+2])/2;
						edgeValue2x = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue2Index])/2;
						edgeValue2y = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue2Index+1])/2;
						edgeValue2z = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue2Index+2])/2;

						// Add the values of the two mean calculation of the original midway edge values
						meanInitEdgeCalcX += edgeValue1x + edgeValue2x;
						meanInitEdgeCalcY += edgeValue1y + edgeValue2y;
						meanInitEdgeCalcZ += edgeValue1z + edgeValue2z;
					}

				}
				//Divide all the means by the pointLineCount
				meanFaceCalcX /= pointLineCount;
				meanFaceCalcY /= pointLineCount;
				meanFaceCalcZ /= pointLineCount;
				meanInitEdgeCalcX /= pointLineCount;
				meanInitEdgeCalcY /= pointLineCount;
				meanInitEdgeCalcZ /= pointLineCount;
				//Create new vertex coordinates
				float newVertexX = ((meanFaceCalcX)/pointLineCount) + ((2*(meanInitEdgeCalcX))/pointLineCount)
								  + (((pointLineCount - 3)*(vertexCoordNow[3*i]))/pointLineCount);
				float newVertexY = ((meanFaceCalcY)/pointLineCount) + ((2*(meanInitEdgeCalcY))/pointLineCount)
						+ (((pointLineCount - 3)*(vertexCoordNow[3*i+1]))/pointLineCount);
				float newVertexZ = ((meanFaceCalcZ)/pointLineCount) + ((2*(meanInitEdgeCalcZ))/pointLineCount)
						+ (((pointLineCount - 3)*(vertexCoordNow[3*i+2]))/pointLineCount);
				//Then, replaces the original vertex coordinates with new coordinates, this does not impact the values of the indices for the face.
				vertexCoordNow[3*i] = newVertexX;
				vertexCoordNow[3*i+1] = newVertexY;
				vertexCoordNow[3*i+2] = newVertexZ;
			}
			return vertexCoordNow;
			}

		}

	public int[] indiceSubdivision(float[] vertexCoordBefore, int[] indicesListBefore, int vertexNumBefore, int faceNumBefore){
		//Attempted to use Catmull-Clark Subdivision Principles
		//It is the same algorithms and actions as vertexSubdivision, but returns indicesListNow.
		float[] vertexCoordNow = new float[vertexNumBefore*6*3]; //each triangle will  be made of 6 triangle
		int[] indicesListNow = new int[faceNumBefore*faceNumBefore*3];
		int k = 0;
		int j = 0;
		for (int l = 0; l < vertexNumBefore*3; l++){
			vertexCoordNow[l] = vertexCoordBefore[l]; //duplicate vertexcoordbefore into vertexcoordnow
		}
		for (int p = 0; p < indicesListBefore.length; p++){
			indicesListNow[p] = indicesListBefore[p]; //duplicate indicesListNow into indicesListNow
		}
		// Generate coordinates of facepoints and insert into vertexCoordNow
		for (j = 0; j < faceNumBefore; j++) { //each of the 400 faces
			float index0xpoint = vertexCoordBefore[indicesListBefore[3 * k] * 3];//get the vertex of first index in line
			float index0ypoint = vertexCoordBefore[indicesListBefore[3 * k] * 3 + 1];
			float index0zpoint = vertexCoordBefore[indicesListBefore[3 * k] * 3 + 2];
			float index1xpoint = vertexCoordBefore[indicesListBefore[3 * k + 1] * 3]; //get the vertex of second index in line
			float index1ypoint = vertexCoordBefore[indicesListBefore[3 * k + 1] * 3 + 1];
			float index1zpoint = vertexCoordBefore[indicesListBefore[3 * k + 1] * 3 + 2];
			float index2xpoint = vertexCoordBefore[indicesListBefore[3 * k + 2] * 3]; //get the vertex of third index in line
			float index2ypoint = vertexCoordBefore[indicesListBefore[3 * k + 2] * 3 + 1];
			float index2zpoint = vertexCoordBefore[indicesListBefore[3 * k + 2] * 3 + 2];

			float xFacePoint = (index0xpoint + index1xpoint + index2xpoint) / 3; //average of face points
			float yFacePoint = (index0ypoint + index1ypoint + index2ypoint) / 3;
			float zFacePoint = (index0zpoint + index1zpoint + index2zpoint) / 3;

			vertexCoordNow[(vertexNumBefore*3) + (3 * k)] = xFacePoint;  //vertexNumBefore*3 already used as original coordinates
			vertexCoordNow[(vertexNumBefore*3) + (3 * k + 1)] = yFacePoint;
			vertexCoordNow[(vertexNumBefore*3) + (3 * k + 2)] = zFacePoint;

			indicesListNow[(faceNumBefore*3) + (3*j)] = faceNumBefore + j;
			indicesListNow[(faceNumBefore*3) + (3*j+1)] = j;
			indicesListNow[(faceNumBefore*3) + (3*j)] =  2*k;
			k++;
		}
		int a = 0;
		int neighbourfaceindex;
		int neighbourfaceindex2;
		int neighbourfaceindex3;
		int neighbourfaceindex4;
		//Find and add the edge points
		for (int i =0; i < faceNumBefore; i += 3){
			neighbourfaceindex = indicesListBefore[i]; //subject vertex index 0
			neighbourfaceindex2 = indicesListBefore[i+1]; //subject vertex index 1
			neighbourfaceindex3 = indicesListBefore[i+2]; //variable to prevents the subject triangle from being used
			//use the neighnourindexfind function to locate the other adjoining triangle facepoint
			neighbourfaceindex4 = neighbourindexfind(indicesListBefore, neighbourfaceindex, neighbourfaceindex2, neighbourfaceindex3, faceNumBefore);

			float lineEndPointAx = vertexCoordNow[(3 * neighbourfaceindex)];
			float lineEndPointAy = vertexCoordNow[(3 * neighbourfaceindex + 1)];
			float lineEndPointAz = vertexCoordNow[(3 * neighbourfaceindex + 2)];
			float lineEndPointBx = vertexCoordNow[(3 * neighbourfaceindex2)];
			float lineEndPointBy = vertexCoordNow[(3 * neighbourfaceindex2 + 1)];
			float lineEndPointBz = vertexCoordNow[(3 * neighbourfaceindex2 + 2)];
			float facepointAx = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex3)];
			float facepointAy = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex3 + 1)];
			float facepointAz = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex3 + 2)];
			float facepointBx = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex4)];
			float facepointBy = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex4 + 1)];
			float facepointBz = vertexCoordNow[(3 * vertexNumBefore) + (3 * neighbourfaceindex4 + 2)];

			float edgePointx = (lineEndPointAx + lineEndPointBx + facepointAx + facepointBx) / 4;
			float edgePointy = (lineEndPointAy + lineEndPointBy + facepointAy + facepointBy) / 4;
			float edgePointz = (lineEndPointAz + lineEndPointBz + facepointAz + facepointBz) / 4;

			vertexCoordNow[(3* (vertexNumBefore + faceNumBefore)) + (3*i)] = edgePointx;
			vertexCoordNow[(3* (vertexNumBefore + faceNumBefore)) + (3*i+1)] = edgePointy;
			vertexCoordNow[(3* (vertexNumBefore + faceNumBefore)) + (3*i+2)] = edgePointz;
			indicesListNow[(3*(faceNumBefore+faceNumBefore)) + (3*i)] = (faceNumBefore+neighbourfaceindex);
			indicesListNow[(3*(faceNumBefore+faceNumBefore)) + (3*i+1)] = (faceNumBefore+neighbourfaceindex2);
			indicesListNow[(3*(faceNumBefore+faceNumBefore)) + (3*i+1)] = (faceNumBefore+neighbourfaceindex3);
		}

		//generate new average vertex coordinate
		for (int i = 0; i < vertexNumBefore; i++){
			int pointLineCount = 0;
			float meanFaceCalcX = 0;
			float meanInitEdgeCalcX = 0;
			float meanFaceCalcY = 0;
			float meanInitEdgeCalcY = 0;
			float meanFaceCalcZ = 0;
			float meanInitEdgeCalcZ = 0;
			List indexListForEdge = new ArrayList<>();
			int edgeValue1Index = 0;
			int edgeValue2Index = 0;
			float edgeValue1x = 0;
			float edgeValue2x = 0;
			float edgeValue1y = 0;
			float edgeValue2y = 0;
			float edgeValue1z = 0;
			float edgeValue2z = 0;
			for (j = 0; j < faceNumBefore; j++){
				if ((i == indicesListBefore[3*j])
						|| i == indicesListBefore[3*j+1]
						|| i == indicesListBefore[3*j+2]){
					pointLineCount+=1;
					meanFaceCalcX += vertexCoordNow[(3*vertexNumBefore) + (3*j)];
					meanFaceCalcY += vertexCoordNow[(3*vertexNumBefore) + (3*j+1)];
					meanFaceCalcZ += vertexCoordNow[(3*vertexNumBefore) + (3*j+2)];

					if (i == indicesListBefore[3*j]){
						edgeValue1Index = indicesListBefore[3*j+1];
						edgeValue2Index = indicesListBefore[3*j+2];
						indicesListNow[(3*(3*faceNumBefore))+(3*j)] = edgeValue1Index;
						indicesListNow[(3*(3*faceNumBefore))+(3*j)+1] = i;
						indicesListNow[(3*(3*faceNumBefore))+(3*j)+2] = edgeValue2Index;
						if (!indexListForEdge.contains(indicesListBefore[3*j])) {
							indexListForEdge.add(indicesListBefore[3 * j]);

						}
						if (!indexListForEdge.contains(indicesListBefore[3*j+1])){
							indexListForEdge.add(indicesListBefore[3*j+1]);
						}
						if (!indexListForEdge.contains(indicesListBefore[3*j+2])){
							indexListForEdge.add(indicesListBefore[3*j+2]);
						}
					}
					if (i == indicesListBefore[3*j+1]){
						edgeValue1Index = indicesListBefore[3*j];
						edgeValue2Index = indicesListBefore[3*j+2];
						indicesListNow[(3*(3*faceNumBefore))+(3*j)] = edgeValue1Index;
						indicesListNow[(3*(3*faceNumBefore))+(3*j)+1] = i;
						indicesListNow[(3*(3*faceNumBefore))+(3*j)+2] = edgeValue2Index;

						if (!indexListForEdge.contains(indicesListBefore[3*j])) {
							indexListForEdge.add(indicesListBefore[3 * j]);
						}
						if (!indexListForEdge.contains(indicesListBefore[3*j+1])){
							indexListForEdge.add(indicesListBefore[3*j+1]);
						}
						if (!indexListForEdge.contains(indicesListBefore[3*j+2])){
							indexListForEdge.add(indicesListBefore[3*j+2]);

						}
					}
					if (i == indicesListBefore[3*j+2]){
						edgeValue1Index = indicesListBefore[3*j];
						edgeValue2Index = indicesListBefore[3*j+1];
						indicesListNow[(3*(3*faceNumBefore))+(3*j)] = edgeValue1Index;
						indicesListNow[(3*(3*faceNumBefore))+(3*j)+1] = i;
						indicesListNow[(3*(3*faceNumBefore))+(3*j)+2] = edgeValue2Index;
						if (!indexListForEdge.contains(indicesListBefore[3*j])) {
							indexListForEdge.add(indicesListBefore[3 * j]);
						}
						if (!indexListForEdge.contains(indicesListBefore[3*j+1])){
							indexListForEdge.add(indicesListBefore[3*j+1]);
						}
						if (!indexListForEdge.contains(indicesListBefore[3*j+2])){
							indexListForEdge.add(indicesListBefore[3*j+2]);

						}

					}
					edgeValue1x = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue1Index])/2;
					edgeValue1y = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue1Index+1])/2;
					edgeValue1z = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue1Index+2])/2;
					edgeValue2x = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue2Index])/2;
					edgeValue2y = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue2Index+1])/2;
					edgeValue2z = (vertexCoordNow[i] + vertexCoordNow[3*edgeValue2Index+2])/2;;

					meanInitEdgeCalcX += edgeValue1x + edgeValue2x;
					meanInitEdgeCalcY += edgeValue1y + edgeValue2y;
					meanInitEdgeCalcZ += edgeValue1z + edgeValue2z;
				}

			}

			meanFaceCalcX /= pointLineCount;
			meanFaceCalcY /= pointLineCount;
			meanFaceCalcZ /= pointLineCount;
			meanInitEdgeCalcX /= pointLineCount;
			meanInitEdgeCalcY /= pointLineCount;
			meanInitEdgeCalcZ /= pointLineCount;
			float newVertexX = ((meanFaceCalcX)/pointLineCount) + ((2*(meanInitEdgeCalcX))/pointLineCount)
					+ (((pointLineCount - 3)*(vertexCoordNow[3*i]))/pointLineCount);
			float newVertexY = ((meanFaceCalcY)/pointLineCount) + ((2*(meanInitEdgeCalcY))/pointLineCount)
					+ (((pointLineCount - 3)*(vertexCoordNow[3*i+1]))/pointLineCount);
			float newVertexZ = ((meanFaceCalcZ)/pointLineCount) + ((2*(meanInitEdgeCalcZ))/pointLineCount)
					+ (((pointLineCount - 3)*(vertexCoordNow[3*i+2]))/pointLineCount);

			vertexCoordNow[3*i] = newVertexX;
			vertexCoordNow[3*i+1] = newVertexY;
			vertexCoordNow[3*i+2] = newVertexZ;
		}
		return indicesListNow;
	}

		public int neighbourindexfind(int[] indicesListBeforeFind, int faceindex, int faceindex2, int faceindex3, int faceNumBefore){
		//finds the index of the neighbour facepoint, after getting the face index of the triangle with the adjacent triangle connnecting to faceindex and faceindex2
		for (int m = 0; m < faceNumBefore; m++) {
			if ((faceindex == indicesListBeforeFind[3 * m]
					|| faceindex == indicesListBeforeFind[3 * m + 1]
					|| faceindex == indicesListBeforeFind[3 * m + 2])
					&& (faceindex2 == indicesListBeforeFind[3 * m + 1]
					|| faceindex2 == indicesListBeforeFind[3 * m]
					|| faceindex2 == indicesListBeforeFind[3 * m + 2])
					&& (faceindex3 == indicesListBeforeFind[3 * m + 2]
					|| faceindex3 == indicesListBeforeFind[3 * m + 1]
					|| faceindex3 == indicesListBeforeFind[3 * m])) { //Prevents the same triangle from forming
				continue;
			}
			if ((faceindex == indicesListBeforeFind[3 * m] //checks for the other vertex connecting to this edge
					|| faceindex == indicesListBeforeFind[3 * m + 1]
					|| faceindex == indicesListBeforeFind[3 * m + 2])
					&& ((faceindex == indicesListBeforeFind[3 * m]
					|| faceindex == indicesListBeforeFind[3 * m + 1]
					|| faceindex == indicesListBeforeFind[3 * m + 2]))) {
				//Guaranteed the other vertex that connects to the edge is here
				if (faceindex2 == indicesListBeforeFind[3 * m]
						|| faceindex2 == indicesListBeforeFind[3 * m + 1]
						|| faceindex2 == indicesListBeforeFind[3 * m + 2]) {

				}
				if (faceindex == indicesListBeforeFind[3 * m] || faceindex == indicesListBeforeFind[3 * m + 1]) {
					if (faceindex2 == indicesListBeforeFind[3 * m] || faceindex2 == indicesListBeforeFind[3 * m + 1]) {
						return indicesListBeforeFind[3 * m + 2]; //found other vertex connected to subject edge
					}
				}

				if (faceindex == indicesListBeforeFind[3 * m] || faceindex == indicesListBeforeFind[3 * m + 2]) {
					if (faceindex2 == indicesListBeforeFind[3 * m] || faceindex2 == indicesListBeforeFind[3 * m + 2]) {
						return indicesListBeforeFind[3 * m + 1];
					}

					if (faceindex == indicesListBeforeFind[3 * m + 1] || faceindex == indicesListBeforeFind[3 * m]) {
						if (faceindex2 == indicesListBeforeFind[3 * m + 1] || faceindex2 == indicesListBeforeFind[3 * m]) {
							return indicesListBeforeFind[3 * m];
						}
					}
				}
			}
		}
		return -1; //filler to not crash
		}

}