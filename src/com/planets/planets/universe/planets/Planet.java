package com.planets.planets.universe.planets;

import com.planets.engine.graphics.Mesh;
import com.planets.engine.graphics.Vertex;
import com.planets.engine.math.Triangle;
import com.planets.engine.math.Vector3f;
import com.planets.engine.math.Vector4f;
import com.planets.engine.math.color.ColorFader3C;
import com.planets.engine.math.noise.ImprovedNoise;
import com.planets.engine.math.noise.SimplexNoise;
import com.planets.engine.objects.RenderObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Planet extends RenderObject {

    // generation variables
    private static int depth; // the amount of times to recursively subdivide faces
    private static final float phi = 1.618f; // the golden ratio used for approximating a tetrahedron

    /**
     * default constructor
     * @param mesh - the mesh that this object is made of
     * @param position - the position of the planet
     * @param rotation - the rotation of the planet
     * @param scale - the scale of the planet
     */
    public Planet(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(mesh, position, rotation, scale);
    }

    /**
     * gets an instance of the planet
     * @param position - the position of the planet
     * @return - a new procedurally generated planet at that location
     */
    public static Planet getInstance(Vector3f position) {
        return new Planet(generateMesh(), position, new Vector3f(0), new Vector3f(1));
    }

    public static Planet getInstance(float x, float y, float z) {
        return new Planet(generateMesh(), new Vector3f(x, y, z), new Vector3f(0), new Vector3f(1));
    }

    public static Planet getInstance(float xyz) {
        return new Planet(generateMesh(), new Vector3f(xyz), new Vector3f(0), new Vector3f(1));
    }

    /**
     * rotates the planet by some amount
     * @param dx - the change in x rotation
     * @param dy - the change in y position
     * @param dz - the change in z position
     */
    public void rotate(float dx, float dy, float dz) {
        this.setRotation(this.getRotation().add(dx, dy, dz));
    }

    private static Mesh generateMesh() {

        boolean blend = true;

        Random random = new Random(60902);
        Color c11 = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        Color c22 = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        Color c33 = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        depth = 4; // how much to recursively subdivide faces
        float spareDistance = 3.5f * random.nextFloat(); // increase -> more spikes
        float spareOffset = 2.0f * random.nextFloat(); // change -> different generation
        float amplitude = 3.5f * random.nextFloat(); // increase -> larger peaks
        float radius = 2.0f; // the default radius of the planet (water level)

        // generate the triangles
        ArrayList<Triangle> triangles = generateTriangles(radius);

        ColorFader3C cf = new ColorFader3C(c11, c22, c33, 0.5f);

        int seed = 10;
        SimplexNoise simplexNoise = new SimplexNoise(11f, 0.6f, seed);

        float maxHeight = 0.0f;
        for (Vector3f v : previousVertices) {

            float newRadius = (float) (
                    radius + amplitude * Math.max(ImprovedNoise.noise(
                            v.getX() * spareDistance + spareOffset,
                            v.getY() * spareDistance + spareOffset,
                            v.getZ() * spareDistance + spareOffset
                    ), 0));

            v.normalize(newRadius);

            if (Vector3f.length(v) > maxHeight) {
                maxHeight = Vector3f.length(v);
            }
        }

        // create the vertex array
        Vertex[] vertices = new Vertex[triangles.size() * 3];
        for (int i = 0; i < triangles.size(); i++) {
            Triangle t = triangles.get(i);

            // calculate the color based on the magnitude of the vertex
            float ps2, ps3;
            Vector3f _c2, _c3;
            Vector4f c2 = new Vector4f(0), c3 = new Vector4f(0);
            if (blend) {
                ps2 = (Vector3f.length(t.getV2()) - radius) / (maxHeight - radius);
                ps3 = (Vector3f.length(t.getV3()) - radius) / (maxHeight - radius);
                _c2 = cf.getColor(ps2);
                _c3 = cf.getColor(ps3);
                c2 = new Vector4f(_c2, 1.0f);
                c3 = new Vector4f(_c3, 1.0f);
            }
            float ps1 = (Vector3f.length(t.getV1()) - radius) / (maxHeight - radius);
            Vector3f _c1 = cf.getColor(ps1);
            Vector4f c1 = new Vector4f(_c1, 1.0f);

            // compute normal vectors
            Vector3f n1 = Vector3f.normalize(Vector3f.cross(Vector3f.subtract(t.getV2(), t.getV1()), Vector3f.subtract(t.getV3(), t.getV1())));

            Vector3f v1 = new Vector3f(t.getV1());
            Vector3f v2 = new Vector3f(t.getV2());
            Vector3f v3 = new Vector3f(t.getV3());

            // create the vertices
            if (blend) {
                vertices[3 * i]     = new Vertex(v1, c1, n1);
                vertices[3 * i + 1] = new Vertex(v2, c2, n1);
                vertices[3 * i + 2] = new Vertex(v3, c3, n1);
            } else {
                vertices[3 * i]     = new Vertex(v1, c1, n1);
                vertices[3 * i + 1] = new Vertex(v2, c1, n1);
                vertices[3 * i + 2] = new Vertex(v3, c1, n1);
            }
        }

        // generate draw order indices
        int[] indices = new int[triangles.size() * 3];
        for (int i = 0; i < triangles.size() * 3; i++) {
            indices[i] = i;
        }

        // make a new mesh
        return new Mesh(vertices, indices);
    }

    /**
     * generates vertices of this sphere
     * @param radius - the radius of the sphere
     */
    public static ArrayList<Triangle> generateTriangles(float radius) {
        previousVertices = new ArrayList<>();

        ArrayList<Triangle> faces = new ArrayList<>();

        // define a regular icosahedron using 12 vertices
        Vector3f[] vertices = new Vector3f[12];
        vertices[0]  = new Vector3f( 0.5f * radius, 0,  phi/2 * radius);
        vertices[1]  = new Vector3f( 0.5f * radius, 0, -phi/2 * radius);
        vertices[2]  = new Vector3f(-0.5f * radius, 0,  phi/2 * radius);
        vertices[3]  = new Vector3f(-0.5f * radius, 0, -phi/2 * radius);
        vertices[4]  = new Vector3f( phi/2 * radius,  0.5f * radius, 0);
        vertices[5]  = new Vector3f( phi/2 * radius, -0.5f * radius, 0);
        vertices[6]  = new Vector3f(-phi/2 * radius,  0.5f * radius, 0);
        vertices[7]  = new Vector3f(-phi/2 * radius, -0.5f * radius, 0);
        vertices[8]  = new Vector3f(0,  phi/2 * radius, 0.5f * radius);
        vertices[9]  = new Vector3f(0,  phi/2 * radius,-0.5f * radius);
        vertices[10] = new Vector3f(0, -phi/2 * radius, 0.5f * radius);
        vertices[11] = new Vector3f(0, -phi/2 * radius,-0.5f * radius);

        // subdivide each triangular face (20 total) recursively
        faces.addAll(subdivide(vertices[0],  vertices[2],  vertices[10], depth, radius));
        faces.addAll(subdivide(vertices[0],  vertices[10], vertices[5],  depth, radius));
        faces.addAll(subdivide(vertices[0],  vertices[5],  vertices[4],  depth, radius));
        faces.addAll(subdivide(vertices[0],  vertices[4],  vertices[8],  depth, radius));
        faces.addAll(subdivide(vertices[0],  vertices[8],  vertices[2],  depth, radius));
        faces.addAll(subdivide(vertices[3],  vertices[1],  vertices[11], depth, radius));
        faces.addAll(subdivide(vertices[3],  vertices[11], vertices[7],  depth, radius));
        faces.addAll(subdivide(vertices[3],  vertices[7],  vertices[6],  depth, radius));
        faces.addAll(subdivide(vertices[3],  vertices[6],  vertices[9],  depth, radius));
        faces.addAll(subdivide(vertices[3],  vertices[9],  vertices[1],  depth, radius));
        faces.addAll(subdivide(vertices[2],  vertices[6],  vertices[7],  depth, radius));
        faces.addAll(subdivide(vertices[2],  vertices[7],  vertices[10], depth, radius));
        faces.addAll(subdivide(vertices[10], vertices[7],  vertices[11], depth, radius));
        faces.addAll(subdivide(vertices[10], vertices[11], vertices[5],  depth, radius));
        faces.addAll(subdivide(vertices[5],  vertices[11], vertices[1],  depth, radius));
        faces.addAll(subdivide(vertices[5],  vertices[1],  vertices[4],  depth, radius));
        faces.addAll(subdivide(vertices[4],  vertices[1],  vertices[9],  depth, radius));
        faces.addAll(subdivide(vertices[4],  vertices[9],  vertices[8],  depth, radius));
        faces.addAll(subdivide(vertices[8],  vertices[9],  vertices[6],  depth, radius));
        faces.addAll(subdivide(vertices[8],  vertices[6],  vertices[2],  depth, radius));

        return faces;
    }

    private static ArrayList<Vector3f> previousVertices;

    /**
     * recursively subdivides a triangle into 4 triangles, and then normalizes each new vertex to a radius of 1
     * @param v1 - the first vertex of the triangle
     * @param v2 - the second vertex of the triangle
     * @param v3 - the third vertex of the triangle
     * @param depth - the current depth of recursion
     */
    private static ArrayList<Triangle> subdivide(Vector3f v1, Vector3f v2, Vector3f v3, long depth, float radius) {

        ArrayList<Triangle> faces = new ArrayList<>();

        // default condition
        if (depth == 0) {

            Vector3f v1p = Vector3f.normalize(v1, radius);
            Vector3f v2p = Vector3f.normalize(v2, radius);
            Vector3f v3p = Vector3f.normalize(v3, radius);

            if (previousVertices.contains(v1p)) {
                v1p = previousVertices.get(previousVertices.indexOf(v1p));
            } else {
                previousVertices.add(v1p);
            }

            if (previousVertices.contains(v2p)) {
                v2p = previousVertices.get(previousVertices.indexOf(v2p));
            } else {
                previousVertices.add(v2p);
            }

            if (previousVertices.contains(v3p)) {
                v3p = previousVertices.get(previousVertices.indexOf(v3p));
            } else {
                previousVertices.add(v3p);
            }

            faces.add(new Triangle(v1p, v2p, v3p));
            return faces;
        }


        // create new vertices for each face
        Vector3f v12 = Vector3f.normalize(new Vector3f(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ()), radius);
        Vector3f v23 = Vector3f.normalize(new Vector3f(v2.getX() + v3.getX(), v2.getY() + v3.getY(), v2.getZ() + v3.getZ()), radius);
        Vector3f v31 = Vector3f.normalize(new Vector3f(v3.getX() + v1.getX(), v3.getY() + v1.getY(), v3.getZ() + v1.getZ()), radius);

        // recursive part
        faces.addAll(subdivide(v1, v12, v31, depth - 1, radius));
        faces.addAll(subdivide(v2, v23, v12, depth - 1, radius));
        faces.addAll(subdivide(v3, v31, v23, depth - 1, radius));
        faces.addAll(subdivide(v12, v23, v31,depth - 1, radius));

        return faces;
    }
}