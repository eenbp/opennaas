/**
 * This file was auto-generated by mofcomp -j version 1.0.0 on Wed Jan 12 
 * 09:21:06 CET 2011. 
 */

package net.i2cat.mantychore.model;

import java.io.*;

/**
 * This Class contains accessor and mutator methods for all properties defined 
 * in the CIM class ContainedLocation as well as methods comparable to the 
 * invokeMethods defined for this class. This Class implements the 
 * ContainedLocationBean Interface. The CIM class ContainedLocation is 
 * described as follows: 
 * 
 * ContainedLocation defines one location in the context of another. For 
 * example, a campus might 'contain' a building, which in turn 'contains' a 
 * floor. 
 */
public class ContainedLocation extends Component implements Serializable {

    /**
     * This constructor creates a ContainedLocationBeanImpl Class which 
     * implements the ContainedLocationBean Interface, and encapsulates the 
     * CIM class ContainedLocation in a Java Bean. The CIM class 
     * ContainedLocation is described as follows: 
     * 
     * ContainedLocation defines one location in the context of another. For 
     * example, a campus might 'contain' a building, which in turn 'contains' 
     * a floor. 
     */
    public ContainedLocation(){};
    /**
     * This method create an Association of the type ContainedLocation between 
     * one Location object and Location object 
     */
    public static ContainedLocation link(Location groupComponent,Location 
	partComponent){

    return (ContainedLocation) 
	Association.link(ContainedLocation.class,groupComponent,partComponent);
    }//link

} // Class ContainedLocation