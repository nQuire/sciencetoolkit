package org.greengin.sciencetoolkit.model.serialize;

import java.io.File;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelChangeListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class ModelDeserializer {
	public static Hashtable<String, Model> xml2modelMap(ModelChangeListener listener, ModelVersionManager versionManager, Context applicationContext, String file) {
		try {
			Element rootElement = loadRootElement(applicationContext, file);
			Hashtable<String, Model> items = xml2modelMap(rootElement, listener);
			versionCheck(versionManager, rootElement, items);
			return items;
		} catch (Exception e) {
			e.printStackTrace();
			return new Hashtable<String, Model>();
		}
	}

	
	private static Element loadRootElement(Context applicationContext, String file) throws Exception {
		File fXmlFile = new File(applicationContext.getFilesDir(), file);
		Scanner scanner = new Scanner(fXmlFile);
		try {
	        while(scanner.hasNextLine()) {       
	        	Log.d("stk file", scanner.nextLine());
	        }
	    } finally {
	        scanner.close();
	    }
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		Element rootElement = doc.getDocumentElement();
		return rootElement;
	}
	
	private static void versionCheck(ModelVersionManager versionManager, Element rootElement, Hashtable<String, Model> items) {
		int modelVersion = getVersion(rootElement);
		int currentVersion = versionManager.getCurrentVersion();
		
		if (currentVersion > modelVersion) {
			for (Entry<String, Model> entry : items.entrySet()) {
				versionManager.updateRootModel(entry.getKey(), entry.getValue(), modelVersion);
			}
		}
	}
	private static int getVersion(Element rootElement) {
		try {
			String vStr = rootElement.getAttribute("version").trim();
			int v = Integer.parseInt(vStr);
			return v;
		} catch (Exception e) {
			return 0;
		}
	}

	private static Hashtable<String, Model> xml2modelMap(Element containerElement, ModelChangeListener listener) {
		Hashtable<String, Model> models = new Hashtable<String, Model>();

		NodeList nList = containerElement.getChildNodes();

		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if ("item".equals(eElement.getTagName())) {
					String itemId = eElement.getAttribute("id");
					Model model = xml2model(eElement, listener, null);
					models.put(itemId, model);
				}
			}
		}

		return models;
	}

	private static Model xml2model(Element modelElement, ModelChangeListener listener, Model parent) {
		Model model;
		
		if (parent == null) {
			model = new Model(listener);
		} else {
			model = new Model(parent);
		}

		NodeList nSubList = modelElement.getChildNodes();

		for (int j = 0; j < nSubList.getLength(); j++) {
			Node nSubNode = nSubList.item(j);
			if (nSubNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eSubElement = (Element) nSubNode;
				if ("entry".equals(eSubElement.getTagName())) {
					String type = eSubElement.getAttribute("type");

					String entryId = eSubElement.getAttribute("id");

					if ("model".equals(type)) {
						Node nValueNode = eSubElement.getFirstChild();
						if (nValueNode != null && nValueNode.getNodeType() == Node.ELEMENT_NODE) {
							Model submodel = xml2model((Element) nValueNode, listener, model);
							model.setModel(entryId, submodel, true);
						}
					} else {
						String valueStr = eSubElement.getTextContent();
						if ("bool".equals(type)) {
							boolean valueBool = Boolean.parseBoolean(valueStr);
							model.setBool(entryId, valueBool, true);
						} else if ("string".equals(type)) {
							model.setString(entryId, valueStr, true);
						} else if ("int".equals(type)) {
							try {
								int valueInt = Integer.parseInt(valueStr);
								model.setInt(entryId, valueInt, true);
							} catch (NumberFormatException e) {
							}
						} else if ("double".equals(type)) {
							try {
								double valueDbl = Double.parseDouble(valueStr);
								model.setDouble(entryId, valueDbl, true);
							} catch (NumberFormatException e) {
							}
						} else if ("long".equals(type)) {
							try {
								long valueLong = Long.parseLong(valueStr);
								model.setLong(entryId, valueLong, true);
							} catch (NumberFormatException e) {
							}
						}
					}
				}
			}
		}

		return model;
	}
}
