package dev.lemonos;

import java.util.ArrayList;
import java.util.List;

final class BackendSandboxPreviewRenderService {
    List<Line> boxLines(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        double endX = (double)maxX + 1.0;
        double endY = (double)maxY + 1.0;
        double endZ = (double)maxZ + 1.0;
        ArrayList<Line> lines = new ArrayList<Line>();
        lines.add(new Line(minX, minY, minZ, endX, minY, minZ));
        lines.add(new Line(minX, minY, endZ, endX, minY, endZ));
        lines.add(new Line(minX, endY, minZ, endX, endY, minZ));
        lines.add(new Line(minX, endY, endZ, endX, endY, endZ));
        lines.add(new Line(minX, minY, minZ, minX, minY, endZ));
        lines.add(new Line(endX, minY, minZ, endX, minY, endZ));
        lines.add(new Line(minX, endY, minZ, minX, endY, endZ));
        lines.add(new Line(endX, endY, minZ, endX, endY, endZ));
        lines.add(new Line(minX, minY, minZ, minX, endY, minZ));
        lines.add(new Line(endX, minY, minZ, endX, endY, minZ));
        lines.add(new Line(minX, minY, endZ, minX, endY, endZ));
        lines.add(new Line(endX, minY, endZ, endX, endY, endZ));
        return lines;
    }

    int lineSteps(Line line) {
        double dx = line.endX() - line.startX();
        double dy = line.endY() - line.startY();
        double dz = line.endZ() - line.startZ();
        return Math.max(1, Math.min(128, (int)Math.ceil(Math.sqrt(dx * dx + dy * dy + dz * dz) * 2.0)));
    }

    Point pointAt(Line line, int step, int steps) {
        double progress = (double)step / (double)steps;
        return new Point(
                line.startX() + (line.endX() - line.startX()) * progress,
                line.startY() + (line.endY() - line.startY()) * progress,
                line.startZ() + (line.endZ() - line.startZ()) * progress);
    }

    record Line(double startX, double startY, double startZ, double endX, double endY, double endZ) {
    }

    record Point(double x, double y, double z) {
    }
}
