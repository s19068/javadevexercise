package org.example;

import java.util.*;

public class Wall implements Structure {
    private List<Block> blocks;

    public Wall(List<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public Optional<Block> findBlockByColor(String color) {
        return findBlockByColorRecursive(color, blocks);
    }

    private Optional<Block> findBlockByColorRecursive(String color, List<Block> blocks) {
        for (Block block : blocks) {
            if (block.getColor().equals(color)) {
                return Optional.of(block);
            }
            if (block instanceof CompositeBlock) {
                Optional<Block> found = findBlockByColorRecursive(color, ((CompositeBlock) block).getBlocks());
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Block> findBlocksByMaterial(String material) {
        List<Block> result = new ArrayList<>();
        findBlocksByMaterialRecursive(material, blocks, result);
        return result;
    }

    private void findBlocksByMaterialRecursive(String material, List<Block> blocks, List<Block> result) {
        for (Block block : blocks) {
            if (block.getMaterial().equals(material)) {
                result.add(block);
            }
            if (block instanceof CompositeBlock) {
                findBlocksByMaterialRecursive(material, ((CompositeBlock) block).getBlocks(), result);
            }
        }
    }

    @Override
    public int count() {
        return countRecursive(blocks);
    }

    private int countRecursive(List<Block> blocks) {
        int count = 0;
        for (Block block : blocks) {
            count++;
            if (block instanceof CompositeBlock) {
                count += countRecursive(((CompositeBlock) block).getBlocks());
            }
        }
        return count;
    }
}
