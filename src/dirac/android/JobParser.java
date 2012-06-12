package dirac.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.util.Log;

public class JobParser {

	private static final String tag = "JobParser";

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private final List<Job> list;
	private final HashMap<String, String> map;

	public JobParser() {
		this.list = new ArrayList<Job>();
		this.map = new HashMap<String, String>();
	}

	public String getNodeValue(NamedNodeMap map, String key) {
		String nodeValue = null;
		Node node = map.getNamedItem(key);
		if (node != null) {
			nodeValue = node.getNodeValue();
		}
		return nodeValue;
	}

	public List<Job> getList() {
		return this.list;
	}

	public HashMap<String, String> getMap() {
		return this.map;
	}


	public void parse(InputStream inStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] RowData = line.split(",");

				Job jobid = new Job(Integer.parseInt(RowData[0]),RowData[1],RowData[2],RowData[3],RowData[4]);
				this.list.add(jobid);
				this.map.put(RowData[0],RowData[1]);
				// do something with "data" and "value"
			}
		}
		catch (IOException ex) {
			// handle exception
		}
		finally {
			try {
				inStream.close();
			}
			catch (IOException e) {
				// handle exception
			}
		}


	}		

}

