package main;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class run {

	public static void main(String[] args) {
//		allFiles();
		singleFile("input/Presentation/1.txt");

	}

	private static void singleFile(String filePath) {				
		try {
			File f = new File(filePath);
			byte[] encoded = Files.readAllBytes(f.toPath());
			String input = new String(encoded, Charset.defaultCharset());
			input = input.replaceAll("&", "&amp;");
			
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(input));
			Document doc = db.parse(is);
	
			Presentation2Content mathDoc = new Presentation2Content(doc);
			String convertedPres =  mathDoc.print();
			
			
			String contentPath = f.getPath().replaceAll("Presentation", "Content");
			File c = new File(contentPath);
			if(c.exists()) {
				byte[] b = Files.readAllBytes(c.toPath());
				String content = new String(b, Charset.defaultCharset());
				content = content.replaceAll("\\s{2,}", "").replaceAll("\n", "");
				convertedPres = convertedPres.replaceAll(System.getProperty("line.separator"), "");
				System.out.println(convertedPres);
				System.out.println(content);
				if(!convertedPres.trim().equals(content.trim())) {
					System.out.println("Conversion does not mathch");
				}
			}else {
				System.out.println(contentPath + " does not exist.");
				System.out.println(convertedPres);
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
	}

	private static void allFiles() {
		List<String> successfulFiles = new ArrayList<String>();
		List<String> failedFiles = new ArrayList<String>();
		int uncounted = 0;
		
		File inputfolder = new File("input/Presentation");
		for(File dir : inputfolder.listFiles()) {
			for(File f : dir.listFiles()) {
				try {
					
					byte[] encoded = Files.readAllBytes(f.toPath());
					String input = new String(encoded, Charset.defaultCharset());
					input = input.replaceAll("&", "&amp;");
					
					DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(input));
					Document doc = db.parse(is);

					Presentation2Content mathDoc = new Presentation2Content(doc);
					String convertedPres =  mathDoc.print();
					
					
					String contentPath = f.getPath().replaceAll("Presentation", "Content");
					File c = new File(contentPath);
					if(c.exists()) {
						byte[] b = Files.readAllBytes(c.toPath());
						String content = new String(b, Charset.defaultCharset());
						content = content.replaceAll("\\s{2,}", "").replaceAll("\n", "");
						convertedPres = convertedPres.replaceAll(System.getProperty("line.separator"), "");
						if(!convertedPres.trim().equals(content.trim())) {
							failedFiles.add(f.getPath());
						}else {
							successfulFiles.add(f.getPath());
						}
					}else {
						System.out.println(contentPath + " does not exist.");
						System.out.println(convertedPres);
						uncounted++;
					}
				} catch (SAXException | IOException | ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
		}

		
		System.out.println("\n\nEOF!");
		System.out.println("\n STATS:");
		System.out.println("Successful: " + successfulFiles.size());
		System.out.println("Failed: " + failedFiles.size());
		System.out.println("Unmatched files: " + uncounted);
		
	}

}
