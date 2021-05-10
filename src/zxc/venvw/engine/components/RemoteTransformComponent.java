package zxc.venvw.engine.components;

import zxc.venvw.engine.networking.ClientRpc;
import zxc.venvw.engine.networking.HostRpc;

public class RemoteTransformComponent extends Component {
    @HostRpc
    @ClientRpc
    public void setPositionRpc(float x, float y) {
        TransformComponent transform = getTransform();
        if (!Float.isNaN(x)) {
            transform.setX(x);
        }
        if (!Float.isNaN(y)) {
            transform.setY(y);
        }
    }
}
