package net.i2cat.mantychore.action.junos.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.gretunnel.GetTunnelConfigurationAction;
import net.i2cat.mantychore.actionsets.junos.actions.test.ActionTestHelper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NextHopRoute;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.Service;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;

public class GetGRETunnelActionTest {

	private static GetTunnelConfigurationAction	action;
	private static Log							log	= LogFactory.getLog(GetGRETunnelActionTest.class);
	static ActionTestHelper						helper;
	static ProtocolSessionManager				protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new GetTunnelConfigurationAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsGRETunnelService());
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void TestActionID() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.GETTUNNELCONFIG, action.getActionID());
	}

	@Test
	public void paramsTest() {
		Object params = action.getParams();
		Assert.assertNotNull("Params can't be null", params);
		Assert.assertTrue("Params must be GRETunnelService.", params instanceof GRETunnelService);

	}

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Not accepted param", "/VM_files/getconfiguration.vm", action.getTemplate());
	}

	private void printTest(net.i2cat.mantychore.model.System routerModel) {

		List<LogicalDevice> ld = routerModel.getLogicalDevices();

		log.info("Logical devices: " + ld.size());

		for (LogicalDevice device : ld) {
			LogicalPort lp = (LogicalPort) device;
			if (device instanceof LogicalTunnelPort) {
				LogicalTunnelPort lt = (LogicalTunnelPort) device;
				log.info("LogicalTunnelPort: " + lt.getName());
				log.info("Peer unit " + lt.getPeer_unit());
				log.info("Unit " + lt.getPortNumber());
			} else if (device instanceof EthernetPort) {
				EthernetPort ep = (EthernetPort) device;
				log.info("EthernetPort: " + ep.getName());
				log.info("Unit " + ep.getPortNumber());
			} else {
				log.info("No such class considered ");
			}
			for (ProtocolEndpoint p : lp.getProtocolEndpoint()) {
				if (p instanceof IPProtocolEndpoint) {
					IPProtocolEndpoint ip = (IPProtocolEndpoint) p;
					log.info(ip.getIPv4Address());
					log.info(ip.getSubnetMask());
				} else {
					VLANEndpoint vlan = (VLANEndpoint) p;
					log.info("VLAN " + vlan.getVlanID());
				}
			}
		}

		log.info("Found " + routerModel.getChildren().size() + " logical resources.");
		for (Object systemElement : routerModel.getChildren()) {
			log.info(systemElement);
			if (systemElement instanceof ComputerSystem) {
				ComputerSystem logicalrouter = (ComputerSystem) systemElement;
				// check that the element is a Logical Router
				log.info(logicalrouter.getName());
			}
		}

		for (Service service : routerModel.getHostedService()) {
			if (service instanceof GRETunnelService) {
				log.info(" - GRE Tunnel");
				GRETunnelService greService = (GRETunnelService) service;
				log.info("Interface :" + greService.getName());
				GRETunnelConfiguration greConf = greService.getGRETunnelConfiguration();
				log.info("Source : " + greConf.getSourceAddress());
				log.info("Destination : " + greConf.getDestinationAddress());
				for (ProtocolEndpoint pE : greService.getProtocolEndpoint()) {
					GRETunnelEndpoint gE = (GRETunnelEndpoint) pE;
					if (gE.getIPv4Address() == null)
						log.info("ipv6 : " + gE.getIPv6Address());
					else
						log.info("ipv4 : " + gE.getIPv4Address());

					for (NextHopRoute nextHop : gE.getNextHopRoutes()) {
						log.info("next-hop route for: " + nextHop.getDestinationAddress());
					}
				}
			}
		}

	}

	@Test
	public void testExecute() throws ActionException {

		ActionResponse response = action.execute(protocolsessionmanager);

		net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) action.getModelToUpdate();
		Assert.assertNotNull(routerModel);
		printTest(routerModel);
	}

	/**
	 * Simple parser. It was used for proves with xml files
	 * 
	 * @param stream
	 * @return
	 */
	public String readStringFromFile(String pathFile) throws Exception {
		String answer = null;
		// InputStream inputFile =
		// ClassLoader.getSystemResourceAsStream(pathFile);
		InputStream inputFile = getClass().getResourceAsStream(pathFile);
		InputStreamReader streamReader = new InputStreamReader(inputFile);
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(streamReader);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		answer = fileData.toString();

		return answer;
	}

}