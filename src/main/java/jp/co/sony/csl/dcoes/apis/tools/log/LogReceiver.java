package jp.co.sony.csl.dcoes.apis.tools.log;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Handler;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import jp.co.sony.csl.dcoes.apis.common.util.vertx.JsonObjectUtil;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.VertxConfig;
import jp.co.sony.csl.dcoes.apis.tools.log.util.MongoDbWriter;

public class LogReceiver extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(LogReceiver.class);

    private static final String DEFAULT_IPV6 = Boolean.FALSE.toString();
    private static final String DEFAULT_MULTICAST_GROUP_ADDRESS_V4 = "224.0.0.1";
    private static final String DEFAULT_MULTICAST_GROUP_ADDRESS_V6 = "FF01::1";
    private static final String DEFAULT_PORT = "8888";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        initializeMongoDbWriter_(resInitializeMongoDbWriter -> {
            if (resInitializeMongoDbWriter.succeeded()) {
                startSocketService_(res -> {
                    if (res.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(res.cause());
                    }
                });
            } else {
                startPromise.fail(resInitializeMongoDbWriter.cause());
            }
        });
    }

    private void startSocketService_(Handler<AsyncResult<Void>> completionHandler) {
        Boolean ipv6 = VertxConfig.config.getBoolean(DEFAULT_IPV6, "logReceiver", "ipv6");
        String multicastGroupAddress = (ipv6) ? VertxConfig.config.getString(new JsonObjectUtil.DefaultString(DEFAULT_MULTICAST_GROUP_ADDRESS_V6), "logReceiver", "multicastGroupAddress") : VertxConfig.config.getString(new JsonObjectUtil.DefaultString(DEFAULT_MULTICAST_GROUP_ADDRESS_V4), "logReceiver", "multicastGroupAddress");
        int port = VertxConfig.config.getInteger(DEFAULT_PORT, "logReceiver", "port");
        String listenAddress = (ipv6) ? "::" : "0.0.0.0";
        Boolean printToStdout = VertxConfig.config.getBoolean(Boolean.FALSE, "logReceiver", "printToStdout");
        findNetworkInterfaceName_(ipv6, multicastGroupAddress, resNetworkInterfaceName -> {
            if (resNetworkInterfaceName.succeeded()) {
                String networkInterfaceName = resNetworkInterfaceName.result();
                DatagramSocket socket;
                try {
                    socket = vertx.createDatagramSocket(new DatagramSocketOptions().setReuseAddress(true).setReusePort(true).setIpV6(ipv6));
                } catch (Exception e) {
                    completionHandler.handle(Future.failedFuture(e));
                    return;
                }
                if (log.isInfoEnabled()) log.info("ipv6 : " + ipv6);
                if (log.isInfoEnabled()) log.info("multicastGroupAddress : " + multicastGroupAddress);
                if (log.isInfoEnabled()) log.info("port : " + port);
                if (log.isInfoEnabled()) log.info("listenAddress : " + listenAddress);
                if (log.isInfoEnabled()) log.info("networkInterfaceName : " + networkInterfaceName);
                socket.handler(packet -> {
                    MongoDbWriter.write(new JsonObject(packet.data().toString()));
                    if (printToStdout) System.out.println("[" + packet.sender() + "] " + String.valueOf(packet.data()).trim());
                }).exceptionHandler(t -> {
                    log.error("exceptionHandler : " + t);
                }).listen(port, listenAddress, resListen -> {
                    if (resListen.succeeded()) {
                        socket.listenMulticastGroup(multicastGroupAddress, resListenMulticastGroup -> {
                            if (resListenMulticastGroup.succeeded()) {
                                if (log.isInfoEnabled()) log.info("log receive multicast service started on group address : " + multicastGroupAddress + ", port : " + port);
                                completionHandler.handle(Future.succeededFuture());
                            } else {
                                completionHandler.handle(Future.failedFuture(resListenMulticastGroup.cause()));
                            }
                        });
                    } else {
                        completionHandler.handle(Future.failedFuture(resListen.cause()));
                    }
                });
            } else {
                completionHandler.handle(Future.failedFuture(resNetworkInterfaceName.cause()));
            }
        });
    }

    private void findNetworkInterfaceName_(Boolean ipv6, String multicastGroupAddress, Handler<AsyncResult<String>> completionHandler) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces != null) {
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    if (addresses != null) {
                        while (addresses.hasMoreElements()) {
                            InetAddress address = addresses.nextElement();
                            if (!address.isLoopbackAddress()) {
                                if (ipv6 && address instanceof Inet6Address) {
                                    if (address.isMulticastAddress()) {
                                        if (log.isInfoEnabled()) log.info("found IPv6 multicast address : " + address);
                                        completionHandler.handle(Future.succeededFuture(networkInterface.getName()));
                                        return;
                                    }
                                } else if (!ipv6 && address instanceof Inet4Address) {
                                    if (address.isMulticastAddress()) {
                                        if (log.isInfoEnabled()) log.info("found IPv4 multicast address : " + address);
                                        completionHandler.handle(Future.succeededFuture(networkInterface.getName()));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                completionHandler.handle(Future.succeededFuture(null));
            } else {
                completionHandler.handle(Future.failedFuture("no network interface found"));
            }
        } catch (SocketException e) {
            log.error("Socket exception while getting network interface", e);
            completionHandler.handle(Future.failedFuture(e));
        }
    }

    private void initializeMongoDbWriter_(Handler<AsyncResult<Void>> completionHandler) {
        MongoDbWriter.initialize(vertx, completionHandler);
    }
}
