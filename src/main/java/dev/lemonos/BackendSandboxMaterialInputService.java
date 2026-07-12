package dev.lemonos;

import java.util.Locale;
import org.bukkit.Material;

final class BackendSandboxMaterialInputService {
    Material newBlockMaterial(String input, boolean replace, Material replaceTargetMaterial, Material defaultSandboxMaterial) {
        return input.isBlank() ? (replace ? replaceTargetMaterial : defaultSandboxMaterial) : this.parseBlockMaterial(input);
    }

    Material oldBlockMaterial(String input, Material replaceSourceMaterial) {
        return input.isBlank() ? replaceSourceMaterial : this.parseBlockMaterial(input);
    }

    Material circleBlockMaterial(String input, Material defaultSandboxMaterial) {
        return input.isBlank() ? defaultSandboxMaterial : this.parseCircleMaterial(input);
    }

    Material basicBlockMaterial(String input) {
        return this.parseBlockMaterial(input);
    }

    String failureMessage() {
        return "try again.";
    }

    private Material parseBlockMaterial(String input) {
        Material material = this.matchMaterial(input);
        return material != null && material.isBlock() ? material : null;
    }

    private Material parseCircleMaterial(String input) {
        Material material = this.matchMaterial(input);
        if (material == Material.AIR) {
            return material;
        }
        return material != null && material.isBlock() ? material : null;
    }

    private Material matchMaterial(String input) {
        return Material.matchMaterial(input.trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
    }
}
