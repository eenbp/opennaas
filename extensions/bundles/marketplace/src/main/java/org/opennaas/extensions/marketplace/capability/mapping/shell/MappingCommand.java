package org.opennaas.extensions.marketplace.capability.mapping.shell;

import org.opennaas.core.resources.capability.ICapability;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.marketplace.capability.mapping.MappingCapability;
import org.opennaas.extensions.marketplace.capability.mapping.IMappingCapability;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.NetworkModel;
import java.util.List;
//import org.opennaas.extensions.sampleresource.capability.example.ExampleCapability;
/**
 * 
 * @author Elisabeth Rigol
 * 
 */
@Command(scope = "mapping", name = "mapVN", description = "Tt will map the VN request.")
public class MappingCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "VNRequest", description = "The name of the person we will greet.", required = true, multiValued = false)
	private String	VNRequest; 

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("mapVN");
		try {

			IResource resource = getResourceFromFriendlyName(resourceName);

            //            List<IMappingCapability> allCapabs = (List<IMappingCapability>) resource.getCapabilities();
            //            printInfo(resourceName + " capabilites : " + allCapabs.size());
             //           printInfo(resourceName + " capabilites : " + allCapabs.size());
             //           printInfo(resourceName + " capabilites : " + allCapabs.size());
             //           printInfo(resourceName + " capabilites : " + allCapabs.size());
                        //ExampleCapability m=(ExampleCapability)allCapabs.get(0);
                        //printInfo(resourceName + " capabilites : " + m.CAPABILITY_TYPE);

			MappingCapability capab = (MappingCapability) resource.getCapabilityByType("mapping");
			String greeting = capab.mapVN(VNRequest);
			printInfo(resourceName + " maps : " + greeting);
			
			////
                        ResourceManager resourcemng=(ResourceManager)getResourceManager();
			List<IResource> res=resourcemng.listResourcesByType("router");
			printInfo(" router resources num : " + res.size());
/*
                        for(int i=0;i<res.size();i++)
                         {

                           printInfo(" router virtual : " + res.get(i).getResourceDescriptor().getProperties().get("virtual"));
                           printInfo(" children : " + res.get(i).getModel().getChildren().size());
                           if(res.get(i).getResourceDescriptor().getProperties().get("virtual")!="true")
                            {
                               printInfo(" 222children : " + res.get(i).getModel().getChildren().size());
                            }
                         }*/

                       List<IResource> res1=resourcemng.listResourcesByType("network");
                       List<Link> links=NetworkModelHelper.getLinks((NetworkModel)res1.get(0).getModel());
                       printInfo(" network links num : " + links.size());
                       for(int i=0;i<links.size();i++)
                         {
                            printInfo(" link : " + links.get(i).getSource().getName());
                            printInfo(" link : " + links.get(i).getSource().getDevice());
                         }
			////
		} catch (Exception e) {
			printError("Error mapping the request " + resourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
