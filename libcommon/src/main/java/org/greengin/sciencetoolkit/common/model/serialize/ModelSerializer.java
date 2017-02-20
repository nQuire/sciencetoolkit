package org.greengin.sciencetoolkit.common.model.serialize;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.greengin.sciencetoolkit.common.model.Model;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;

public class ModelSerializer {
	public static void model2xml(ModelVersionManager versionManager, Hashtable<String, Model> models, Context applicationContext, String file) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			Document doc = model2xml(versionManager, models);
			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(new File(applicationContext.getFilesDir(), file));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Document model2xml(ModelVersionManager versionManager, Hashtable<String, Model> models) throws ParserConfigurationException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("root");
		Attr versionAttr = doc.createAttribute("version");
		versionAttr.setValue(String.valueOf(versionManager.getCurrentVersion()));
		rootElement.setAttributeNode(versionAttr);
		
		doc.appendChild(rootElement);

		for (Map.Entry<String, Model> item : models.entrySet()) {
			Element modelElement = model2xml(item.getValue(), doc, rootElement);
			Attr modelIdAttr = doc.createAttribute("id");
			modelIdAttr.setValue(item.getKey());
			modelElement.setAttributeNode(modelIdAttr);
		}

		return doc;
	}

	private static Element model2xml(Model model, Document doc, Element containerElement) {
		Element modelElement = doc.createElement("item");
		containerElement.appendChild(modelElement);

		for (Map.Entry<String, Object> entry : model.entries().entrySet()) {
			Element entryElement = doc.createElement("entry");
			modelElement.appendChild(entryElement);

			Attr entryId = doc.createAttribute("id");
			entryId.setValue(entry.getKey());
			entryElement.setAttributeNode(entryId);

			Object obj = entry.getValue();
			Attr entryType = doc.createAttribute("type");

			boolean isModel = false;

			if (obj instanceof Integer) {
				entryType.setValue("int");
			} else if (obj instanceof Double) {
				entryType.setValue("double");
			} else if (obj instanceof Boolean) {
				entryType.setValue("bool");
			} else if (obj instanceof String) {
				entryType.setValue("string");
			} else if (obj instanceof Long) {
				entryType.setValue("long");
			} else if (obj instanceof Model) {
				entryType.setValue("model");
				isModel = true;
			} else {
				entryType.setValue("unknown");
			}

			entryElement.setAttributeNode(entryType);

			if (isModel) {
				model2xml((Model)obj, doc, entryElement);
			} else {
				entryElement.appendChild(doc.createTextNode(obj.toString()));
			}
		}		
		return modelElement;
	}

}
