package com.ixume.karta.colormapping;

import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColorTree {
    public final ColorNode rootNode;
    private static final int MAX = 255;
    public int leaves;

    public ColorTree(List<ColorPoint> points) {
        //construct color tree
        leaves = 0;
        rootNode = constructTree(new ArrayList<>(points), new Cuboid(new Vector3i(0, 0, 0), new Vector3i(MAX, MAX, MAX)), 0);
    }

    private ColorNode constructTree(List<ColorPoint> points, Cuboid cuboid, int depth) {
        int axis = depth % 3; //0 = comparing X values, 1 = comparing Y values, 2 = comparing Z values
        depth++;

        //check if is a leaf node
        if (points.size() == 1) {
            ColorPoint point = points.get(0);
            int plane;
            if (axis == 0) {
                plane = point.getVector().x;
            } else if (axis == 1) {
                plane = point.getVector().y;
            } else {
                plane = point.getVector().z;
            }

            leaves++;
            return new ColorNode(point, cuboid, plane, axis, null, null);
        }

        //find median plane
        List<ColorPoint> sortedPoints;
        int medianPlane;
        if (axis == 0) {
            sortedPoints = points.stream().sorted(Comparator.comparingInt(k -> k.getVector().x)).toList();
            medianPlane = (sortedPoints.size() % 2) == 0 ?
                    (int) Math.ceil((sortedPoints.get(sortedPoints.size() / 2).getVector().x + sortedPoints.get(sortedPoints.size() / 2 - 1).getVector().x) / 2f)
                    : sortedPoints.get((int) (sortedPoints.size() / 2f - 0.5f)).getVector().x;
        } else if (axis == 1) {
            sortedPoints = points.stream().sorted(Comparator.comparingInt(k -> k.getVector().y)).toList();
            medianPlane = (sortedPoints.size() % 2) == 0 ?
                    (int) Math.ceil((sortedPoints.get(sortedPoints.size() / 2).getVector().y + sortedPoints.get(sortedPoints.size() / 2 - 1).getVector().y) / 2f)
                    : sortedPoints.get((int) (sortedPoints.size() / 2f - 0.5f)).getVector().y;
        } else {
            sortedPoints = points.stream().sorted(Comparator.comparingInt(k -> k.getVector().z)).toList();
            medianPlane = (sortedPoints.size() % 2) == 0 ?
                    (int) Math.ceil((sortedPoints.get(sortedPoints.size() / 2).getVector().z + sortedPoints.get(sortedPoints.size() / 2 - 1).getVector().z) / 2f)
                    : sortedPoints.get((int) (sortedPoints.size() / 2f - 0.5f)).getVector().z;
        }

        List<ColorPoint> leftPoints = sortedPoints.subList(0, (int) Math.ceil(sortedPoints.size() / 2f));
        List<ColorPoint> rightPoints = new ArrayList<>(points);
        rightPoints.removeAll(leftPoints);

        if (axis == 0) {
            Cuboid leftCuboid = new Cuboid(cuboid.getMin(), new Vector3i(medianPlane, cuboid.getMax().y, cuboid.getMax().z));
            Cuboid rightCuboid = new Cuboid(new Vector3i(medianPlane, cuboid.getMin().y, cuboid.getMin().z), cuboid.getMax());
            return new ColorNode(null, cuboid, medianPlane, axis, constructTree(leftPoints, leftCuboid, depth), constructTree(rightPoints, rightCuboid, depth));
        } else if (axis == 1) {
            Cuboid leftCuboid = new Cuboid(cuboid.getMin(), new Vector3i(cuboid.getMax().x, medianPlane, cuboid.getMax().z));
            Cuboid rightCuboid = new Cuboid(new Vector3i(cuboid.getMin().x, medianPlane, cuboid.getMin().z), cuboid.getMax());
            return new ColorNode(null, cuboid, medianPlane, axis, constructTree(leftPoints, leftCuboid, depth), constructTree(rightPoints, rightCuboid, depth));
        } else {
            Cuboid leftCuboid = new Cuboid(cuboid.getMin(), new Vector3i(cuboid.getMax().x, cuboid.getMax().y, medianPlane));
            Cuboid rightCuboid = new Cuboid(new Vector3i(cuboid.getMin().x, cuboid.getMin().y, medianPlane), cuboid.getMax());
            return new ColorNode(null, cuboid, medianPlane, axis, constructTree(leftPoints, leftCuboid, depth), constructTree(rightPoints, rightCuboid, depth));
        }
    }

    public PointDistancePair getClosest(ColorNode currentNode, Vector3i target, boolean perceptual) {
        if (currentNode.getLeft() != null && currentNode.getRight() != null) {
            //i am not a leaf
            if (isLeft(target, currentNode.getPlane(), currentNode.getAxis())) {
                //find the best point on the left
                //check if right could have closer point
                //if it could, then check that, if it can't then current point is best
                PointDistancePair leftBest = getClosest(currentNode.getLeft(), target, perceptual);

                if (currentNode.getRight().getBounds().distanceSquared(target) < leftBest.distance) {
                    //right could have best
                    PointDistancePair rightBest = getClosest(currentNode.getRight(), target, perceptual);
                    //return best
                    return rightBest.distance < leftBest.distance ? rightBest : leftBest;
                }

                return leftBest;
            } else {
                PointDistancePair rightBest = getClosest(currentNode.getRight(), target, perceptual);

                if (currentNode.getLeft().getBounds().distanceSquared(target) < rightBest.distance) {
                    //left could have best
                    PointDistancePair leftBest = getClosest(currentNode.getLeft(), target, perceptual);
                    //return best
                    return rightBest.distance < leftBest.distance ? rightBest : leftBest;
                }

                return rightBest;
            }
        } else {
            //i am a leaf
            //perceptual distance is NOT optimized for, and is significantly slower since the cuboid distance check is not accurate when it is used
            return new PointDistancePair(currentNode.getPoint(), perceptual ? colorDistance(currentNode.getPoint().getVector(), target) : currentNode.getPoint().getVector().distanceSquared(target));
        }
    }

    private boolean isLeft(Vector3i target, int plane, int axis) {
        if (axis == 0) {
            return target.x < plane;
        } else if (axis == 1) {
            return target.y < plane;
        } else {
            return target.z < plane;
        }
    }

    private static double colorDistance(Vector3i c1, Vector3i c2)
    {
        double rmean = (c1.x + c2.x) / 2.0;
        double r = c1.x - c2.x;
        double g = c1.y - c2.y;
        int b = c1.z - c2.z;
        double weightR = 2 + rmean / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256.0;
        return weightR * r * r + weightG * g * g + weightB * b * b;
//        int red1 = c1.x;
//        int red2 = c2.x;
//        int rmean = (red1 + red2) >> 1;
//        int r = red1 - red2;
//        int g = c1.y - c2.y;
//        int b = c1.z - c2.z;
//        return (((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8);
    }
}
