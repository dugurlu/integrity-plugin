package hudson.scm;

import hudson.FilePath;
import hudson.model.*;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.Hudson.MasterComputer;
import hudson.slaves.SlaveComputer;


import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FVOReport {

	public void generateXML (FilePath workspace, List<Hashtable<CM_PROJECT, Object>> projectMembersList, String projectConfigPath) {
	
	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("report");
		// set the project config path as attribute
	    Attr attr = doc.createAttribute("config_path");
	    attr.setValue(projectConfigPath);
	    rootElement.setAttributeNode(attr);
		
		doc.appendChild(rootElement);

		Element member, node;
			
		Hashtable<CM_PROJECT, Object> ht = new Hashtable() ;
			for (int i = 0; i < projectMembersList.size(); i++) {
		
		ht = projectMembersList.get(i);
	
	
	    member = doc.createElement("member");
	    rootElement.appendChild(member);

		 // set the member name
		  attr = doc.createAttribute("name");
		  if (ht.get(CM_PROJECT.MEMBER_ID) != null)
			attr.setValue(ht.get(CM_PROJECT.MEMBER_ID).toString());
		  else 
			attr.setValue("--");
		  member.setAttributeNode(attr);
	
         // set attributes
		  // set the member revision
		  attr = doc.createAttribute("revision");
		   if (ht.get(CM_PROJECT.REVISION) != null)
			attr.setValue(ht.get(CM_PROJECT.REVISION).toString());
		  else 
			attr.setValue("--");
		  member.setAttributeNode(attr); 
		
		  // set the change time stamp
		  attr = doc.createAttribute("timestamp");
		    if (ht.get(CM_PROJECT.TIMESTAMP) != null)
			attr.setValue(ht.get(CM_PROJECT.TIMESTAMP).toString());
		  else 
			attr.setValue("--");
		  member.setAttributeNode(attr); 
		   
		
		 
		// set member node childs
		
		  // set the change author 
		  node = doc.createElement("author");
		    if (ht.get(CM_PROJECT.AUTHOR) != null)
			node.appendChild(doc.createTextNode(ht.get(CM_PROJECT.AUTHOR).toString()));
		  else 
			node.appendChild(doc.createTextNode("--"));
		 member.appendChild(node);	
			
	
       	// set the member relative path
	    node = doc.createElement("relative_path");
		if (ht.get(CM_PROJECT.RELATIVE_FILE) != null)
		  node.appendChild(doc.createTextNode(ht.get(CM_PROJECT.RELATIVE_FILE).toString()));
		else 
		  node.appendChild(doc.createTextNode("--"));
		member.appendChild(node);
		
	/*	
	
	// set the path in mks project
	    node = doc.createElement("member_path");
		if (ht.get(CM_PROJECT.NAME) != null)
		  node.appendChild(doc.createTextNode(ht.get(CM_PROJECT.NAME).toString()));
		else 
		  node.appendChild(doc.createTextNode("--"));
		member.appendChild(node);
	
	
	 // set the changes comments
		node = doc.createElement("comments");
		if (ht.get(CM_PROJECT.DESCRIPTION) != null)
		node.appendChild(doc.createTextNode(ht.get(CM_PROJECT.DESCRIPTION).toString()));
		else 
		  node.appendChild(doc.createTextNode("--"));
		member.appendChild(node);	
		
	// set the old revision (used in last build)
		node = doc.createElement("old_revision");
		if (ht.get(CM_PROJECT.OLD_REVISION) != null)
		node.appendChild(doc.createTextNode(ht.get(CM_PROJECT.OLD_REVISION).toString()));
		else 
		  node.appendChild(doc.createTextNode("--"));
		member.appendChild(node);
		
		// set the project config path
	    node = doc.createElement("config_path");
		if (ht.get(CM_PROJECT.CONFIG_PATH) != null)
		  node.appendChild(doc.createTextNode(ht.get(CM_PROJECT.CONFIG_PATH).toString()));
		else 
		  node.appendChild(doc.createTextNode("--"));
		member.appendChild(node);
			
			
	  member_id = doc.createElement("member_id");
		if (ht.get(CM_PROJECT.MEMBER_ID) != null)
			member_id.appendChild(doc.createTextNode(ht.get(CM_PROJECT.MEMBER_ID).toString()));
		else 
		  member_id.appendChild(doc.createTextNode("--"));
		member.appendChild(member_id);
	
	
	revision = doc.createElement("revision");
		if (ht.get(CM_PROJECT.REVISION) != null)
		revision.appendChild(doc.createTextNode(ht.get(CM_PROJECT.REVISION).toString()));
		else 
		  revision.appendChild(doc.createTextNode("--"));
		member.appendChild(revision);
		
		
		timestamp = doc.createElement("timestamp");
		if (ht.get(CM_PROJECT.TIMESTAMP) != null)
		timestamp.appendChild(doc.createTextNode(ht.get(CM_PROJECT.TIMESTAMP).toString()));
		else 
		  timestamp.appendChild(doc.createTextNode("--"));
		member.appendChild(timestamp);
		
		author = doc.createElement("author");
		if (ht.get(CM_PROJECT.AUTHOR) != null)
		author.appendChild(doc.createTextNode(ht.get(CM_PROJECT.AUTHOR).toString()));
		else 
		  author.appendChild(doc.createTextNode("--"));
		member.appendChild(author);
	
*/	
			
		   }
		
			       
     // FilePath rootPath = Computer.currentComputer().getNode().getRootPath();
	 // String jobName = run.getParent().getName();
	  String reportsPath = workspace + File.separator +  "reports";
	
      File reportsDir = new File(reportsPath);

    // create the reports directory if it does not exist
    if (!reportsDir.isDirectory()) {
    
	reportsDir.mkdirs();
    // System.out.println("Folder reports created");
      }
        
    else {

     File[] files = reportsDir.listFiles();
     if(files!=null) { 
        for(File f: files) {

		if (f.getName() == "FVO_Report_All_Files.xml" || f.getName()  == "FVO_Report_All_Files.txt" || f.getName()  == "FVO_Report_Code_Arch_Files.txt")
                f.delete();
            }
        }
    }
	
	
	
      // write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
	
  		
	//	StreamResult result = new StreamResult(new File("C:\\Users\\abc6si\\.jenkins\\workspace\\Test_PTC_Plugin\\file.xml"));
	    
		File xmlReport = new File(reportsPath + File.separator + "FVO_Report_All_Files.xml");
		 xmlReport.getParentFile().mkdirs(); 
		
		// xmlReport.delete();
        xmlReport.createNewFile();
		
		StreamResult result = new StreamResult(xmlReport);
	
	
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
        
		// xmlReport.close();
		// result.getOutputStream().close();
		
		
	//	System.out.println("Report: FVO report successfully generated !");

	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	   catch(IOException ie){
            ie.printStackTrace();
        }
	}
	
	
	public void generateText_All_Files(FilePath workspace, List<Hashtable<CM_PROJECT, Object>> projectMembersList , String projectConfigPath) {	
		try {
	
			String reportsPath = workspace + File.separator +  "reports";
	
			File reportsDir = new File(reportsPath);
	  
			File outputFile = new File(reportsPath + File.separator + "FVO_Report_All_Files.txt");
			outputFile.createNewFile();
		
			PrintWriter fvoWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));
	
			fvoWriter.println("MKS project: " + projectConfigPath + "\n");
			fvoWriter.println("Report containing an overview of all files and their versions: \n");

		
			String memberPath;
			String memberRevision;
			Hashtable<CM_PROJECT, Object> ht = new Hashtable() ;
			
			for (int i = 0; i < projectMembersList.size(); i++) {
				    ht = projectMembersList.get(i);

					memberPath = ht.get(CM_PROJECT.RELATIVE_FILE).toString();
					memberRevision = ht.get(CM_PROJECT.REVISION).toString();
					fvoWriter.println(memberPath + "  " + memberRevision);
					
			}
	
			fvoWriter.close();
	 
		}
		
		catch(IOException ie){
            ie.printStackTrace();
        }
	
	
	}
	
	
	
	public void generateText_Code_Arch_Files(FilePath workspace, List<Hashtable<CM_PROJECT, Object>> myProjectMembersList , String projectConfigPath) {	
		try {
	
			String reportsPath = workspace + File.separator +  "reports";
	
			File reportsDir = new File(reportsPath);
	  
			File outputFile = new File(reportsPath + File.separator + "FVO_Report_Code_Arch_Files.txt");
			outputFile.createNewFile();
		
			PrintWriter fvoWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));
	
			fvoWriter.println("MKS project: " + projectConfigPath + "\n");
			fvoWriter.println("Report containing an overview of relevant files and their versions:");
			fvoWriter.println("- relevant files for compilation: .c, .h and .mk files");
			fvoWriter.println("- arch.zip files \n");
		
			String memberPath;
			String memberRevision;
			Hashtable<CM_PROJECT, Object> ht = new Hashtable() ;
			
			for (int i = 0; i < myProjectMembersList.size(); i++) {
				    ht = myProjectMembersList.get(i);

					memberPath = ht.get(CM_PROJECT.RELATIVE_FILE).toString();
					memberRevision = ht.get(CM_PROJECT.REVISION).toString();
					fvoWriter.println(memberPath + "  " + memberRevision);
					
			}
	
			fvoWriter.close();
	 
		}
		
		catch(IOException ie){
            ie.printStackTrace();
        }
	
	
	}
	
	
	
	
}