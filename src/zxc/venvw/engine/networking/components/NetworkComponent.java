package zxc.venvw.engine.networking.components;

import zxc.venvw.engine.components.Component;

public class NetworkComponent extends Component {
    private final int id;

    public NetworkComponent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
