package zxc.venvw.engine.components;

import zxc.venvw.engine.InputManager;

public class MovementComponent extends Component {
    private final int upKeyCode;
    private final int downKeyCode;
    private final int speed;

    public MovementComponent(int upKeyCode, int downKeyCode, int speed) {
        this.upKeyCode = upKeyCode;
        this.downKeyCode = downKeyCode;
        this.speed = speed;
    }

    @Override
    protected void doUpdate(float deltaTime) {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);
        InputManager input = getEntity().getApplication().getInputManager();

        if (input.getKeyPressed(upKeyCode)) {
            transform.setY(transform.getY() - speed * deltaTime);
        }

        if (input.getKeyPressed(downKeyCode)) {
            transform.setY(transform.getY() + speed * deltaTime);
        }

        getEntity().getApplication().getNetworkManager().rpc(1, RemoteTransformComponent.class.getName(), "setPositionRpc", Float.NaN, transform.getY());
    }
}
