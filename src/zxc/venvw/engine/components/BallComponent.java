package zxc.venvw.engine.components;

import java.util.logging.Handler;

public class BallComponent extends Component {
    private float resetPositionX;
    private float resetPositionY;

    private final float velocity;
    private float velocityX;
    private float velocityY;

    private RectangleComponent leftRectangle;
    private RectangleComponent rightRectangle;

    public BallComponent(float resetPositionX, float resetPositionY, float velocity, RectangleComponent leftRectangle, RectangleComponent rightRectangle) {
        this.resetPositionX = resetPositionX;
        this.resetPositionY = resetPositionY;
        this.velocity = velocity;
        this.velocityX = velocity;
        this.velocityY = 0.f;
        this.leftRectangle = leftRectangle;
        this.rightRectangle = rightRectangle;
    }

    private void resetPosition() {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);

        transform.setX(resetPositionX);
        transform.setY(resetPositionY);
    }

    private RectangleComponent findRectangleUsingVelocity() {
        if (velocityX > 0)
            return rightRectangle;

        return leftRectangle;
    }

    @Override
    protected void doUpdate(float deltaTime) {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);
        CircleComponent circle = getEntity().getComponent(CircleComponent.class);

        transform.setX(transform.getX() + velocityX * deltaTime);
        transform.setY(transform.getY() + velocityY * deltaTime);

        getEntity().getApplication().getNetworkManager().rpc(2, RemoteTransformComponent.class.getName(), "setPositionRpc", 1280 - transform.getX(), transform.getY());

        int x = (int) transform.getX();
        int y = (int) transform.getY();
        int radius = circle.getRadius();

        if (!isRectangleCollision(circle) && (x < 0 || x > 1280)) {
            resetPosition();
        }

        if (y - radius < 0 || y + radius > 720) {
            velocityY = -velocityY;
        }
    }

    private static float lerp(float x, float y, float t) {
        return x + y * t;
    }

    private static float clamp(float v, float min, float max) {
        return v < min ? min : Math.min(v, max);
    }

    private boolean isRectangleCollision(CircleComponent circle) {
        RectangleComponent rectangle = findRectangleUsingVelocity();

        var distX = Math.abs(circle.getTransform().getX() - rectangle.getTransform().getX() - rectangle.getWidth() * 0.5f);
        var distY = circle.getTransform().getY() - rectangle.getTransform().getY() - rectangle.getHeight() * 0.5f;
        float angleSign = Math.signum(distY);
        distY = Math.abs(distY);

        if (distX > (rectangle.getWidth() * 0.5f + circle.getRadius())) {
            return false;
        }
        if (distY > (rectangle.getHeight() * 0.5f + circle.getRadius())) {
            return false;
        }

        if (distY <= (rectangle.getHeight() * 0.5f)) {
            float sign = Math.signum(velocityX);

            velocityX = velocity;
            velocityY = 0;

            float angle = angleSign * lerp((float) Math.toRadians(0), (float) Math.toRadians(30), distY / rectangle.getHeight() * 2.f);

            float rvx = (float) ((velocityX * Math.cos(angle)) - (velocityY * Math.sin(angle)));
            float rvy = (float) ((velocityX * Math.sin(angle)) + (velocityY * Math.cos(angle)));

            velocityX = -sign * rvx;
            velocityY = rvy;

            return true;
        }

        return false;
    }
}
