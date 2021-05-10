package zxc.venvw.engine.components;

import java.awt.*;

public class CircleComponent extends Component {
    private final int radius;
    private final Color color;

    public CircleComponent(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    protected void doDraw(Graphics graphics) {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);

        graphics.setColor(color);
        graphics.fillOval((int) transform.getX() - radius, (int) transform.getY() - radius, radius * 2, radius * 2);
    }

    public int getRadius() {
        return radius;
    }
}
