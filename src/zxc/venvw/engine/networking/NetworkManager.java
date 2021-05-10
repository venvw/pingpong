package zxc.venvw.engine.networking;

import zxc.venvw.Application;
import zxc.venvw.engine.GameEntity;
import zxc.venvw.engine.networking.components.NetworkComponent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {
    private final Application application;
    private final ExecutorService executor;
    private final DatagramSocket socket;
    private final boolean isHost;
    private InetAddress address = null;
    private Integer port = null;

    private NetworkManager(Application application, DatagramSocket socket, boolean isHost, InetAddress address, Integer port) {
        this.application = application;
        this.socket = socket;
        this.isHost = isHost;
        this.address = address;
        this.port = port;

        this.executor = Executors.newSingleThreadExecutor();
        executor.execute(this::listen);
    }

    public void rpc(int id, String componentName, String methodName, Object... args) {
        if (address == null) {
            return;
        }

        if (port == null) {
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024)
                .putInt(id)
                .putInt(componentName.getBytes().length)
                .put(componentName.getBytes())
                .putInt(methodName.getBytes().length)
                .put(methodName.getBytes());

        int argsPut = 0;

        while (argsPut < args.length) {
            if (args[argsPut].getClass().isAssignableFrom(Float.class)) {
                buffer.putFloat((Float) args[argsPut++]);
            } else {
                throw new IllegalArgumentException();
            }
        }

        DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length);
        packet.setAddress(address);
        packet.setPort(port);

        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            this.address = packet.getAddress();
            this.port = packet.getPort();

            ByteBuffer wrapper = ByteBuffer.wrap(buffer);
            int networkEntityId = wrapper.getInt();

            int componentNameLength = wrapper.getInt();
            byte[] componentNameBytes = new byte[componentNameLength];
            wrapper.get(componentNameBytes);
            String componentName = new String(componentNameBytes);

            int methodNameLength = wrapper.getInt();
            byte[] methodNameBytes = new byte[methodNameLength];
            wrapper.get(methodNameBytes);
            String methodName = new String(methodNameBytes);

            Class<?> c;
            try {
                c = Class.forName(componentName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            Method m;
            try {
                m = Arrays.stream(c.getDeclaredMethods()).filter(method -> method.getName().equals(methodName)).findFirst().orElseThrow(NoSuchMethodException::new);
            } catch (NoSuchMethodException e){
                e.printStackTrace();
                continue;
            }

            if (!this.isHost && !m.isAnnotationPresent(ClientRpc.class)) {
                continue;
            }

            if (this.isHost && !m.isAnnotationPresent(HostRpc.class)) {
                continue;
            }

            Class<?>[] parameterTypes = m.getParameterTypes();
            List<Object> parameters = new ArrayList<>();
            for (Class<?> pt : parameterTypes) {
                if (pt.isAssignableFrom(float.class)) {
                    parameters.add(wrapper.getFloat());
                } else {
                    break;
                }
            }

            if (parameters.size() != parameterTypes.length) {
                continue;
            }

            Optional<GameEntity> entity = application.getEntities().stream().filter(gameEntity -> {
                NetworkComponent component = gameEntity.getComponent(NetworkComponent.class);
                return networkEntityId == component.getId();
            }).findFirst();

            if (entity.isPresent()) {
                Object component = entity.get().getZxcComponent(c);

                if (component != null) {
                    try {
                        m.invoke(component, parameters.toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static NetworkManager host(Application application, int port) throws SocketException {
        return new NetworkManager(application, new DatagramSocket(port), true, null, null);
    }

    public static NetworkManager connect(Application application, InetAddress address, int port) throws SocketException {
        return new NetworkManager(application, new DatagramSocket(), false, address, port);
    }
}
