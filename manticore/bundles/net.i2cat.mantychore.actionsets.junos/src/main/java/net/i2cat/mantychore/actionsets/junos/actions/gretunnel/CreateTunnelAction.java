package net.i2cat.mantychore.actionsets.junos.actions.gretunnel;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.ManagedElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class CreateTunnelAction extends JunosAction {

	private static final String	NAME_PATTERN	= "gre.([\\d{1}&&[^0]])(\\d*)";

	private final Log			log				= LogFactory.getLog(CreateTunnelAction.class);

	public CreateTunnelAction() {
		super();
		initialize();
	}

	public void initialize() {
		this.setActionID(ActionConstants.CREATETUNNEL);
		setTemplate("/VM_files/createTunnel.vm");
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {
			// not only check the params also it change the velocity template according to the interface
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
			command.initialize();
			actionResponse.addResponse(sendCommandToProtocol(command, protocol));

		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
		validateAction(actionResponse);

	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// Nothing to do
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null)
			throw new ActionException("Params can't be null for the " + getActionID() + " action.");
		if (!(params instanceof GRETunnelService))
			throw new ActionException(getActionID() + " only accept GRE Tunnel Services as params.");
		if (!checkNamePattern(((GRETunnelService) params).getName()))
			throw new ActionException("The name of the GRE Tunnel must have the following format gre.[1..n]");
		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {

		checkParams(params);
		try {
			IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("ipUtilsHelper", ipUtilsHelper);

			String name = ((GRETunnelService) params).getName();
			String portNumber = name.split("\\.")[1];
			name = name.split("\\.")[0];
			((GRETunnelService) params).setName(name);
			extraParams.put("portNumber", portNumber);

			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
				// TODO If we don't have a ManagedElement initialized
			} else if (params != null && params instanceof ManagedElement && ((ManagedElement) params).getElementName() == null) {
				((ManagedElement) params).setElementName("");

			}
			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Check if name has this patter gre.{x} where x >= 1
	 * 
	 * @param name
	 * @return true if name has the pattern
	 */
	private boolean checkNamePattern(String name) {
		return name.matches(NAME_PATTERN);
	}

}