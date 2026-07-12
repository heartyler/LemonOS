package dev.lemonos;

final class BackendSandboxClearPreviewPlanService {
    ClearPreviewPlan build(BackendSandboxGeometryService.SelectionBounds bounds) {
        return new ClearPreviewPlan(bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());
    }

    record ClearPreviewPlan(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    }
}
