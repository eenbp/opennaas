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
class Link implements Serializable
    {
        public int id;
        public int node1Id;
        public int node2Id;
        public int capacity;
        //public int availableCapacity;        
        public int delay;

        public Link()
        {
            id = -1;
        }
         
    }

class Location implements Serializable
    {
        public int row;
	public int cell;
    }
class VEnvironment implements Serializable
    {
        public int vType;
        public int vEnv;
    }
class Path implements Serializable
    {
        public int id;
        public int node1Id;
        public int node2Id;
        public ArrayList<Link> links=new ArrayList<Link>();
        public int capacity;
        public int maxCapacity;
        public int delay;

        ///// method to calculate the capacity and delay of a path based on the included links
        public void findPathCapacityAndDelay(List<List<Link>> connections)
        {
                int minCapacity, delay;
	        minCapacity=1000000;delay=0;
	        maxCapacity=0;
	        int n1,n2,n3;
	        for(int i=0;i<(int)this.links.size();i++)
	           {
                          
                       n1 = this.links.get(i).node1Id; n2 = this.links.get(i).node2Id; if (n2 < n1) { n3 = n2; n2 = n1; n1 = n3; }
		          if(connections.get(n1).get(n2).capacity<minCapacity)
			      minCapacity=connections.get(n1).get(n2).capacity;

		          if(connections.get(n1).get(n2).capacity>maxCapacity)
			           maxCapacity=connections.get(n1).get(n2).capacity;

                   delay=delay+links.get(i).delay;
	          }
	        this.capacity=minCapacity;

	        this.maxCapacity=maxCapacity;
	        this.delay=delay;
        }
        
       /// method to add a link to a path
        public Path addLinkToPath(Link l)
        {
            Path res = new Path(); 
            int size = this.links.size();
            
            for (int i = 0; i < (int)this.links.size(); i++)
                res.links.add(i, this.links.get(i)) ;

            res.links.add(size,l);
            //res->findPathCapacityAndDelay();
            return res;
        }
    }


class PNode implements Serializable
    {
        public int id;
        public int capacity;        
        //public int availableCapacity;
        public int pathNum;
        public Location location=new Location();
        public VEnvironment env = new VEnvironment();                
        public HashSet services=new HashSet();
        
    }

/// the next class is used to store a list of paths between two nodes
class Paths
    {
        public int node1Id;
        public int node2Id;
        public ArrayList<Path> paths=new ArrayList<Path>();
    }

/// the next class is used to store information during the search for a path (SPF)
class pathCell
    {
        public int nodeId;
        public int linkNum;
        public int delay;
        public pathCell prev;
    }

/// the next class is used to store information during the search for a path (LB)
 class pathCell2
    {
        public int nodeId;
        public int linkNum;
        public int delay;
        public int prev;
        public int remaining;  // remaining avaialble bandwidth
        public int passed;
        public pathCell2()
        {
            this.remaining = -1;
            this.prev = -1;
            this.linkNum = 0;
            this.delay = 0;
            this.passed = 0;
        }
    }


