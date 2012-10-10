/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.marketplace.capability.mapping;

import java.io.*;
import java.util.*;
import java.util.HashSet;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author ahammaa
 */
class VLink
    {
        public int id;
        public int node1Id;
        public int node2Id;
        public int capacity;
        public int delay;
        public VLink()
        {
            id = -1;
        }
    }

class VNode
    {
        public int id;
        public int capacity;
        public int distance;
        public Location location=new Location();
        public VEnvironment env=new VEnvironment();
    }

class VNLifetime
    {
        public int vnID;
        public int arrivalTime;
        public int releasingTime;
        public int providerID;
        public MappingResult mres=new MappingResult();
        public int waitingForReleasing = -1;
        public VNLifetime(int id, int arrival, int life)
        {
            vnID = id;
            arrivalTime=arrival;
            releasingTime = life;
            waitingForReleasing = -1;
        }
    }

public class VNTRequest
    {
        public ArrayList<ArrayList<VLink>> connections=new ArrayList<ArrayList<VLink>>();
        public ArrayList<VNode> vnodes=new ArrayList<VNode>();
        public ArrayList<VLink> vlinks=new ArrayList<VLink>();
        public int startDay;
        public int endDay;
        ////
        public int mappingCost;
        public int imrovedMappingCost;
        public int mappingRevenue;
        ////
        public ArrayList<ArrayList> locations=new ArrayList<ArrayList>();
        public int vnodeNum;
        public HashSet services=new HashSet();        

        public void printVNTNetwork()
        {
            
            System.out.println("Nodes:");
            System.out.println(vnodeNum);            
            for (int i = 0; i < vnodeNum; i++)
            {
                System.out.println("node " + vnodes.get(i).id + "--" + vnodes.get(i).capacity + "--X--" + vnodes.get(i).location.row + "--Y--" + vnodes.get(i).location.cell + "--D--" + vnodes.get(i).distance);
                
            }
            System.out.println("links size" + vlinks.size());
            for (int i = 0; i < vlinks.size(); i++)
            {
                System.out.println("link : " + vlinks.get(i).node1Id + "--" + vlinks.get(i).node2Id + " : " + vlinks.get(i).capacity + " , " + vlinks.get(i).delay);                
            }
        }
        
        /// generate randon VNT request
        public VNTRequest generateVNRequest(int nodeMin, int nodeMax, int minCapacity, int maxCapacity, int minLinkCapacity, int maxLinkCapacity, int minLinkDelay, int maxLinkDelay, double linkProbability,int minDistance,int maxDistance, Random random)
        {
            VNTRequest res=new VNTRequest();       
	    
            res.vnodeNum=Global.getNodeNum(nodeMin,nodeMax,random);
            
           for(int i=0;i<res.vnodeNum;i++)
             {
              VNode n=new VNode();
              n.id=i;
              n.location.row=Global.getRandInt(1,Global.rowNum,random);
              n.location.cell=Global.getRandInt(1,Global.cellNum,random);
              n.distance=Global.getRandInt(minDistance,maxDistance,random);
              n.capacity=Global.getRandInt(minCapacity,maxCapacity,random);
              
              res.vnodes.add(n);
           }
          
           for (int i = 0; i < res.vnodeNum; i++) {
               res.connections.add(new ArrayList<VLink>());    		  
    	    }

          int linksNum=0;
                    
          for (int i = 0; i < res.vnodeNum; i++) {
    	    for (int j = 0; j < res.vnodeNum; j++) {
              
              if(i<j)
                {
                 
        	   double d1=(double)random.nextDouble();
        	   if(d1<=linkProbability)
        	    {
                      VLink l=new VLink();
                      l.id=1;
                      l.node1Id=i;
                      l.node2Id=j;
                      l.capacity=Global.getRandInt(minLinkCapacity,maxLinkCapacity,random);                      
                      l.delay=Global.getRandInt(minLinkDelay,maxLinkDelay,random);;
                      res.connections.get(i).add(l);                      
                      res.vlinks.add(l);                      
                      linksNum++;
        	     }
                       else
                          res.connections.get(i).add(new VLink()); 
               }
              else
                 res.connections.get(i).add(new VLink()); 
    	   }
       }     
         return res;
      }
             
        /// get the adjacent vnodes to a vnode
        public ArrayList getAdjacentVNodes(int nodeId)
        {
            ArrayList res=new ArrayList();            
            for (int i = 0; i < (int)this.vlinks.size(); i++)
            {
                if (vlinks.get(i).node1Id == nodeId)
                {
                    res.add(vlinks.get(i).node2Id);
                }
                if (vlinks.get(i).node2Id == nodeId)
                {
                    res.add(vlinks.get(i).node1Id);
                }
            }
            return res;
        }
        
        public static void printVNTsToXMLFile(String fileName, ArrayList<VNTRequest> vnts)
        {
            try {
 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("VNRequests");
		doc.appendChild(rootElement);
                for(int i=0;i<vnts.size();i++)
                {     Element vn = doc.createElement("VN"+i);
		      rootElement.appendChild(vn);
                   for(int k=0;k<vnts.get(i).vnodes.size();k++)
                    {
		      Element node = doc.createElement("VNode");
		      vn.appendChild(node);
                   
		      Element id = doc.createElement("id");
		      id.appendChild(doc.createTextNode(""+k));
		      node.appendChild(id);
                   
                      Element capacity = doc.createElement("capacity");
		      capacity.appendChild(doc.createTextNode(""+vnts.get(i).vnodes.get(k).capacity));
		      node.appendChild(capacity);
                   
                      Element x = doc.createElement("x");
		      x.appendChild(doc.createTextNode(""+vnts.get(i).vnodes.get(k).location.row));
		      node.appendChild(x);
                   
                      Element y = doc.createElement("y");
		      y.appendChild(doc.createTextNode(""+vnts.get(i).vnodes.get(k).location.cell));
		     node.appendChild(y);
                     
                     Element d = doc.createElement("d");
		      d.appendChild(doc.createTextNode(""+vnts.get(i).vnodes.get(k).distance));
		     node.appendChild(d);
 
                 }
                
                for(int k=0;k<vnts.get(i).vlinks.size();k++)
                {
                   Element link = doc.createElement("VLink");
		   vn.appendChild(link);
                   
		   Element node1 = doc.createElement("node1");
		   node1.appendChild(doc.createTextNode(""+vnts.get(i).vlinks.get(k).node1Id));
		   link.appendChild(node1);
                   
                   Element node2 = doc.createElement("node2");
		   node2.appendChild(doc.createTextNode(""+vnts.get(i).vlinks.get(k).node2Id));
		   link.appendChild(node2);
                   
                   Element capacity = doc.createElement("capacity");
		   capacity.appendChild(doc.createTextNode(""+vnts.get(i).vlinks.get(k).capacity));
		   link.appendChild(capacity);
                   
                   Element delay = doc.createElement("delay");
		   delay.appendChild(doc.createTextNode(""+vnts.get(i).vlinks.get(k).delay));
		   link.appendChild(delay);
                                   
                }
                
                }
                ////
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileName));
 
		
		transformer.transform(source, result);
 
 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
        
        }
        
        public VNTRequest readVNTRequestFromXMLFile(String fileName,int vnID)
        {
            VNTRequest res=new VNTRequest();
            
            try {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(fileName);
            if (file.exists()) {
                Document doc = db.parse(file);
                Element docEle = doc.getDocumentElement();
 
                NodeList theVN = docEle.getElementsByTagName("VN"+vnID);
                
                if (theVN != null && theVN.getLength() > 0) {
                    Node vn = theVN.item(0);
                    Element element = (Element) vn;
                    NodeList theVNodes = element.getElementsByTagName("VNode");
                    if (theVNodes != null && theVNodes.getLength() > 0) {
                    for (int i = 0; i < theVNodes.getLength(); i++) {
 
                        Node node = theVNodes.item(i);
 
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            
                            VNode n=new VNode();     
                            Element e = (Element) node;
                            NodeList nodeList = e.getElementsByTagName("id");
                            n.id=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            
                            nodeList = e.getElementsByTagName("capacity");
                            n.capacity=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            nodeList = e.getElementsByTagName("x");
                            n.location.row=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            nodeList = e.getElementsByTagName("y");
                            n.location.cell=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());    
                            nodeList = e.getElementsByTagName("d");
                            n.distance=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());   
                            
                            res.vnodes.add(n);   
                            res.vnodeNum++;
              
                        }
                    }
                 }
                    
                    for (int i = 0; i < res.vnodeNum; i++) {
                        res.connections.add(new ArrayList<VLink>());  
    	             for (int j = 0; j < res.vnodeNum; j++) {
                         res.connections.get(i).add(new VLink()); 
    	           }
                }
                    
                    NodeList theVLinks = element.getElementsByTagName("VLink");
                    
                    if (theVLinks != null && theVLinks.getLength() > 0) {
                    for (int i = 0; i < theVLinks.getLength(); i++) {
 
                        Node link = theVLinks.item(i);
 
                        if (link.getNodeType() == Node.ELEMENT_NODE) {
                            
                            Element e = (Element) link;
                            
                            NodeList nodeList = e.getElementsByTagName("node1");
                            int node1=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            nodeList = e.getElementsByTagName("node2");
                            int node2=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            
                                                      
                            res.connections.get(node1).get(node2).id=1;
                            
                            res.connections.get(node1).get(node2).node1Id=node1;
                            
                            res.connections.get(node1).get(node2).node2Id=node2;
                            
                            nodeList = e.getElementsByTagName("capacity");
                            res.connections.get(node1).get(node2).capacity=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            nodeList = e.getElementsByTagName("delay");
                            res.connections.get(node1).get(node2).delay=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());;
                                                 
                            res.vlinks.add(res.connections.get(node1).get(node2));                     
                            
                               
              
                        }
                    }
                   }
                    
                }
            }
            
            
            
            }catch (Exception e) {
            System.out.println(e);
          }
            
            return res;
        }
   
    }
