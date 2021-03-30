package publib.tools;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlNode {

	String name = null;
	Map<String, String> attrs = null;
	
	String text = null;
	List<XmlNode> children = null;
	int level = 0;
	XmlNode parent = null;
	
	/**
	 * 根据结点名称获取子结点数组
	 * @param name
	 * @return
	 */
	public XmlNode[] getChildren(String name){
		List<XmlNode> list = new ArrayList<XmlNode>();
		
		if(children == null)
			return (XmlNode[]) list.toArray(new XmlNode[0]);
		for(XmlNode node:children)
		{
			if(name.equals(node.name)){
				list.add(node);
			}
		}
		
		return (XmlNode[]) list.toArray(new XmlNode[0]);
	}
	/**
	 * 根据结点名称获取子结点
	 * @param name
	 * @return
	 */
	public XmlNode getChild(String name){
		if(children == null)
			return null;
		for(XmlNode node:children)
		{
			if(name.equals(node.name)){
				return node;
			}
		}
		
		return null;
	}
	
	/**
	 * 解析数据流中XML到当点node
	 * @param ins
	 */
	public void decodeXml(InputStream ins)
	{
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(ins, "UTF-8");
			
			int eventType = parser.getEventType();
			
			int attrsCount = 0;
			XmlNode parentNode = null;
			XmlNode currentNode = this;
			
			while(XmlPullParser.END_DOCUMENT != eventType){
				switch(eventType){
				//case XmlPullParser.START_DOCUMENT:
				case XmlPullParser.START_TAG:
					if(null == currentNode)
					{
						currentNode = new XmlNode();
					}
					currentNode.name = parser.getName();

					attrsCount = parser.getAttributeCount();
					if(attrsCount > 0){
						currentNode.attrs = new HashMap<String, String>();
						for(int index=0; index<attrsCount; index++){
							currentNode.attrs.put(parser.getAttributeName(index),
													parser.getAttributeValue(index));
						}
					}
					
					if(parentNode != null){
						parentNode.children.add(currentNode);
						currentNode.level = parentNode.level + 1;
						currentNode.parent = parentNode;
					}
					
					try{
						//如果读取到了TEXT说明当前结点读完了
						currentNode.text = parser.nextText();
						//不知道为什么有Text的结点读取不了END_TAG事件,所以在这边设置为null
						currentNode = null;
					}
					catch(Exception e){
						currentNode.children = new ArrayList<XmlNode>();
						parentNode = currentNode;
						currentNode = null;
						continue;
					}

					break;
					
				case XmlPullParser.END_TAG:
					if(currentNode != null){
						//never do this 
						parentNode = currentNode.parent;
						currentNode = null;
					}
					else if(parentNode != null){
						parentNode = parentNode.parent;
					}
					break;
					
				//case XmlPullParser.END_DOCUMENT:
					
					//break;
				}
				//int i = XmlPullParser.ENTITY_REF;
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(ins != null){
					ins.close();
					ins = null;
				}
			}catch(Exception e){
				
			}
		}
		
	}
	/**
	 * 根据文件路径解析XML到当前node
	 * @param xmlFile
	 */
	public void decodeXml(String xmlFile){
		try{
			decodeXml(new FileInputStream(xmlFile));
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("XmlNode [name=");
		builder.append(name);
		builder.append(", attrs=");
		builder.append(attrs);
		builder.append(", text=");
		builder.append(text);
		builder.append(", children=");
		builder.append(children);
		builder.append(", level=");
		builder.append(level);
		builder.append(", parent=");
		builder.append(parent==null?null:parent.name);
		builder.append("]");
		return builder.toString();
	}
}
