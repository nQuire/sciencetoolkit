package org.greengin.sciencetoolkit.model;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class ModelDeserializer {
	public static Hashtable<String, Model> xml2modelMap(ModelChangeListener listener, Context applicationContext, String file) {
		try {
			File fXmlFile = new File(applicationContext.getFilesDir(), file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			Element rootElement = doc.getDocumentElement();

			return xml2modelMap(rootElement, listener);
		} catch (Exception e) {
			Log.e("stk deserialize", e.getMessage());
			e.printStackTrace();
			return new Hashtable<String, Model>();
		}
	}

	public static Vector<Model> xml2modelList(ModelChangeListener listener, Context applicationContext, String file) {
		try {
			File fXmlFile = new File(applicationContext.getFilesDir(), file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			Element rootElement = doc.getDocumentElement();

			return xml2modelList(rootElement, listener);
		} catch (Exception e) {
			e.printStackTrace();
			return new Vector<Model>();
		}
	}

	public static Hashtable<String, Model> xml2modelMap(Element containerElement, ModelChangeListener listener) {
		Hashtable<String, Model> models = new Hashtable<String, Model>();

		NodeList nList = containerElement.getChildNodes();

		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if ("item".equals(eElement.getTagName())) {
					String itemId = eElement.getAttribute("id");
					Model model = xml2model(eElement, listener);
					models.put(itemId, model);
				}
			}
		}

		return models;
	}

	public static Vector<Model> xml2modelList(Element containerElement, ModelChangeListener listener) {
		Vector<Model> models = new Vector<Model>();

		NodeList nList = containerElement.getChildNodes();

		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if ("item".equals(eElement.getTagName())) {
					Model model = xml2model(eElement, listener);
					models.add(model);
				}
			}
		}

		return models;
	}

	public static Model xml2model(Element modelElement, ModelChangeListener listener) {
		Model model = new Model(listener);

		NodeList nSubList = modelElement.getChildNodes();

		for (int j = 0; j < nSubList.getLength(); j++) {
			Node nSubNode = nSubList.item(j);
			if (nSubNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eSubElement = (Element) nSubNode;
				if ("entry".equals(eSubElement.getTagName())) {
					String type = eSubElement.getAttribute("type");

					String entryId = eSubElement.getAttribute("id");

					Object valueObj = null;

					if ("model".equals(type)) {
						Node nValueNode = eSubElement.getFirstChild();
						if (nValueNode != null && nValueNode.getNodeType() == Node.ELEMENT_NODE) {
							Model submodel = xml2model((Element) nValueNode, listener);
							model.setModel(entryId, submodel, true);
						}
					} else {
						String valueStr = eSubElement.getTextContent();
						if ("bool".equals(type)) {
							boolean valueBool = Boolean.parseBoolean(valueStr);
							model.setBool(entryId, valueBool, true);
							valueObj = valueBool;
						} else if ("string".equals(type)) {
							valueObj = valueStr;
							model.setString(entryId, valueStr, true);
						} else if ("int".equals(type)) {
							try {
								int valueInt = Integer.parseInt(valueStr);
								model.setInt(entryId, valueInt, true);
								valueObj = valueInt;
							} catch (NumberFormatException e) {
								valueObj = null;
							}
						} else if ("double".equals(type)) {
							try {
								double valueDbl = Double.parseDouble(valueStr);
								model.setDouble(entryId, valueDbl, true);
								valueObj = valueDbl;
							} catch (NumberFormatException e) {
								valueObj = null;
							}
						} else if ("long".equals(type)) {
							try {
								long valueLong = Long.parseLong(valueStr);
								model.setLong(entryId, valueLong, true);
								valueObj = valueLong;
							} catch (NumberFormatException e) {
								valueObj = null;
							}
						}
					}
					Log.d("stk settings", "entry: " + entryId + " " + type + " " + valueObj);
				}
			}
		}

		return model;
	}
}
