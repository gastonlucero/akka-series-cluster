package cluster.utils

import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.{Ignite, Ignition}

object IgniteHelper {

  def createIgnite () = {
    val cfg: IgniteConfiguration = new IgniteConfiguration()
    val spi = new TcpDiscoverySpi()
    spi.setLocalPort(1024)
    val ipFinder = new TcpDiscoveryMulticastIpFinder()
    ipFinder.setMulticastGroup("228.10.10.157")
    spi.setIpFinder(ipFinder)
    cfg.setDiscoverySpi(spi)
    val ignite: Ignite = Ignition.start(cfg)
    ignite.getOrCreateCache[String,String]("DebugNodes")
  }


}
