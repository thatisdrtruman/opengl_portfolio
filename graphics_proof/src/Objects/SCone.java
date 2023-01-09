package Objects;
public class SCone extends SObject{
	private float radius;
	private int slices;
	private int stacks;
	private int height;
	//added integer height

	public SCone(){
		super();
		init();
		update();
	}

	public SCone(float radius, int height){
		super();
		init();
		this.radius = radius;
		this.height = height;
		update();
	}

	public SCone(float radius, int slices, int stacks, int height){
		super();
		this.radius = radius;
		this.slices = slices;
		this.stacks = stacks;
		this.height = height;
		update();
	}

	private void init(){
		this.radius = 1;
		this.slices = 360;
		this.stacks = 2;
		this.height = 2;
	}

	@Override
	protected void genData() {
		int i,j,k;

		double deltaLong=PI*2/slices;
		// Generate vertices coordinates, normal values, and texture coordinates
		numVertices = (slices+1)*(stacks-1)+2;
		vertices = new float[numVertices*3];
		normals = new float[numVertices*3];
		textures = new float[numVertices*2];

		//North tip point
		normals[0] = 0; normals[1] = 0; normals[2] = 1;
		vertices[0] = 0; vertices[1] = 0; vertices[2] = height;
		textures[0]= 0.5f; textures[1] = 1.0f;

		k = 1;
		//vertices on the main body
		for(j=0;j<=slices;j++){
			normals[3*k] = cos(deltaLong*j);
			normals[3*k+1] = sin(deltaLong*j);
			normals[3*k+2] = 0;
			vertices[3*k] = radius*normals[3*k];
			vertices[3*k+1] = radius*normals[3*k+1];
			vertices[3*k+2] = radius*normals[3*k+2];
			textures[2*k] = (float) j/slices;
			textures[2*k+1] = 1-(float) j/stacks;
			k++;
		}

		//Centre of base point
		normals[3*k] = 0; normals[3*k+1] = 0; normals[3*k+2] = 0;
		vertices[3*k] = 0; vertices[3*k+1] = 0; vertices[3*k+2] = 0;
		textures[2*k] = 0.5f; textures[2*k+1] = 0.0f;


		// Generate indices for triangular mesh
		numIndices = (stacks-1)*slices*6;
		indices = new int[numIndices];

		k = 0;
		//North Pole, numElement:slices*3
		for(j=1;j<=slices;j++){
			indices[k++] = 0;
			indices[k++] = j;
			indices[k++] = j+1;
		}

		//Centre of Base point vertex, numElement:slices*3
		int centreindex = numVertices-1;
		for(j=1;j<=slices;j++){
			indices[k++] = centreindex;
			indices[k++] = j;
			indices[k++] = j+1;
		}
	}

	public void setRadius(float radius){
		this.radius = radius;
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

	//keep consistent functions
	public void setHeight(int height){
		this.height = height;
		updated = false;
	}

	public float getRadius(){
		return radius;
	}

	public int getSlices(){
		return slices;
	}

	public int getStacks(){
		return stacks;
	}

	//keep consistent functions conventions
	public int getHeight(){
		return height;
	}


}