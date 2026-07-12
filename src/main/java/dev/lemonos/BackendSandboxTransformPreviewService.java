package dev.lemonos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.data.BlockData;

final class BackendSandboxTransformPreviewService {
    <P> TransformPlan<P> build(BackendSandboxGeometryService.SelectionBounds bounds, PositionFactory<P> positionFactory, BlockTransform<P> blockTransform) {
        HashMap<P, BlockData> blockDataByPosition = new HashMap<P, BlockData>();
        HashSet<P> affectedPositions = new HashSet<P>();
        for (int x = bounds.minX(); x <= bounds.maxX(); ++x) {
            for (int y = bounds.minY(); y <= bounds.maxY(); ++y) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); ++z) {
                    TransformedBlock<P> transformedBlock = blockTransform.apply(x, y, z);
                    P sourcePosition = positionFactory.create(x, y, z);
                    affectedPositions.add(sourcePosition);
                    affectedPositions.add(transformedBlock.position());
                    blockDataByPosition.put(transformedBlock.position(), transformedBlock.blockData());
                }
            }
        }
        return new TransformPlan<P>(affectedPositions, blockDataByPosition);
    }

    <P> boolean tooManyPositions(TransformPlan<P> plan, int maxBlocks) {
        return plan.affectedPositions().size() > maxBlocks;
    }

    <P> boolean allWithinVerticalRange(Iterable<P> positions, ToIntFunction<P> yValue, int minHeight, int maxHeight) {
        for (P position : positions) {
            int y = yValue.applyAsInt(position);
            if (y < minHeight || y >= maxHeight) {
                return false;
            }
        }
        return true;
    }

    <P> TransformSummary<P> summarizeChanges(Iterable<P> positions, Function<P, BlockData> oldData, Function<P, BlockData> newData, ToIntFunction<P> xValue, ToIntFunction<P> yValue, ToIntFunction<P> zValue, BiPredicate<BlockData, BlockData> sameExact) {
        ArrayList<ChangedBlock<P>> changes = new ArrayList<ChangedBlock<P>>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (P position : positions) {
            BlockData oldBlockData = oldData.apply(position);
            BlockData newBlockData = newData.apply(position);
            if (sameExact.test(oldBlockData, newBlockData)) {
                continue;
            }
            changes.add(new ChangedBlock<P>(position, oldBlockData, newBlockData));
            int x = xValue.applyAsInt(position);
            int y = yValue.applyAsInt(position);
            int z = zValue.applyAsInt(position);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        return new TransformSummary<P>(changes, minX, minY, minZ, maxX, maxY, maxZ);
    }

    PreviewStatus previewStatus(boolean invalidSelection, boolean tooManyPositions, boolean outsideVerticalRange, boolean empty) {
        if (invalidSelection || tooManyPositions || outsideVerticalRange) {
            return new PreviewStatus(false, true, "too large.", NamedTextColor.DARK_GRAY);
        }
        if (empty) {
            return new PreviewStatus(false, true, "nothing changed.", NamedTextColor.DARK_GRAY);
        }
        return new PreviewStatus(true, false, "", NamedTextColor.GRAY);
    }

    PreviewFailureLifecycle previewFailureLifecycle(PreviewStatus status) {
        return new PreviewFailureLifecycle(!status.ready(), status.sendStatus());
    }

    interface PositionFactory<P> {
        P create(int x, int y, int z);
    }

    interface BlockTransform<P> {
        TransformedBlock<P> apply(int x, int y, int z);
    }

    record TransformedBlock<P>(P position, BlockData blockData) {
    }

    record TransformPlan<P>(HashSet<P> affectedPositions, HashMap<P, BlockData> blockDataByPosition) {
    }

    record ChangedBlock<P>(P position, BlockData oldData, BlockData newData) {
    }

    record TransformSummary<P>(List<ChangedBlock<P>> changes, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        boolean empty() {
            return this.changes.isEmpty();
        }
    }

    record PreviewStatus(boolean ready, boolean sendStatus, String message, NamedTextColor color) {
    }

    record PreviewFailureLifecycle(boolean removeDrawingState, boolean sendStatus) {
    }
}
