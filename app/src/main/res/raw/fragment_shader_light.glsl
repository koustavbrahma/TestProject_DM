precision mediump float;

struct LightInfo {
  int Type;
  vec4 Position; // Light position in eye coords.
  vec3 Direction;
  vec3 Intensity; // A,D,S intensity
  float Exponent; // Angular attenuation exponent
  float Cutoff; // Cutoff angle (between 0 and 90)
};

struct MaterialInfo {
  vec3 Ka; // Ambient reflectivity
  vec3 Kd; // Diffuse reflectivity
  vec3 Ks; // Specular reflectivity
  float Shininess; // Specular shininess factor
};

uniform bool u_Skip_Color;
uniform bool u_Shadow_Enable;
uniform int u_LightCount;
uniform LightInfo u_Light[8];
uniform MaterialInfo u_Material;
uniform vec4 u_Color;
uniform sampler2D u_ShadowMap;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec4 v_ShadowCoordinates;
varying vec4 v_ShadowPosition;

void DirectionalLight(LightInfo Light, out vec3 amb, out vec3 diff, out vec3 spec) {
  vec3 n = normalize(v_Normal);
  vec3 s = normalize(Light.Direction);
  vec3 v = normalize(vec3(-v_Position));
  vec3 r = reflect( -s, n );
  amb = Light.Intensity * ( u_Material.Ka);
  diff = Light.Intensity * (u_Material.Kd * max( dot(s, n), 0.0 ));
  spec = Light.Intensity * (u_Material.Ks * pow( max( dot(r,v), 0.0 ), u_Material.Shininess));
}

void PointLight(LightInfo Light, out vec3 amb, out vec3 diff, out vec3 spec) {
  vec3 n = normalize(v_Normal);
  vec3 s = vec3(Light.Position) - v_Position;
  float distance = length(s);
  s = normalize(s);
  vec3 v = normalize(vec3(-v_Position));
  vec3 h = normalize( v + s );
  amb = (Light.Intensity) * ( u_Material.Ka );
  diff = (Light.Intensity) * (((u_Material.Kd/distance) * max( dot(s, n), 0.0 )));
  spec = (Light.Intensity/distance) * (u_Material.Ks * pow( max( dot(h,n), 0.0 ), u_Material.Shininess));
}

void SpotLight(LightInfo Light, out vec3 amb, out vec3 diff, out vec3 spec) {
  vec3 n = normalize(v_Normal);
  vec3 s = vec3(Light.Position) - v_Position;
  float distance = length(s);
  s = normalize(s);
  vec3 d = normalize(Light.Direction);
  float angle = acos( dot(-s, d) );
  float cutoff = radians( clamp( Light.Cutoff, 0.0, 90.0 ) );
  vec3 ambient = (Light.Intensity) * u_Material.Ka;
  if( angle < cutoff ) {
    float spotFactor = pow(dot(-s, d), Light.Exponent);
    vec3 v = normalize(vec3(-v_Position));
    vec3 h = normalize( v + s );
    amb = ambient;
    diff = (spotFactor * (Light.Intensity/distance) * (u_Material.Kd * max( dot(s, n), 0.0 )));
    spec = spotFactor * (Light.Intensity/distance) * (u_Material.Ks * pow( max( dot(h,n), 0.0 ), u_Material.Shininess));
  } else {
    amb = ambient;
    spec = vec3(0.0, 0.0, 0.0);
  }
}

void phongModel( out vec3 amb, out vec3 diff, out vec3 spec ) {
// Compute the ADS shading model here, return
//ambient and diffuse color in ambAndDiff, and return specular
// color in spec
  vec3 ambOfOneLight, diffOfOneLight, specOfOneLight;
  amb = vec3(0.0, 0.0, 0.0);
  diff = vec3(0.0, 0.0, 0.0);
  spec = vec3(0.0, 0.0, 0.0);

  for(int i = 0; i < u_LightCount; i++) {
    if (u_Light[i].Type == 0) {
       DirectionalLight(u_Light[i], ambOfOneLight, diffOfOneLight, specOfOneLight);
       amb += ambOfOneLight;
       diff += diffOfOneLight;
       spec += specOfOneLight;
    }

    if (u_Light[i].Type == 1) {
      PointLight(u_Light[i], ambOfOneLight, diffOfOneLight, specOfOneLight);
      amb += ambOfOneLight;
      diff += diffOfOneLight;
      spec += specOfOneLight;
    }

    if (u_Light[i].Type == 2) {
      SpotLight(u_Light[i], ambOfOneLight, diffOfOneLight, specOfOneLight);
      amb += ambOfOneLight;
      diff += diffOfOneLight;
      spec += specOfOneLight;
    }
  }
}

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

  vec3 amb, diff, spec;
  phongModel(amb, diff, spec);
  float visibility = unpack();

  gl_FragColor = vec4(amb, 1.0) * u_Color + vec4(visibility * diff, 1.0) * u_Color + vec4(visibility * spec, 1.0);
}