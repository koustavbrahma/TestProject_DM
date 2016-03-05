uniform mat4 u_MVMatrix;
uniform mat4 u_IT_MVMatrix;
uniform mat4 u_MVPMatrix;
uniform mat4 u_ShadowMatrix;
uniform bool u_Shadow_Enable;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec3 a_TextureCoordinates;

varying vec3 v_TextureCoordinates;
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec4 v_ShadowCoordinates;
varying vec4 v_ShadowPosition;

void main() {
v_TextureCoordinates = a_TextureCoordinates;
v_Position = vec3(u_MVMatrix * a_Position);
v_Normal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

if (u_Shadow_Enable) {
  v_ShadowCoordinates = u_ShadowMatrix * a_Position;
}
v_ShadowPosition = u_MVPMatrix * a_Position;
gl_Position = u_MVPMatrix * a_Position;
}