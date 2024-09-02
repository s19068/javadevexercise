package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Wall implements Structure {
    private final List<Block> blocks;

    public Wall(List<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public Optional<Block> findBlockByColor(String color) {
        return flattenBlocks(this.blocks)
                .filter(block -> block.getColor().equals(color))
                .findFirst();
    }

    @Override
    public List<Block> findBlocksByMaterial(String material) {
        return flattenBlocks(this.blocks)
                .filter(block -> block.getMaterial().equals(material))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public int count() {
        return (int) flattenBlocks(this.blocks).count();
    }

    // Pomocnicza metoda do spłaszczenia struktury bloków
    private Stream<Block> flattenBlocks(List<Block> blocks) {
        return blocks.stream()
                .flatMap(block -> {
                    if (block instanceof CompositeBlock) {
                        return Stream.concat(
                                Stream.of(block),
                                flattenBlocks(((CompositeBlock) block).getBlocks())
                        );
                    } else {
                        return Stream.of(block);
                    }
                });
    }
}
