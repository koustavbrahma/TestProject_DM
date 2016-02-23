precision mediump float;

varying vec2 v_TextureCoordinates; // Texture coordinate

uniform sampler2D u_TextureUnit;
uniform int u_Width; // Width of the screen in pixels
uniform int u_Height; // Height of the screen in pixels
uniform int u_Weight_Count;
uniform float u_Weight[5];
uniform int u_Pass;

const float PixOffset[5] = float[5](0.0,1.0,2.0,3.0,4.0);

vec4 pass1()
{
  float dy = 1.0 / float(u_Height);
  vec4 sum = texture2D(u_TextureUnit, v_TextureCoordinates) * u_Weight[0];
  for( int i = 1; i < u_Weight_Count; i++ ) {
    sum += texture2D( u_TextureUnit, v_TextureCoordinates + vec2(0.0,PixOffset[i]) * dy ) * u_Weight[i];
    sum += texture2D( u_TextureUnit, v_TextureCoordinates - vec2(0.0,PixOffset[i]) * dy ) * u_Weight[i];
  }
  return sum;
}

vec4 pass2()
{
  float dx = 1.0 / float(u_Width);
  vec4 sum = texture2D(u_TextureUnit, v_TextureCoordinates) * u_Weight[0];
  for( int i = 1; i < u_Weight_Count; i++ ) {
    sum += texture2D( u_TextureUnit, v_TextureCoordinates + vec2(PixOffset[i],0.0) * dx ) * u_Weight[i];
    sum += texture2D( u_TextureUnit, v_TextureCoordinates - vec2(PixOffset[i],0.0) * dx ) * u_Weight[i];
  }

  return sum;
}

void main()
{
  // This will call either pass1() and pass2()
  gl_FragColor = (u_Pass == 1) ? pass1() : pass2();
}