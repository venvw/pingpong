package zxc.venvw;

import zxc.venvw.engine.Canvas;
import zxc.venvw.engine.GameEntity;
import zxc.venvw.engine.InputManager;
import zxc.venvw.engine.networking.NetworkManager;
import zxc.venvw.engine.components.*;
import zxc.venvw.engine.networking.components.NetworkComponent;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.*;

public class Application extends JFrame {
    private final List<GameEntity> entities;
    private final InputManager inputManager;
    private final NetworkManager networkManager;

    public Application(String hostAddress, int port) throws SocketException, UnknownHostException {
        super("ping pong" + hostAddress);

        this.inputManager = new InputManager();
        this.entities = new ArrayList<>();

        boolean isHost = hostAddress == null;

        if (isHost) {
            networkManager = NetworkManager.host(this, port);
        } else {
            networkManager = NetworkManager.connect(this, InetAddress.getByName(hostAddress), port);
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        try {
            GameEntity player = new GameEntity(this);
            player.addComponent(new RectangleComponent(10, 100, Color.WHITE));
            player.addComponent(new MovementComponent(VK_W, VK_S, 500));
            player.addComponent(new NetworkComponent(0));
            TransformComponent playerTransform = player.getComponent(TransformComponent.class);
            playerTransform.setX(50);
            entities.add(player);

            GameEntity enemy = new GameEntity(this);
            enemy.addComponent(new RectangleComponent(10, 100, Color.WHITE));
            enemy.addComponent(new NetworkComponent(1));
            enemy.addComponent(new RemoteTransformComponent());
            TransformComponent enemyTransform = enemy.getComponent(TransformComponent.class);
            enemyTransform.setX(1280 - 10 - 50);
            entities.add(enemy);

            GameEntity ball = new GameEntity(this);
            ball.addComponent(new CircleComponent(15, Color.WHITE));
            ball.addComponent(new NetworkComponent(2));
            ball.addComponent(new RemoteTransformComponent());
            TransformComponent ballTransform = ball.getComponent(TransformComponent.class);
            ballTransform.setX(1280 * 0.5f);
            ballTransform.setY(720 * 0.5f);
            if (isHost) {
                ball.addComponent(new BallComponent(1280 * 0.5f, 720 * 0.5f, 850.f,
                        player.getComponent(RectangleComponent.class), enemy.getComponent(RectangleComponent.class)));
            }
            entities.add(ball);
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.scheduleAtFixedRate(() -> {
            for (GameEntity entity : entities) {
                entity.update(0.005f);
                repaint();
            }
        }, 0, 5, TimeUnit.MILLISECONDS);

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        add(new Canvas(this));
        pack();
        addKeyListener(inputManager);
    }

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                new Application(null, 1459);
            } else {
                new Application(args[0], 1459);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<GameEntity> getEntities() {
        return entities;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }
}
