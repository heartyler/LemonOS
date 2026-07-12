package dev.lemonos;

final class BackendSandboxDrawingTransitionService {
    boolean startsWithSize(String action) {
        return "CIRCLE".equals(action);
    }

    boolean usesMoreTool(String action) {
        return "CIRCLE".equals(action) || "REPLACE".equals(action) || "FLIP".equals(action) || "ROTATE".equals(action);
    }

    StartPlan startPlan(String action) {
        return new StartPlan(this.startsWithSize(action), this.usesMoreTool(action));
    }

    boolean expectsChat(String inputStep, boolean hasSelection, String action) {
        if (!"NONE".equals(inputStep) && !"READY".equals(inputStep)) {
            return true;
        }
        return hasSelection && !"CLEAR".equals(action) && !"CLONE".equals(action);
    }

    boolean rejectsBlockClick(String inputStep) {
        return "SIZE".equals(inputStep) || "CIRCLE_BLOCK".equals(inputStep) || "FLIP".equals(inputStep) || "ROTATION".equals(inputStep);
    }

    BlockClickRoute blockClickRoute(String action, String inputStep, boolean hasFirst, boolean hasSecond, boolean hasBlockData) {
        if ("CIRCLE".equals(action) && "CIRCLE_BLOCK".equals(inputStep)) {
            return BlockClickRoute.CIRCLE_BLOCK_PICK;
        }
        if (this.rejectsBlockClick(inputStep)) {
            return BlockClickRoute.REJECT_BLOCK_CLICK;
        }
        if ("CIRCLE".equals(action) && "READY".equals(inputStep)) {
            return BlockClickRoute.CIRCLE_CENTER_APPLY;
        }
        if ("REPLACE_SOURCE".equals(inputStep)) {
            return BlockClickRoute.REPLACE_SOURCE_PICK;
        }
        if ("NEW_BLOCK".equals(inputStep)) {
            return BlockClickRoute.NEW_BLOCK_PICK;
        }
        if (!hasFirst) {
            return BlockClickRoute.FIRST_CORNER;
        }
        if (!hasSecond) {
            return BlockClickRoute.SECOND_CORNER;
        }
        if ("CLONE".equals(action)) {
            return BlockClickRoute.CLONE_PLACE;
        }
        if (!"NONE".equals(inputStep) && !"READY".equals(inputStep)) {
            return BlockClickRoute.IGNORE;
        }
        if (!hasBlockData && !"CLEAR".equals(action)) {
            return BlockClickRoute.FINAL_BLOCK_APPLY;
        }
        return BlockClickRoute.IGNORE;
    }

    SelectionTransition afterSelection(String action) {
        if ("CLEAR".equals(action)) {
            return SelectionTransition.CLEAR_PREVIEW;
        }
        if ("CLONE".equals(action)) {
            return SelectionTransition.CLONE_PLACE;
        }
        if ("REPLACE".equals(action)) {
            return SelectionTransition.REPLACE_SOURCE;
        }
        if ("FLIP".equals(action)) {
            return SelectionTransition.FLIP_INPUT;
        }
        if ("ROTATE".equals(action)) {
            return SelectionTransition.ROTATION_INPUT;
        }
        return SelectionTransition.BLOCK_INPUT;
    }

    DrawingInputRoute inputRoute(String inputStep) {
        if ("SIZE".equals(inputStep)) {
            return DrawingInputRoute.SIZE;
        }
        if ("CIRCLE_BLOCK".equals(inputStep)) {
            return DrawingInputRoute.CIRCLE_BLOCK;
        }
        if ("REPLACE_SOURCE".equals(inputStep)) {
            return DrawingInputRoute.REPLACE_SOURCE;
        }
        if ("NEW_BLOCK".equals(inputStep)) {
            return DrawingInputRoute.NEW_BLOCK;
        }
        if ("FLIP".equals(inputStep)) {
            return DrawingInputRoute.FLIP;
        }
        if ("ROTATION".equals(inputStep)) {
            return DrawingInputRoute.ROTATION;
        }
        return DrawingInputRoute.BASIC_BLOCK;
    }

    enum SelectionTransition {
        CLEAR_PREVIEW,
        CLONE_PLACE,
        REPLACE_SOURCE,
        FLIP_INPUT,
        ROTATION_INPUT,
        BLOCK_INPUT
    }

    enum BlockClickRoute {
        CIRCLE_BLOCK_PICK,
        REJECT_BLOCK_CLICK,
        CIRCLE_CENTER_APPLY,
        REPLACE_SOURCE_PICK,
        NEW_BLOCK_PICK,
        FIRST_CORNER,
        SECOND_CORNER,
        CLONE_PLACE,
        FINAL_BLOCK_APPLY,
        IGNORE
    }

    enum DrawingInputRoute {
        SIZE,
        CIRCLE_BLOCK,
        REPLACE_SOURCE,
        NEW_BLOCK,
        FLIP,
        ROTATION,
        BASIC_BLOCK
    }

    record StartPlan(boolean startsWithSize, boolean moreTool) {
    }
}
