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

uniform int u_LightCount;
uniform LightInfo u_Light[8];
uniform MaterialInfo u_Material;
uniform vec4 u_Color;

varying vec3 v_Position;
varying vec3 v_Normal;

void DirectionalLight(LightInfo Light, out vec3 ambAndDiff, out vec3 spec) {
  vec3 n = normalize(v_Normal);
  vec3 s = normalize(Light.Direction);
  vec3 v = normalize(vec3(-v_Position));
  vec3 r = reflect( -s, n );
  ambAndDiff = Light.Intensity * ( u_Material.Ka + u_Material.Kd * max( dot(s, n), 0.0 ));
  spec = Light.Intensity * (u_Material.Ks * pow( max( dot(r,v), 0.0 ), u_Material.Shininess));
}

void PointLight(LightInfo Light, out vec3 ambAndDiff, out vec3 spec) {
  vec3 n = normalize(v_Normal);
  vec3 s = vec3(Light.Position) - v_Position;
  float distance = length(s);
  s = normalize(s);
  vec3 v = normalize(vec3(-v_Position));
  vec3 h = normalize( v + s );
  ambAndDiff = (Light.Intensity) * ( u_Material.Ka + ((u_Material.Kd/distance) * max( dot(s, n), 0.0 )));
  spec = (Light.Intensity/distance) * (u_Material.Ks * pow( max( dot(h,n), 0.0 ), u_Material.Shininess));
}

void SpotLight(LightInfo Light, out vec3 ambAndDiff, out vec3 spec) {
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
    ambAndDiff = ambient + (spotFactor * (Light.Intensity/distance) * (u_Material.Kd * max( dot(s, n), 0.0 )));
    spec = spotFactor * (Light.Intensity/distance) * (u_Material.Ks * pow( max( dot(h,n), 0.0 ), u_Material.Shininess));
  } else {
    ambAndDiff = ambient;
    spec = vec3(0.0, 0.0, 0.0);
  }
}

void phongModel( out vec3 ambAndDiff, out vec3 spec ) {
// Compute the ADS shading model here, return
//ambient and diffuse color in ambAndDiff, and return specular
// color in spec
  vec3 ambAndDiffOfOneLight, specOfOneLight;
  ambAndDiff = vec3(0.0, 0.0, 0.0);
  spec = vec3(0.0, 0.0, 0.0);

  for(int i = 0; i < u_LightCount; i++) {
    if (u_Light[i].Type == 0) {
       DirectionalLight(u_Light[i], ambAndDiffOfOneLight, specOfOneLight);
       ambAndDiff += ambAndDiffOfOneLight;
       spec += specOfOneLight;
    }

    if (u_Light[i].Type == 1) {
      PointLight(u_Light[i], ambAndDiffOfOneLight, specOfOneLight);
      ambAndDiff += ambAndDiffOfOneLight;
      spec += specOfOneLight;
    }

    if (u_Light[i].Type == 2) {
      SpotLight(u_Light[i], ambAndDiffOfOneLight, specOfOneLight);
      ambAndDiff += ambAndDiffOfOneLight;
      spec += specOfOneLight;
    }
  }
}

void main() {
  vec3 ambAndDiff, spec;
  phongModel(ambAndDiff, spec);
  gl_FragColor = vec4(ambAndDiff, 1.0) * u_Color + vec4(spec, 1.0);
}