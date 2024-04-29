package com.ixume.karta.render;

import org.joml.Vector2i;
import org.joml.Vector3d;

public class RenderUtils3D {
    public static Vector2i worldToScreenPos(Vector3d worldPos, int resolution) {
        //camera at 0 0 0
        //faces negative Z
        //+x on right
        //width and height is dimension of frustum 1 unit away from camera
        //resolution is pixels per unit of frustum
        double x = worldPos.x;
        double y = worldPos.y;
        double z = worldPos.z;
        return new Vector2i((int) (x/z * resolution), (int) (y/z * resolution));
    }
}
