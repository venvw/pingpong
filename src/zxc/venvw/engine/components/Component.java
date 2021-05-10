package zxc.venvw.engine.components;

import zxc.venvw.engine.GameEntity;

import java.awt.*;

public abstract class Component {
    private boolean enabled;
    private GameEntity entity;

    protected Component() {
        this.enabled = true;
        this.entity = null;
    }

    protected void doUpdate(float deltaTime) {
    }

    protected void doDraw(Graphics graphics) {
    }

    public void update(float deltaTime) {
        if (enabled) {
            doUpdate(deltaTime);
        }
    }

    public void draw(Graphics graphics) {
        if (enabled) {
            doDraw(graphics);
        }
    }

    public TransformComponent getTransform() {
        return entity.getTransform();
    }

    public GameEntity getEntity() {
        return entity;
    }

    public void setEntity(GameEntity entity) {
        this.entity = entity;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
