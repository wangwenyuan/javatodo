/**
 * Copyright (c) 2017, Wang Wenyuan 王文渊 (827287829@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javatodo.core.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
 
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
 
public class XmlTools {
 
    private XPath path;
    private Document doc;
 
    public XmlTools(String xml) {
        StringReader sr = new StringReader(xml.trim());
        InputSource inputSource = new InputSource(sr);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.parse(inputSource);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.path = XPathFactory.newInstance().newXPath();
    }
 
    // 将路径解析为字符串
    public String parseToString(String expression) {
        return (String) this.parse(expression, null, XPathConstants.STRING);
    }
 
    // 将路径解析为字符串
    public String parseToString(Object node, String expression) {
        return (String) this.parse(expression, node, XPathConstants.STRING);
    }
 
    // 将路径解析为布尔值
    public Boolean parseToBoolean(String expression) {
        return (Boolean) parse(expression, null, XPathConstants.BOOLEAN);
    }
 
    // 将路径解析为布尔值
    public Boolean parseToBoolean(Object node, String expression) {
        return (Boolean) parse(expression, node, XPathConstants.BOOLEAN);
    }
 
    // 将路径解析为数字
    public Number parseToNumber(String expression) {
        return (Number) parse(expression, null, XPathConstants.NUMBER);
    }
 
    // 将路径解析为数字
    public Number parseToNumber(Object node, String expression) {
        return (Number) parse(expression, node, XPathConstants.NUMBER);
    }
 
    // 获取某个节点
    public Node parseToNode(String expression) {
        return (Node) parse(expression, null, XPathConstants.NODE);
    }
 
    // 获取某个节点
    public Node parseToNode(Object node, String expression) {
        return (Node) parse(expression, node, XPathConstants.NODE);
    }
 
    // 获取子节点
    public NodeList parseToNodeList(String expression) {
        return (NodeList) parse(expression, null, XPathConstants.NODESET);
    }
 
    // 获取子节点
    public NodeList parseToNodeList(Object node, String expression) {
        return (NodeList) parse(expression, node, XPathConstants.NODESET);
    }
 
    // 将节点封装成map形式
    public Map<String, String> parseToMap() {
        Element root = doc.getDocumentElement();
        Map<String, String> params = new HashMap<String, String>();
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                params.put(node.getNodeName(), node.getTextContent());
            }
        }
        return params;
    }
 
    /**
     * 获取xml解析后的内容
     *
     * @param expression xpath表达式
     * @param item
     * @param returnType
     * @return
     */
    private Object parse(String expression, Object item, QName returnType) {
        item = null == item ? doc : item;
        try {
            return path.evaluate(expression, item, returnType);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}