public class InPNetwork implements Serializable
    {
        public ArrayList<ArrayList<Link>> connections=new ArrayList<ArrayList<Link>>();
        public ArrayList<PNode> nodes=new ArrayList<PNode>();
        public ArrayList<Link> links=new ArrayList<Link>();

        public ArrayList<ArrayList<Paths>> allPaths=new ArrayList<ArrayList<Paths>>();
        public ArrayList<ArrayList> locations=new ArrayList<ArrayList>();
        public int nodeNum;

  
  public void printNetwork()
        {
            System.out.println();
            System.out.println("Nodes:");
            System.out.println(nodeNum);
            for (int i = 0; i < nodeNum; i++)
            {
                System.out.println("node " + nodes.get(i).id + "--" + nodes.get(i).capacity + "--X--" + nodes.get(i).location.row + "--Y--" + nodes.get(i).location.cell );
                
            }
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("links size" + links.size());
            for (int i = 0; i < links.size(); i++)
            {
                System.out.println("link : " + links.get(i).node1Id + "--" + links.get(i).node2Id + " : " + links.get(i).capacity + " , " + links.get(i).delay);
            }
        }
  
  //// generate random physical network///
  public InPNetwork generateNetwork(int nodeMin, int nodeMax, int minCapacity, int maxCapacity, double servDegree, int minLinkCapacity, int maxLinkCapacity, int minLinkDelay, int maxLinkDelay, double linkProbability, Random random)
        {
            InPNetwork res=new InPNetwork();             
            res.nodeNum=Global.getNodeNum(nodeMin,nodeMax,random);
            
           for(int i=0;i<res.nodeNum;i++)
             {
              PNode n=new PNode();
              n.id=i;
              n.location.row=Global.getRandInt(1, Global.rowNum, random);
              n.location.cell=Global.getRandInt(1, Global.cellNum, random);             
              n.capacity=Global.getRandInt(minCapacity, maxCapacity, random);;
              //n.capacity=maxCapacity;              
              n.pathNum=0;
              res.nodes.add(n);
           }

           for (int i = 0; i < res.nodeNum; i++) {
               res.connections.add(new ArrayList<Link>());    		  
    	    }

          int linksNum=0;
                    
          for (int i = 0; i < res.nodeNum; i++) {
    	    for (int j = 0; j < res.nodeNum; j++) {
              
              if(i<j)
                {
                 
        	   double d1=(double)random.nextDouble();
        	   if(d1<=linkProbability)
        	     {
                      Link l=new Link();
                      l.id=1;
                      l.node1Id=i;
                      l.node2Id=j;
                      //l.capacity=maxLinkCapacity;
                      l.capacity=Global.getRandInt(minLinkCapacity, maxLinkCapacity, random);;
                      l.delay=Global.getRandInt(minLinkDelay, maxLinkDelay, random);
                      res.connections.get(i).add(l);                      
                      res.links.add(l);                      
                      linksNum++;
        	     }
                   else
                      res.connections.get(i).add(new Link()); 
               }
              else
                 res.connections.get(i).add(new Link()); 

    	   }
       }     
         return res;
      }
      
  ///// get the adjacent nodes to a node
        public ArrayList getAdjacentNodes(int nodeId)
        {
            ArrayList res=new ArrayList();           
            for (int i = 0; i < (int)this.links.size(); i++)
            {
                if (links.get(i).node1Id == nodeId)
                {                    
                    res.add(links.get(i).node2Id);
                }
                if (links.get(i).node2Id == nodeId)
                {                   
                    res.add(links.get(i).node1Id);
                }
            }
            return res;
        }
        
        //// get the index of a physical link
        public int getLinkIndex(int node1, int node2)
        {
            int res=-1; int n1, n2;
            if (node1 < node2) { n1 = node1; n2 = node2; }
            else { n1 = node2; n2 = node1; }

            for (int i = 0; i < (int)this.links.size(); i++)
            {
                if ((this.links.get(i).node1Id == n1) && (this.links.get(i).node2Id == n2))
                {
                    res = i;
                }
            }
            return res;
        }

        /// find the shortest path that satisfy the virtual link requirement
        public Path findPathBetweemTwoNodes(int src, int dst, int requiredCapacity, int requiredDelay, int maxLinkNum)
        {
            ArrayList<pathCell> passedNodes=new ArrayList<pathCell>();            
            pathCell p = new pathCell();
            p.nodeId = src;
            p.prev = new pathCell();
            p.prev.nodeId = -1;
            Path res = new Path();
            res.id = -1;
            passedNodes.add(p);
            int currentPassedNodeIndex = 0;
            int stop = 0;
            int n,n1,n2;
            int max = 100;
            int t = 0;
            while ((stop != 1)&&(t<max))
            {
                t++;
                if (passedNodes.get(currentPassedNodeIndex).linkNum < maxLinkNum)
                {
                    ArrayList adjacentNodes = this.getAdjacentNodes(passedNodes.get(currentPassedNodeIndex).nodeId);
                    
                    for (int i = 0; i < (int)adjacentNodes.size(); i++)
                    {
                        /// choosing next adjacent node to continue
                        n = Integer.valueOf(adjacentNodes.get(i).toString());
                        
                        if ((n != passedNodes.get(currentPassedNodeIndex).prev.nodeId) && (n != src))
                        {
                            if (passedNodes.get(currentPassedNodeIndex).nodeId < n) { n1 = passedNodes.get(currentPassedNodeIndex).nodeId; n2 = n; }

                            else { n2 = passedNodes.get(currentPassedNodeIndex).nodeId; n1 = n; }

                            if ((this.connections.get(n1).get(n2).capacity >= requiredCapacity) && (this.connections.get(n1).get(n2).delay + passedNodes.get(currentPassedNodeIndex).delay <= requiredDelay))
                            {
                                
                                if (n == dst)
                                {
                                    
                                    stop = 1; res.id = 1;
                                    res.node1Id = src;
                                    res.node2Id = dst;
                                    res.capacity = requiredCapacity;
                                    
                                    res.delay = passedNodes.get(currentPassedNodeIndex).delay + this.connections.get(n1).get(n2).delay;
                                    //// now construcing the resulted links
                                    int current;
                                    int prev;
                                    current = n;
                                    pathCell prevCell = new pathCell();
                                    prevCell = passedNodes.get(currentPassedNodeIndex);
                                    prev = passedNodes.get(currentPassedNodeIndex).nodeId;
                                    
                                    for (int j = passedNodes.get(currentPassedNodeIndex).linkNum; j >= 0; j--)
                                    {                                        
                                        if (current < prev)
                                        {
                                            res.links.add(this.connections.get(current).get(prev));                                            
                                        }
                                        else
                                        {
                                            res.links.add(this.connections.get(prev).get(current));                                            
                                        }

                                        if (j != 0)
                                        {
                                            current = prev;
                                            prevCell = prevCell.prev;
                                            prev = prevCell.nodeId;
                                        }
                                    }
                                }
                                else
                                {
                                    if ((passedNodes.get(currentPassedNodeIndex).linkNum + 1 < maxLinkNum) && (stop != 1))
                                    {
                                        /// now check if is passed before//
                                        pathCell c = new pathCell();
                                        c.nodeId = n;
                                        c.linkNum = passedNodes.get(currentPassedNodeIndex).linkNum + 1;
                                        c.delay = this.connections.get(n1).get(n2).delay + passedNodes.get(currentPassedNodeIndex).delay;
                                        c.prev = new pathCell();
                                        c.prev = passedNodes.get(currentPassedNodeIndex);
                                        passedNodes.add(c);
                                    }
                                }
                            }
                        }
                    }
                }
                currentPassedNodeIndex++;
                if (currentPassedNodeIndex == (int)passedNodes.size()) stop = 1;
            }

            passedNodes.clear();
            return res;
        }

        /// find the path that satisfy the virtual link requirement considering load balancing
        public Path findPathBetweemTwoNodes2(int src, int dst, int requiredCapacity, int requiredDelay, int maxLinkNum)
        {
            Path res = new Path();
            res.id = -1;
            ArrayList<pathCell2> cells=new ArrayList<pathCell2>();            
            for (int i = 0; i < this.nodeNum;i++ )
            {
                cells.add(new pathCell2());
            }
            ArrayList adjacentNodes=new ArrayList();
            int curNode = src; int n, n1, n2;
            cells.get(curNode).passed = 1;
            cells.get(curNode).linkNum = 0;
            int stop = 0;
            while ((curNode != dst) && (stop != 1))
            {
                adjacentNodes = this.getAdjacentNodes(curNode);
                cells.get(curNode).passed = 1;
                for (int i = 0; i < (int)adjacentNodes.size(); i++)
                {
                    n = Integer.valueOf(adjacentNodes.get(i).toString());
                    
                    if (curNode < n) { n1 = curNode; n2 = n; }

                    else { n2 = curNode; n1 = n; }
                    if ((cells.get(curNode).linkNum + 1 < maxLinkNum) || (n == dst))
                        if ((this.connections.get(n1).get(n2).capacity >= requiredCapacity) && (this.connections.get(n1).get(n2).delay + cells.get(curNode).delay <= requiredDelay))
                        {
                            if ((cells.get(n).passed != 1) && (cells.get(n).remaining < this.connections.get(n1).get(n2).capacity - requiredCapacity))
                            {
                                cells.get(n).remaining = this.connections.get(n1).get(n2).capacity - requiredCapacity;
                                cells.get(n).delay = this.connections.get(n1).get(n2).delay + cells.get(curNode).delay;
                                cells.get(n).linkNum = cells.get(curNode).linkNum + 1;
                                cells.get(n).prev = curNode;
                            }
                        }
                }
                //// get current node with the maximum remaining
                int max = -1;
                for (int i = 0; i < cells.size(); i++)
                {
                    if ((cells.get(i).remaining > max) && (cells.get(i).passed == 0))
                    {
                        max = cells.get(i).remaining;
                        curNode = i;
                    }
                }
                if (max == -1) stop = 1;
            }
            ////////////
            if (stop != 1)
            {

                res.node1Id = src; res.id = 1;
                res.node2Id = dst;
                res.capacity = requiredCapacity;
                res.delay = cells.get(dst).delay;
                int current = dst;
                int prev = cells.get(dst).prev;
                for (int j = cells.get(dst).linkNum - 1; j >= 0; j--)
                {
                    if (current < prev)
                        res.links.add(this.connections.get(current).get(prev));
                    else
                        res.links.add(this.connections.get(prev).get(current));

                    if (j != 0)
                    {
                        current = prev;

                        prev = cells.get(current).prev;
                    }
                }
            }            
            return res;
        }      
        
        
        /////
        public void printPNetworkToXMLFile(String fileName)
        {
            try {
 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("PNetwork");
		doc.appendChild(rootElement);
                for(int i=0;i<nodes.size();i++)
                {
		   Element node = doc.createElement("Node");
		   rootElement.appendChild(node);
                   
		   Element id = doc.createElement("id");
		   id.appendChild(doc.createTextNode(""+i));
		   node.appendChild(id);
                   
                   Element capacity = doc.createElement("capacity");
		   capacity.appendChild(doc.createTextNode(""+nodes.get(i).capacity));
		   node.appendChild(capacity);
                   
                   Element x = doc.createElement("x");
		   x.appendChild(doc.createTextNode(""+nodes.get(i).location.row));
		   node.appendChild(x);
                   
                   Element y = doc.createElement("y");
		   y.appendChild(doc.createTextNode(""+nodes.get(i).location.cell));
		   node.appendChild(y);
 
                }
                
                for(int i=0;i<links.size();i++)
                {
                   Element link = doc.createElement("Link");
		   rootElement.appendChild(link);
                   
		   Element node1 = doc.createElement("node1");
		   node1.appendChild(doc.createTextNode(""+links.get(i).node1Id));
		   link.appendChild(node1);
                   
                   Element node2 = doc.createElement("node2");
		   node2.appendChild(doc.createTextNode(""+links.get(i).node2Id));
		   link.appendChild(node2);
                   
                   Element capacity = doc.createElement("capacity");
		   capacity.appendChild(doc.createTextNode(""+links.get(i).capacity));
		   link.appendChild(capacity);
                   
                   Element delay = doc.createElement("delay");
		   delay.appendChild(doc.createTextNode(""+links.get(i).delay));
		   link.appendChild(delay);
                   
                   
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
        
        public InPNetwork readPNetworkFromXMLFile(String fileName)
        {
            InPNetwork res=new InPNetwork();
            
            try {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(fileName);
            if (file.exists()) {
                Document doc = db.parse(file);
                Element docEle = doc.getDocumentElement();
 
                NodeList theNodes = docEle.getElementsByTagName("Node");
                
                if (theNodes != null && theNodes.getLength() > 0) {
                    for (int i = 0; i < theNodes.getLength(); i++) {
 
                        Node node = theNodes.item(i);
 
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            
                            PNode n=new PNode();     
                            Element e = (Element) node;
                            NodeList nodeList = e.getElementsByTagName("id");
                            n.id=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            
                             nodeList = e.getElementsByTagName("capacity");
                            n.capacity=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            nodeList = e.getElementsByTagName("x");
                            n.location.row=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            nodeList = e.getElementsByTagName("y");
                            n.location.cell=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());    
                            n.pathNum=0;
                            res.nodes.add(n);   
                            res.nodeNum++;
              
                        }
                    }
                } else {
                    System.exit(1);
                }
                
              
                for (int i = 0; i < res.nodeNum; i++) {
                    res.connections.add(new ArrayList<Link>());  
    	          for (int j = 0; j < res.nodeNum; j++) {
                        res.connections.get(i).add(new Link()); 
    	           }
                }
                
                NodeList theLinks = docEle.getElementsByTagName("Link");
  
                if (theLinks != null && theLinks.getLength() > 0) {
                    for (int i = 0; i < theLinks.getLength(); i++) {
 
                        Node link = theLinks.item(i);
 
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
                                                 
                            res.links.add(res.connections.get(node1).get(node2));                     
                            
                               
              
                        }
                    }
                } else {
                    System.exit(1);
                }
                
                
             } else System.out.println("00000000:");
           } catch (Exception e) {
            System.out.println(e);
          }
        
            
            return res;
        }
        
    }
