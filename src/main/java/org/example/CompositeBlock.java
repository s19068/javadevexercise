package org.example;

import java.util.List;

interface CompositeBlock extends Block {
    List<Block> getBlocks();
}
