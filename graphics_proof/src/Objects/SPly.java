package Objects;
public class SPly extends SObject{
	private int numVerticesinit;
	private int numFaces;
	private float[] verticesList;
	private int[] indicesList;

	public SPly(){
		super();
		init();
		update();
	}

	public SPly(int numVerticesinit, int numFaces){
		super();
		init();
		this.numVerticesinit = numVerticesinit;
		this.numFaces = numFaces;
		update();
	}

	public SPly(int numVerticesinit, int numFaces, float[] verticesList, int[] indicesList){
		super();
		this.numVerticesinit = numVerticesinit;
		this.numFaces = numFaces;
		this.verticesList = verticesList;
		this.indicesList = indicesList;
		update();
	}

	private void init(){
		this.numVerticesinit = 202;
		this.numFaces = 400;
	}

	@Override
	protected void genData() {
		int i,j,k;
		// Generate vertices coordinates, normal values, and texture coordinates
		//set NumVertices to be the array of the vertexCoord parameter.
		numVertices = numVerticesinit;
		//set NumIndices to be the array of the vertexCoord parameter.

		numIndices = numFaces;
		vertices = new float[numVertices*3];
		normals = new float[numVertices*3];
		textures = new float[numVertices*2];
		indices = new int[numIndices*3];

		k = 0;
		for (i = 0; i < numVertices; i++){
			normals[3*k] = verticesList[3*k];
			normals[3*k+1] = verticesList[3*k+1];
			normals[3*k+2] = verticesList[3*k+2];
			vertices[3*k] = verticesList[3*k];
			vertices[3*k+1] = verticesList[3*k+1];
			vertices[3*k+2] = verticesList[3*k+2];
			textures[2*k] =0.5f;
			textures[2*k+1] = 1.0f;
			System.out.println(vertices[3*k]);
			System.out.println(vertices[3*k+1]);
			System.out.println(vertices[3*k+2]);
			k++;
		}
		k = 0;
		for (i = 0; i < numFaces; i++){
			indices[3*k] = indicesList[3*k];
			indices[3*k+1] = indicesList[3*k+1];
			indices[3*k+2] = indicesList[3*k+2];
			System.out.println(indices[3*k]);
			System.out.println(indices[3*k+1]);
			System.out.println(indices[3*k+2]);
			k++;
		}

		//This file is essentially a coordinates duplicator so that it is compatiable with
		//the SObject class.

	}

	public void setNumVertices(int numVerticesinit){
		this.numVerticesinit = numVerticesinit;
		updated = false;
	}


	public void setNumFaces(int numFaces){
		this.numFaces = numFaces;
		updated = false;
	}

	public void setVerticesList(float[] verticesList){
		this.verticesList = verticesList;
		updated = false;
	}

	public void setIndicesList(int[] indicesList){
		this.indicesList = indicesList;
		updated = false;
	}
	public int getnumVerticesinit(){
		return numVerticesinit;
	}

	public int getNumFaces(){
		return numFaces;
	}

	public int[] getIndicesList(){
		return indicesList;
	}

	//keep consistent functions conventions
	public float[] getVerticesList(){
		return verticesList;
	}

}