package zxc.venvw.engine;

import zxc.venvw.Application;
import zxc.venvw.engine.components.Component;
import zxc.venvw.engine.components.TransformComponent;

import javax.management.InstanceAlreadyExistsException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameEntity {
    private final Application application;
    private final List<Component> components;
    private boolean enabled;

    public GameEntity(Application application) {
        this.application = application;
        this.components = new ArrayList<>();
        this.components.add(new TransformComponent());
        this.enabled = true;
    }

    public void update(float deltaTime) {
        if (enabled) {
            for (Component component : components) {
                component.update(deltaTime);
            }
        }
    }

    public void render(Graphics graphics) {
        if (enabled) {
            for (Component component : components) {
                component.draw(graphics);
            }
        }
    }

    public <T extends Component> void addComponent(T component) throws InstanceAlreadyExistsException {
        if (component.getEntity() != null) {
            throw new IllegalArgumentException();
        }

        for (Component c : components) {
            if (component.getClass().isAssignableFrom(c.getClass())) {
                throw new InstanceAlreadyExistsException();
            }
        }

        components.add(component);
        component.setEntity(this);
    }

    public <T extends Component> T getComponent(Class<T> tClass) {
        for (Component component : components) {
            if (tClass.isAssignableFrom(component.getClass())) {
                return (T) component;
            }
        }

        return null;
    }

    public Object getZxcComponent(Class<?> tClass) {
        for (Component component : components) {
            if (tClass.isAssignableFrom(component.getClass())) {
                return component;
            }
        }

        return null;
    }

    public TransformComponent getTransform() {
        return getComponent(TransformComponent.class);
    }

    public <T extends Component> T removeComponent(Class<T> tClass) {
        if (tClass.isAssignableFrom(TransformComponent.class)) {
            throw new IllegalArgumentException();
        }

        Component found = null;

        for (Component component : components) {
            if (tClass.isAssignableFrom(component.getClass())) {
                found = component;
                break;
            }
        }

        if (found != null) {
            components.remove(found);
            found.setEntity(null);
        }

        return (T) found;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public Application getApplication() {
        return application;
    }
}
