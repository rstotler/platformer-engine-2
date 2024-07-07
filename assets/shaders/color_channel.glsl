#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

uniform float target_r;
uniform float target_g;
uniform float target_b;
uniform float target_alpha;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    vec3 colorVector = vec3(color.r * target_r, color.g * target_g, color.b * target_b);

    float alpha = color.a;
    if(alpha > 0.0f) {
        alpha = target_alpha;
    }

    gl_FragColor = vec4(colorVector.rgb, alpha);
}
