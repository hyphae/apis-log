package jp.co.sony.csl.dcoes.apis.tools.log;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import jp.co.sony.csl.dcoes.apis.common.util.vertx.JsonObjectUtil;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.VertxConfig;
import jp.co.sony.csl.dcoes.apis.tools.log.util.MongoDbWriter;

/**
 * UDP でマルチキャストされる APIS プログラムのログを受信し処理する Verticle.
 * {@link jp.co.sony.csl.dcoes.apis.tools.log.util.Starter} Verticle から起動される.
 * @author OES Project
 */
public class LogReceiver extends AbstractVerticle {
	private static final Logger log = LoggerFactory.getLogger(LogReceiver.class);

	private static final Boolean DEFAULT_IPV6 = Boolean.TRUE;
	private static final String DEFAULT_MULTICAST_GROUP_ADDRESS_V4 = "224.2.2.4";
	private static final String DEFAULT_MULTICAST_GROUP_ADDRESS_V6 = "FF02:0:0:0:0:0:0:1";
	private static final Integer DEFAULT_PORT = Integer.valueOf(8888);

	/**
	 * 起動時に呼び出される.
	 * MongoDB の初期化を呼び出す.
	 * ネットワークまわりの初期化を呼び出す.
	 * @param startFuture {@inheritDoc}
	 * @throws Exception {@inheritDoc}
	 */
	@Override public void start(Future<Void> startFuture) throws Exception {
		MongoDbWriter.initialize(vertx, resInitializeMongoDbWriter -> {
			if (resInitializeMongoDbWriter.succeeded()) {
				startSocketService_(resSocket -> {
					if (resSocket.succeeded()) {
						if (log.isTraceEnabled()) log.trace("started : " + deploymentID());
						startFuture.complete();
					} else {
						startFuture.fail(resSocket.cause());
					}
				});
			} else {
				startFuture.fail(resInitializeMongoDbWriter.cause());
			}
		});
	}

	/**
	 * 停止時に呼び出される.
	 * @throws Exception {@inheritDoc}
	 */
	@Override public void stop() throws Exception {
		if (log.isTraceEnabled()) log.trace("stopped : " + deploymentID());
	}

	////

	/**
	 * ネットワークまわりの初期化.
	 * CONFIG から設定を取得し初期化する.
	 * - CONFIG.logReceiver.ipv6 : IPv6 フラグ. true なら IPv6 [{@link Boolean}]
	 * - CONFIG.logReceiver.multicastGroupAddress : マルチキャストグループアドレス [{@link String}]
	 * - CONFIG.logReceiver.port : ポート [{@link Integer}]
	 * - CONFIG.logReceiver.printToStdout : 標準出力フラグ. true なら受信したログを標準出力に出力する [{@link Boolean}]
	 * @param completionHandler the completion handler
	 */
	private void startSocketService_(Handler<AsyncResult<Void>> completionHandler) {
		Boolean ipv6 = VertxConfig.config.getBoolean(DEFAULT_IPV6, "logReceiver", "ipv6");
		String multicastGroupAddress = (ipv6) ? VertxConfig.config.getString(new JsonObjectUtil.DefaultString(DEFAULT_MULTICAST_GROUP_ADDRESS_V6), "logReceiver", "multicastGroupAddress") : VertxConfig.config.getString(new JsonObjectUtil.DefaultString(DEFAULT_MULTICAST_GROUP_ADDRESS_V4), "logReceiver", "multicastGroupAddress");
		Integer port = VertxConfig.config.getInteger(DEFAULT_PORT, "logReceiver", "port");
		String listenAddress = (ipv6) ? "::" : "0.0.0.0";
		Boolean printToStdout = VertxConfig.config.getBoolean(Boolean.FALSE, "logReceiver", "printToStdout");
		findNetworkInterfaceName_(ipv6, multicastGroupAddress, resNetworkInterfaceName -> {
			if (resNetworkInterfaceName.succeeded()) {
				String networkInterfaceName = resNetworkInterfaceName.result();
				DatagramSocket socket;
				try {
					socket = vertx.createDatagramSocket(new DatagramSocketOptions().setIpV6(ipv6));
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
					// パケット受信時の処理
					MongoDbWriter.write(packet);
					if (printToStdout) System.out.println("[" + packet.sender() + "] " + String.valueOf(packet.data()).trim());
				}).exceptionHandler(t -> {
					log.error("exceptionHandler : " + t);
				}).listen(port, listenAddress, resListen -> {
					if (resListen.succeeded()) {
						socket.listenMulticastGroup(multicastGroupAddress, networkInterfaceName, null, resListenMulticastGroup -> {
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
	private void findNetworkInterfaceName_(boolean isIpv6, String multicastGroupAddress, Handler<AsyncResult<String>> completionHandler) {
		if (isIpv6) {
			int pos = multicastGroupAddress.lastIndexOf('%');
			if (0 <= pos) {
				completionHandler.handle(Future.succeededFuture(multicastGroupAddress.substring(pos + 1)));
				return;
			}
		}
		String result = null;
		try {
			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
			if (nis != null) {
				outside: while (nis.hasMoreElements()) {
					NetworkInterface ni = nis.nextElement();
					if (!ni.isLoopback() && ni.isUp()) {
						if (ni.getName() != null && ni.getName().startsWith("e")) {
							byte[] ha = ni.getHardwareAddress();
							if (ha != null) {
								Enumeration<InetAddress> ias = ni.getInetAddresses();
								if (ias != null) {
									while (ias.hasMoreElements()) {
										InetAddress ia = ias.nextElement();
										if (isIpv6 && ia instanceof Inet6Address) {
											result = ni.getName();
											break outside;
										}
										if (!isIpv6 && ia instanceof Inet4Address) {
											result = ni.getName();
											break outside;
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (SocketException e) {
			log.error(e);
			completionHandler.handle(Future.failedFuture(e));
			return;
		}
		if (result != null) {
			completionHandler.handle(Future.succeededFuture(result));
		} else {
			completionHandler.handle(Future.failedFuture("no network interface name found"));
		}
	}

}
