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

uniform mat4 u_MVMatrix;
uniform mat4 u_IT_MVMatrix;
uniform mat4 u_MVPMatrix;
uniform mat4 u_ShadowMatrix;
uniform bool u_Skip_Color;
uniform bool u_Shadow_Enable;
uniform int u_LightCount;
uniform LightInfo u_Light[8];
uniform MaterialInfo u_Material;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TextureCoordinates;

varying vec3 v_ambOfLight;
varying vec3 v_diffOfLight;
varying vec3 v_specOfLight;
varying vec2 v_TextureCoordinates;
varying vec4 v_ShadowCoordinates;
varying vec4 v_ShadowPosition;

void DirectionalLight(vec3 v_Position, vec3 v_Normal, LightInfo Light, out vec3 amb, out vec3 diff, out vec3 spec) {
  vec3 n = normalize(v_Normal);
  vec3 s = normalize(Light.Direction);
  vec3 v = normalize(vec3(-v_Position));
  vec3 r = reflect( -s, n );
  amb = Light.Intensity * ( u_Material.Ka);
  diff = Light.Intensity * (u_Material.Kd * max( dot(s, n), 0.0 ));
  spec = Light.Intensity * (u_Material.Ks * pow( max( dot(r,v), 0.0 ), u_Material.Shininess));
}

void PointLight(vec3 v_Position, vec3 v_Normal, LightInfo Light, out vec3 amb, out vec3 diff, out vec3 spec) {
  vec3 n = normalize(v_Normal);
  vec3 s = vec3(Light.Position) - v_Position;
  float distance = length(s);
  s = normalize(s);
  vec3 v = normalize(vec3(-v_Position));
  vec3 h = normalize( v + s );
  amb = (Light.Intensity) * (u_Material.Ka);
  diff = (Light.Intensity) * (((u_Material.Kd/distance) * max( dot(s, n), 0.0 )));
  spec = (Light.Intensity/distance) * (u_Material.Ks * pow( max( dot(h,n), 0.0 ), u_Material.Shininess));
}

void SpotLight(vec3 v_Position, vec3 v_Normal, LightInfo Light, out vec3 amb, out vec3 diff, out vec3 spec) {
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

void phongModel(vec3 Position, vec3 Normal ) {
// Compute the ADS shading model here, return
//ambient and diffuse color in amb and diff respectively, and return specular
// color in spec
  vec3 ambOfOneLight, diffOfOneLight, specOfOneLight;
  v_ambOfLight = vec3(0.0, 0.0, 0.0);
  v_diffOfLight = vec3(0.0, 0.0, 0.0);
  v_specOfLight = vec3(0.0, 0.0, 0.0);

  for(int i = 0; i < u_LightCount; i++) {
    if (u_Light[i].Type == 0) {
       DirectionalLight(Position, Normal, u_Light[i], ambOfOneLight, diffOfOneLight, specOfOneLight);
       v_ambOfLight += ambOfOneLight;
       v_diffOfLight += diffOfOneLight;
       v_specOfLight += specOfOneLight;
    }

    if (u_Light[i].Type == 1) {
      PointLight(Position, Normal, u_Light[i], ambOfOneLight, diffOfOneLight, specOfOneLight);
      v_ambOfLight += ambOfOneLight;
      v_diffOfLight += diffOfOneLight;
      v_specOfLight += specOfOneLight;
    }

    if (u_Light[i].Type == 2) {
      SpotLight(Position, Normal, u_Light[i], ambOfOneLight, diffOfOneLight, specOfOneLight);
      v_ambOfLight += ambOfOneLight;
      v_diffOfLight += diffOfOneLight;
      v_specOfLight += specOfOneLight;
    }
  }
}

void main() {
v_TextureCoordinates = a_TextureCoordinates;

if (u_Shadow_Enable) {
  v_ShadowCoordinates = u_ShadowMatrix * a_Position;
}

if (!u_Skip_Color) {
  vec3 Position = vec3(u_MVMatrix * a_Position);
  vec3 Normal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));
  phongModel(Position, Normal);
}

v_ShadowPosition = u_MVPMatrix * a_Position;
gl_Position = u_MVPMatrix * a_Position;
}