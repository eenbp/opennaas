/**
 * This file was auto-generated by mofcomp -j version 1.0.0 on Wed Jan 12 
 * 09:21:06 CET 2011. 
 */

package net.i2cat.mantychore.model;

import java.io.*;

/**
 * This Class contains accessor and mutator methods for all properties defined 
 * in the CIM class AssociatedNextHop as well as methods comparable to the 
 * invokeMethods defined for this class. This Class implements the 
 * AssociatedNextHopBean Interface. The CIM class AssociatedNextHop is 
 * described as follows: 
 * 
 * AssociatedNextHop depicts the relationship between a route and the 
 * specification of its next hop. The next hop is external to a System, and 
 * hence is defined as a kind of RemoteServiceAccessPoint. Note that this 
 * relationship is independent of RouteUsesEndpoint (the local Endpoint used 
 * to transmit the traffic), and both may be defined for a route. 
 */
public class AssociatedNextHop extends Dependency implements Serializable {

    /**
     * This constructor creates a AssociatedNextHopBeanImpl Class which 
     * implements the AssociatedNextHopBean Interface, and encapsulates the 
     * CIM class AssociatedNextHop in a Java Bean. The CIM class 
     * AssociatedNextHop is described as follows: 
     * 
     * AssociatedNextHop depicts the relationship between a route and the 
     * specification of its next hop. The next hop is external to a System, 
     * and hence is defined as a kind of RemoteServiceAccessPoint. Note that 
     * this relationship is independent of RouteUsesEndpoint (the local 
     * Endpoint used to transmit the traffic), and both may be defined for a 
     * route. 
     */
    public AssociatedNextHop(){};
    /**
     * This method create an Association of the type AssociatedNextHop between 
     * one RemoteServiceAccessPoint object and NextHopRoute object 
     */
    public static AssociatedNextHop link(RemoteServiceAccessPoint 
	antecedent,NextHopRoute dependent){

    return (AssociatedNextHop) 
	Association.link(AssociatedNextHop.class,antecedent,dependent);
    }//link

} // Class AssociatedNextHop