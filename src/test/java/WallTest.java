
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WallTest {

    private Object wall;
    private Object simpleBlock1;
    private Object simpleBlock2;
    private Object compositeBlock1;
    private Object compositeBlock2;
    private Class<?> wallClass;
    private Class<?> blockClass;
    private Class<?> compositeBlockClass;

    @BeforeEach
    void setUp() throws Exception {
        wallClass = Class.forName("org.example.Wall");
        blockClass = Class.forName("org.example.Block");
        compositeBlockClass = Class.forName("org.example.CompositeBlock");

        simpleBlock1 = createBlockInstance("Red", "Wood");
        simpleBlock2 = createBlockInstance("Blue", "Metal");
        compositeBlock1 = createCompositeBlockInstance("Green", "Concrete", List.of(simpleBlock1, simpleBlock2));
        compositeBlock2 = createCompositeBlockInstance("Yellow", "Plastic", List.of(compositeBlock1, simpleBlock1));

        List<Object> blocks = new ArrayList<>();
        blocks.add(compositeBlock1);
        blocks.add(compositeBlock2);

        Constructor<?> wallConstructor = wallClass.getDeclaredConstructor(List.class);
        wallConstructor.setAccessible(true);
        wall = wallConstructor.newInstance(blocks);
    }

    private Object createBlockInstance(String color, String material) throws Exception {
        return java.lang.reflect.Proxy.newProxyInstance(
                blockClass.getClassLoader(),
                new Class<?>[]{blockClass},
                (proxy, method, args) -> {
                    if (method.getName().equals("getColor")) {
                        return color;
                    } else if (method.getName().equals("getMaterial")) {
                        return material;
                    }
                    return null;
                });
    }

    private Object createCompositeBlockInstance(String color, String material, List<Object> nestedBlocks) throws Exception {
        return java.lang.reflect.Proxy.newProxyInstance(
                compositeBlockClass.getClassLoader(),
                new Class<?>[]{compositeBlockClass},
                (proxy, method, args) -> {
                    if (method.getName().equals("getColor")) {
                        return color;
                    } else if (method.getName().equals("getMaterial")) {
                        return material;
                    } else if (method.getName().equals("getBlocks")) {
                        return nestedBlocks;
                    }
                    return null;
                });
    }

    @Test
    void testFindBlockByColor() throws Exception {
        Method findBlockByColorMethod = wallClass.getDeclaredMethod("findBlockByColor", String.class);
        findBlockByColorMethod.setAccessible(true);
        Optional<?> foundBlock = (Optional<?>) findBlockByColorMethod.invoke(wall, "Red");
        assertTrue(foundBlock.isPresent(), "Block with color 'Red' should be found");

        Method getColorMethod = blockClass.getDeclaredMethod("getColor");
        getColorMethod.setAccessible(true);
        String color = (String) getColorMethod.invoke(foundBlock.get());
        assertEquals("Red", color, "Found block should have color 'Red'");

        Optional<?> notFoundBlock = (Optional<?>) findBlockByColorMethod.invoke(wall, "Purple");
        assertFalse(notFoundBlock.isPresent(), "Block with color 'Purple' should not be found");
    }

    @Test
    void testFindBlocksByMaterial() throws Exception {
        Method findBlocksByMaterialMethod = wallClass.getDeclaredMethod("findBlocksByMaterial", String.class);
        findBlocksByMaterialMethod.setAccessible(true);
        List<?> foundBlocks = (List<?>) findBlocksByMaterialMethod.invoke(wall, "Wood");
        assertEquals(2, foundBlocks.size(), "There should be 2 blocks with material 'Wood'");

        List<?> notFoundBlocks = (List<?>) findBlocksByMaterialMethod.invoke(wall, "Glass");
        assertEquals(0, notFoundBlocks.size(), "There should be no blocks with material 'Glass'");
    }

    @Test
    void testCount() throws Exception {
        Method countMethod = wallClass.getDeclaredMethod("count");
        countMethod.setAccessible(true);
        int totalBlocks = (int) countMethod.invoke(wall);
        assertEquals(5, totalBlocks, "Total number of blocks should be 5 (including nested blocks)");
    }
}