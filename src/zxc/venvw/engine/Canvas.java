package zxc.venvw.engine;

import zxc.venvw.Application;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Canvas extends JPanel {
    private final Application application;

    public Canvas(Application application) {
        super();

        this.application = application;

        Dimension size = new Dimension();
        size.setSize(1280, 720);
        setPreferredSize(size);

    }

    @Override
    protected void paintComponent(Graphics g) {
        List<GameEntity> entities = application.getEntities();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (GameEntity entity : entities) {
            entity.render(g);
        }

    }
}
