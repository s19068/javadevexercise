import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WallTest {
    private Object wall;
    private Class<?> wallClass;
    private Class<?> blockInterface;
    private Class<?> compositeBlockInterface;
    private Method getColorMethod;
    private Method getMaterialMethod;

    @BeforeEach
    void setUp() throws Exception {
        wallClass = Class.forName("org.example.Wall");
        blockInterface = Class.forName("org.example.Block");
        compositeBlockInterface = Class.forName("org.example.CompositeBlock");

        getColorMethod = blockInterface.getDeclaredMethod("getColor");
        getMaterialMethod = blockInterface.getDeclaredMethod("getMaterial");
        getColorMethod.setAccessible(true);
        getMaterialMethod.setAccessible(true);

        Object redBrick = createBlock("red", "brick");
        Object blueConcrete = createBlock("blue", "concrete");
        Object greenWood = createBlock("green", "wood");
        Object yellowPlastic = createBlock("yellow", "plastic");

        Object compositeBlock = createCompositeBlock("multi", "mixed", Arrays.asList(greenWood, yellowPlastic));

        Constructor<?> wallConstructor = wallClass.getDeclaredConstructor(List.class);
        wallConstructor.setAccessible(true);
        wall = wallConstructor.newInstance(Arrays.asList(redBrick, blueConcrete, compositeBlock));
    }

    @Test
    void testFindBlockByColor() throws Exception {
        Method findBlockByColor = wallClass.getDeclaredMethod("findBlockByColor", String.class);
        findBlockByColor.setAccessible(true);

        Optional<?> redBlock = (Optional<?>) findBlockByColor.invoke(wall, "red");
        assertTrue(redBlock.isPresent());
        assertEquals("red", getColor(redBlock.get()));
        assertEquals("brick", getMaterial(redBlock.get()));

        Optional<?> yellowBlock = (Optional<?>) findBlockByColor.invoke(wall, "yellow");
        assertTrue(yellowBlock.isPresent());
        assertEquals("yellow", getColor(yellowBlock.get()));
        assertEquals("plastic", getMaterial(yellowBlock.get()));

        Optional<?> purpleBlock = (Optional<?>) findBlockByColor.invoke(wall, "purple");
        assertFalse(purpleBlock.isPresent());
    }

    @Test
    void testFindBlocksByMaterial() throws Exception {
        Method findBlocksByMaterial = wallClass.getDeclaredMethod("findBlocksByMaterial", String.class);
        findBlocksByMaterial.setAccessible(true);

        List<?> brickBlocks = (List<?>) findBlocksByMaterial.invoke(wall, "brick");
        assertEquals(1, brickBlocks.size());
        assertEquals("red", getColor(brickBlocks.get(0)));

        List<?> woodBlocks = (List<?>) findBlocksByMaterial.invoke(wall, "wood");
        assertEquals(1, woodBlocks.size());
        assertEquals("green", getColor(woodBlocks.get(0)));

        List<?> glassBlocks = (List<?>) findBlocksByMaterial.invoke(wall, "glass");
        assertTrue(glassBlocks.isEmpty());
    }

    @Test
    void testCount() throws Exception {
        Method count = wallClass.getDeclaredMethod("count");
        count.setAccessible(true);

        int blockCount = (int) count.invoke(wall);
        assertEquals(5, blockCount);

        Constructor<?> wallConstructor = wallClass.getDeclaredConstructor(List.class);
        wallConstructor.setAccessible(true);
        Object emptyWall = wallConstructor.newInstance(Collections.emptyList());
        assertEquals(0, (int) count.invoke(emptyWall));

        Object singleBlockWall = wallConstructor.newInstance(
                Collections.singletonList(createBlock("white", "marble")));
        assertEquals(1, (int) count.invoke(singleBlockWall));
    }

    private Object createBlock(String color, String material) {
        return Proxy.newProxyInstance(
                blockInterface.getClassLoader(),
                new Class<?>[] { blockInterface },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if (method.getName().equals("getColor")) return color;
                        if (method.getName().equals("getMaterial")) return material;
                        throw new UnsupportedOperationException();
                    }
                }
        );
    }

    private Object createCompositeBlock(String color, String material, List<Object> blocks) {
        return Proxy.newProxyInstance(
                compositeBlockInterface.getClassLoader(),
                new Class<?>[] { compositeBlockInterface },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if (method.getName().equals("getColor")) return color;
                        if (method.getName().equals("getMaterial")) return material;
                        if (method.getName().equals("getBlocks")) return new ArrayList<>(blocks);
                        throw new UnsupportedOperationException();
                    }
                }
        );
    }

    private String getColor(Object block) throws Exception {
        return (String) getColorMethod.invoke(block);
    }

    private String getMaterial(Object block) throws Exception {
        return (String) getMaterialMethod.invoke(block);
    }
}