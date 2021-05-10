package zxc.venvw.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class InputManager extends KeyAdapter {
    private final Map<Integer, Boolean> keys;

    public InputManager() {
        keys = new HashMap<>();
    }

    private void keyStateChanged(KeyEvent e, boolean state) {
        Boolean replaced = keys.replace(e.getKeyCode(), state);

        if (replaced == null) {
            keys.put(e.getKeyCode(), state);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyStateChanged(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyStateChanged(e, false);
    }

    public boolean getKeyPressed(int keyCode) {
        return keys.getOrDefault(keyCode, false);
    }
}
