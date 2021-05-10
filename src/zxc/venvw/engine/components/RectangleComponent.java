package zxc.venvw.engine.components;

import java.awt.*;

public class RectangleComponent extends Component {
    private final int width;
    private final int height;
    private final Color color;

    public RectangleComponent(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    protected void doDraw(Graphics graphics) {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);

        graphics.setColor(color);
        graphics.fillRect((int) transform.getX(), (int) transform.getY(), width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
