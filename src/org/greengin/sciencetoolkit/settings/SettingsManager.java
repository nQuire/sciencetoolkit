package org.greengin.sciencetoolkit.settings;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class SettingsManager {

	private static SettingsManager instance;

	public static void init(Context applicationContext) {
		instance = new SettingsManager(applicationContext);
	}

	public static SettingsManager getInstance() {
		return instance;
	}

	ReentrantLock lock;
	Timer timer;

	Context applicationContext;
	Hashtable<String, Settings> items;

	private SettingsManager(Context applicationContext) {
		this.applicationContext = applicationContext;

		lock = new ReentrantLock();
		items = new Hashtable<String, Settings>();

		load();
	}

	public Settings get(String key) {
		if (!items.containsKey(key)) {
			items.put(key, new Settings(this));
			modified();
		}

		return items.get(key);
	}

	public void remove(String key) {
		if (items.remove(key) != null) {
			modified();
		}
	}

	public void modified() {
		Log.d("stk settings", "modified");
		lock.lock();
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Log.d("stk settings", "about to save");
					timer = null;
					save();

				}
			}, 1000);
		}
		lock.unlock();
	}

	private void load() {
		Log.d("stk settings", "load");

		try {

			File fXmlFile = new File(applicationContext.getFilesDir(), "settings.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("item");

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String itemId = eElement.getAttribute("id");
					Log.d("stk settings", "item: " + itemId);
					
					Settings settings = new Settings(this);
					
					NodeList nSubList = eElement.getElementsByTagName("entry");

					for (int j = 0; j < nSubList.getLength(); j++) {
						Node nSubNode = nSubList.item(j);
						if (nSubNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eSubElement = (Element) nSubNode;
							String type = eSubElement.getAttribute("type");
							String valueStr = eSubElement.getTextContent();
							String entryId = eSubElement.getAttribute("id");

							Object valueObj = null;
							if ("bool".equals(type)) {
								boolean valueBool = Boolean.parseBoolean(valueStr); 
								settings.setBool(entryId, valueBool, true);
								valueObj = valueBool;
							} else if ("string".equals(type)) {
								valueObj = valueStr;
								settings.setString(entryId, valueStr, true);
							} else if ("int".equals(type)) {
								try {
									int valueInt = Integer.parseInt(valueStr);
									settings.setInt(entryId, valueInt, true);
									valueObj = valueInt;
								} catch (NumberFormatException e) {
									valueObj = null;
								}
							} else if ("double".equals(type)) {
								try {
									double valueDbl = Double.parseDouble(valueStr);
									settings.setDouble(entryId, valueDbl, true);
									valueObj = valueDbl;
								} catch (NumberFormatException e) {
									valueObj = null;
								}
							} else if ("long".equals(type)) {
								try {
									long valueLong = Long.parseLong(valueStr);
									settings.setLong(entryId, valueLong, true);
									valueObj = valueLong;
								} catch (NumberFormatException e) {
									valueObj = null;
								}
							}
							Log.d("stk settings", "entry: " + entryId + " " + type + " " + valueObj);
						}
						
						this.items.put(itemId, settings);
					}
				}
			}
		} catch (Exception e) {
			Log.d("std settings", "exception: " + e.getMessage());
		}

	}

	private void save() {
		Log.d("stk settings", "save");
		lock.lock();

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("settings");
			doc.appendChild(rootElement);

			for (Map.Entry<String, Settings> item : items.entrySet()) {
				Element itemElement = doc.createElement("item");
				rootElement.appendChild(itemElement);
				Attr itemId = doc.createAttribute("id");
				itemId.setValue(item.getKey());
				itemElement.setAttributeNode(itemId);

				for (Map.Entry<String, Object> entry : item.getValue().entries.entrySet()) {
					Element entryElement = doc.createElement("entry");
					itemElement.appendChild(entryElement);

					Attr entryId = doc.createAttribute("id");
					entryId.setValue(entry.getKey());
					entryElement.setAttributeNode(entryId);

					Object obj = entry.getValue();
					Attr entryType = doc.createAttribute("type");

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
					} else {
						entryType.setValue("unknown");
					}

					entryElement.setAttributeNode(entryType);
					entryElement.appendChild(doc.createTextNode(obj.toString()));
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(new File(applicationContext.getFilesDir(), "settings.xml"));
			transformer.transform(source, result);
			Log.d("stk settings", "saved");

		} catch (Exception e) {
			Log.d("stk settings", "exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
