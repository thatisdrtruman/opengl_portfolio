#version 330 core

in  vec4 color;
in  vec2 texCoord;
out vec4 fColor;

uniform sampler2D tex;

void main()
{
	 fColor = color*texture( tex, texCoord );
	vec4 t = texture( tex, texCoord );
	//the fcolor that casts the Welsh Flag in the
	//right colour, while allowing the shadowing light to appear
	fColor = 0.1*color + 0.9* t;
	//fColor = color*(1-t) + vec4(0.8,0.5,0.3,1)* t;
}