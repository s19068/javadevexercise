package org.example;

import java.util.*;
import java.util.function.Predicate;

public class Wall implements Structure {
    private List<Block> blocks;

    public Wall(List<Block> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    @Override
    public Optional<Block> findBlockByColor(String color) {
        return findBlock(block -> color.equals(block.getColor()));
    }

    @Override
    public List<Block> findBlocksByMaterial(String material) {
        return findBlocks(block -> material.equals(block.getMaterial()));
    }

    @Override
    public int count() {
        return countBlocks();
    }

    private Optional<Block> findBlock(Predicate<Block> predicate) {
        Deque<Block> stack = new ArrayDeque<>(blocks);
        while (!stack.isEmpty()) {
            Block current = stack.pop();
            if (predicate.test(current)) {
                return Optional.of(current);
            }
            if (current instanceof CompositeBlock) {
                stack.addAll(((CompositeBlock) current).getBlocks());
            }
        }
        return Optional.empty();
    }

    private List<Block> findBlocks(Predicate<Block> predicate) {
        List<Block> result = new ArrayList<>();
        Deque<Block> stack = new ArrayDeque<>(blocks);
        while (!stack.isEmpty()) {
            Block current = stack.pop();
            if (predicate.test(current)) {
                result.add(current);
            }
            if (current instanceof CompositeBlock) {
                stack.addAll(((CompositeBlock) current).getBlocks());
            }
        }
        return result;
    }

    private int countBlocks() {
        int count = 0;
        Deque<Block> stack = new ArrayDeque<>(blocks);
        while (!stack.isEmpty()) {
            Block current = stack.pop();
            count++;
            if (current instanceof CompositeBlock) {
                stack.addAll(((CompositeBlock) current).getBlocks());
            }
        }
        return count;
    }
}
