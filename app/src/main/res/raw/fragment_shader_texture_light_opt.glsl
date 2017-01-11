precision mediump float;

uniform bool u_Skip_Color;
uniform bool u_Shadow_Enable;
uniform sampler2D u_TextureUnit;
uniform sampler2D u_ShadowMap;

varying vec3 v_ambOfLight;
varying vec3 v_diffOfLight;
varying vec3 v_specOfLight;
varying vec2 v_TextureCoordinates;
varying vec4 v_ShadowCoordinates;
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

// unpack colour to depth value and calculate the visibility
float unpack()
{
  float visibility = 1.0;
  if (!u_Shadow_Enable) {
     return visibility;
  }

  if (v_ShadowCoordinates.w > 0.0) {
    vec4 ShadowCoordinateWdiv =  (v_ShadowCoordinates / v_ShadowCoordinates.w);
    ShadowCoordinateWdiv = (ShadowCoordinateWdiv + 1.0)/ 2.0;
    vec4 rgba_depth = texture2D(u_ShadowMap, ShadowCoordinateWdiv.st);
    const vec4 bit_shift = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
    float distanceFromLight = dot(rgba_depth, bit_shift);
    if (distanceFromLight < (ShadowCoordinateWdiv.z - 0.004)) {
      visibility = 0.0;
    }
  }

  return visibility;
}

void main() {
  if (u_Skip_Color) {
    // pack depth value into 32-bit RGBA texture
    gl_FragColor = pack();
    return;
  }
  vec4 textColor = texture2D(u_TextureUnit, v_TextureCoordinates);
  float visibility = unpack();

  gl_FragColor = vec4(v_ambOfLight, 1.0) * textColor+ vec4(visibility * v_diffOfLight, 1.0) * textColor + vec4(visibility * v_specOfLight, 1.0);
}