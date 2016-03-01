precision mediump float;
uniform sampler2D u_TextureUnit;
uniform bool u_Skip_Color;
varying vec2 v_TextureCoordinates;
varying vec4 v_ShadowPosition;

// from Fabien Sangalard's DEngine
vec4 pack ()
{
  // the depth
  float normalizedDistance  = v_ShadowPosition.z / v_ShadowPosition.w;
  // scale -1.0;1.0 to 0.0;1.0
  normalizedDistance = (normalizedDistance + 1.0) / 2.0;
  const vec4 bit_shift = vec4(256.0*256.0*256.0, 256.0*256.0, 256.0, 1.0);
  const vec4 bit_mask  = vec4(0.0, 1.0/256.0, 1.0/256.0, 1.0/256.0);
  vec4 res = fract(normalizedDistance * bit_shift);
  res -= res.xxyz * bit_mask;
  return res;
}

void main()
{
if (u_Skip_Color) {
  // pack depth value into 32-bit RGBA texture
  gl_FragColor = pack();
  return;
}
gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}