uniform mat4 u_Matrix;
attribute vec4 a_Position;
varying vec4 v_ShadowPosition;

void main() {
v_ShadowPosition = u_MVPMatrix * a_Position;
gl_Position = u_Matrix * a_Position;
}