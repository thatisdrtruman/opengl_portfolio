package Objects;
public class SCube extends SObject{
	private float edge;
	private int slices;
	private int stacks;

	public SCube(){
		super();
		init();
		update();
	}

	public SCube(float radius){
		super();
		init();
		this.edge = edge;
		update();
	}

	public SCube(float edge, int slices, int stacks){
		super();
		this.edge = edge;
		this.slices = slices;
		this.stacks = stacks;
		update();
	}
	
	private void init(){
		this.edge = 1;
		this.slices = 2;
		this.stacks = 2;
	}

	@Override
	protected void genData() {

		// Generate vertices coordinates, normal values, and texture coordinates
		numVertices = 8;
		vertices = new float[numVertices*9];
		normals = new float[numVertices*9];
		textures = new float[numVertices*6];

		//vertices on the main body
		//front face
		normals[0] = 0;  normals[1] = 0;  normals[2] = 0; //Bottom left front
		normals[3] = 1;   normals[4] = 0;  normals[5] = 0; //bottom right front
		normals[6] = 1;   normals[7] = 0;   normals[8] = 1; //top right front
		normals[9] = 0;  normals[10] = 0;  normals[11] = 1;//top left front
		//back face
		normals[12] = 0; normals[13] = 1; normals[14] =  0;//bottom left back
		normals[15] = 1; normals[16] = 1; normals[17] =  0;//bottom right back
		normals[18] =  1; normals[19] = 1; normals[20] =  1;// top right back
		normals[21] =  0; normals[22] =  1; normals[23] =  1; //top left back
		//left face
		normals[24] = 0;  normals[25] = 0;  normals[26] = 0; //Bottom left
		normals[27] = 0;   normals[28] = 1;  normals[29] = 0; //bottom right
		normals[30] = 0;   normals[31] = 1;   normals[32] = 1; //top right
		normals[33] = 0;  normals[34] = 0;  normals[35] = 1;//top left
		//right face
		normals[36] = 1; normals[37] = 0; normals[38] =  0;//bottom left
		normals[39] = 1; normals[40] = 1; normals[41] =  0;//bottom right
		normals[42] =  1; normals[43] = 1; normals[44] =  1;// top right
		normals[45] =  1; normals[46] =  0; normals[47] =  1; //top left
		//top face
		normals[48] = 0;  normals[49] = 0;  normals[50] = 1; //Bottom left
		normals[51] = 1;   normals[52] = 0;  normals[53] = 1; //bottom right
		normals[54] = 1;   normals[55] = 1;   normals[56] = 1; //top right
		normals[57] = 0;  normals[58] = 1;  normals[59] = 1;//top left
		//bottom face
		normals[60] = 0; normals[61] = 1; normals[62] =  0;//bottom left
		normals[63] = 1; normals[64] = 1; normals[65] =  0;//bottom right
		normals[66] =  1; normals[67] = 0; normals[68] =  0;// top right
		normals[69] =  0; normals[70] =  0; normals[71] =  0; //top left

		//front face
		vertices[0] = 0;  vertices[1] = 0;  vertices[2] = 0; //Bottom left front
		vertices[3] = 1;   vertices[4] = 0;  vertices[5] = 0; //bottom right front
		vertices[6] = 1;   vertices[7] = 0;   vertices[8] = 1; //top right front
		vertices[9] = 0;  vertices[10] = 0;  vertices[11] = 1;//top left front
		//back face
		vertices[12] = 0; vertices[13] = 1; vertices[14] =  0;//bottom left back
		vertices[15] = 1; vertices[16] = 1; vertices[17] =  0;//bottom right back
		vertices[18] =  1; vertices[19] = 1; vertices[20] =  1;// top right back
		vertices[21] =  0; vertices[22] =  1; vertices[23] =  1; //top left back
		//left face
		vertices[24] = 0;  vertices[25] = 0;  vertices[26] = 0; //Bottom left
		vertices[27] = 0;   vertices[28] = 1;  vertices[29] = 0; //bottom right
		vertices[30] = 0;   vertices[31] = 1;   vertices[32] = 1; //top right
		vertices[33] = 0;  vertices[34] = 0;  vertices[35] = 1;//top left
		//right face
		vertices[36] = 1; vertices[37] = 0; vertices[38] =  0;//bottom left
		vertices[39] = 1; vertices[40] = 1; vertices[41] =  0;//bottom right
		vertices[42] =  1; vertices[43] = 1; vertices[44] =  1;// top right
		vertices[45] =  1; vertices[46] =  0; vertices[47] =  1; //top left
		//top face
		vertices[48] = 0;  vertices[49] = 0;  vertices[50] = 1; //Bottom left
		vertices[51] = 1;   vertices[52] = 0;  vertices[53] = 1; //bottom right
		vertices[54] = 1;   vertices[55] = 1;   vertices[56] = 1; //top right
		vertices[57] = 0;  vertices[58] = 1;  vertices[59] = 1;//top left
		//bottom face
		vertices[60] = 0; vertices[61] = 1; vertices[62] =  0;//bottom left
		vertices[63] = 1; vertices[64] = 1; vertices[65] =  0;//bottom right
		vertices[66] =  1; vertices[67] = 0; vertices[68] =  0;// top right
		vertices[69] =  0; vertices[70] =  0; vertices[71] =  0; //top left
//												texture coordinates of:
		//front
		textures[0] = 0.0f;  textures[1] = 0.0f; //bottom left square
		textures[2] = 1.0f;  textures[3] = 0.0f; //bottom right
		textures[4] = 1.0f;  textures[5] = 1.0f; //top right
		textures[6] = 0.0f;   textures[7] = 1.0f;//top left
		//back
		textures[8] = 0.0f;  textures[9] = 0.0f;//bottom left square back
		textures[10] = 1.0f;  textures[11] = 0.0f;//bottom right
		textures[12] = 1.0f; textures[13] = 1.0f;//top right
		textures[14] = 0.0f; textures[15] = 1.0f;//top left
		//left
		textures[16] = 0.0f;  textures[17] = 0.0f; //bottom left square
		textures[18] = 1.0f;  textures[19] = 0.0f; //bottom right
		textures[20] = 1.0f;  textures[21] = 1.0f; //top right
		textures[22] = 0.0f;   textures[23] = 1.0f;//top left
		//right
		textures[24] = 0.0f;  textures[25] = 0.0f;//bottom left square back
		textures[26] = 1.0f;  textures[27] = 0.0f;//bottom right
		textures[28] = 1.0f; textures[29] = 1.0f;//top right
		textures[30] = 0.0f; textures[31] = 1.0f;//top left
		//top
		textures[32] = 0.0f;  textures[33] = 0.0f; //bottom left square
		textures[34] = 1.0f;  textures[35] = 0.0f; //bottom right
		textures[36] = 1.0f;  textures[37] = 1.0f; //top right
		textures[38] = 0.0f;   textures[39] = 1.0f;//top left
		//bottom
		textures[40] = 0.0f;  textures[41] = 0.0f;//bottom left square back
		textures[42] = 1.0f;  textures[43] = 0.0f;//bottom right
		textures[44] = 1.0f; textures[45] = 1.0f;//top right
		textures[46] = 0.0f; textures[47] = 1.0f;//top left

		// Generate indices for triangular mesh
		numIndices = 3*2*6; //36 indices triangles = 2 stacks, 3 slices, 6 faces
		indices = new int[numIndices];

		//front face
		//each square makes up of 2 triangles
		indices[0] = 0; indices[1] = 1; indices[2] = 2; //front bottom right
		indices[3] = 0; indices[4] = 2; indices[5] = 3; //front top left
		//back face
		indices[6] = 4; indices[7] = 5; indices[8] = 6;
		indices[9] = 4; indices[10] = 6; indices[11] = 7;
		//left face
		indices[12] = 8; indices[13] = 9; indices[14] = 10;
		indices[15] = 8; indices[16] = 10; indices[17] = 11;
		//right face
		indices[18] = 12; indices[19] = 13; indices[20] = 14;
		indices[21] = 12; indices[22] = 14; indices[23] = 15;
		//top face
		indices[24] = 16; indices[25] = 17; indices[26] = 18;
		indices[27] = 16; indices[28] = 18; indices[29] = 19;
		//bottom face
		indices[30] = 20; indices[31] = 21; indices[32] = 22;
		indices[33] = 20; indices[34] = 22; indices[35] = 23;


	}	
	
	public void setEdge(float edge){
		this.edge = edge;
		updated = false;
	}

	public void setSlices(int slices){
		this.slices = slices;
		updated = false;
	}

	public void setStacks(int stacks){
		this.stacks = stacks;
		updated = false;
	}
		
	public float getEdge(){
		return edge;
	}

	public int getSlices(){
		return slices;
	}

	public int getStacks(){
		return stacks;
	}	
}