/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author cyann
 */
public class HtmlTableGenerator<T> {

    private static final Logger LOGGER = Logger.getLogger(HtmlTableGenerator.class.getName());

    public static class Column<T> {

        private final String name;
        private final Function<T, String> dataAccessor;
        private boolean header;

        public String getName() {
            return name;
        }

        public Function<T, String> getDataAccessor() {
            return dataAccessor;
        }

        public boolean isHeader() {
            return header;
        }

        public Column<T> setHeader(boolean header) {
            this.header = header;
            return this;
        }

        public Column(String name, Function<T, String> dataAccessor) {
            this.name = name;
            this.dataAccessor = dataAccessor;
        }

    }

    private final String title;
    private final List<T> data;
    private final List<Column<T>> columns;

    public HtmlTableGenerator(String title, List<T> data) {
        this.title = title;
        this.data = data;
        this.columns = new ArrayList<>();
    }

    public void addColumn(Column<T> column) {
        columns.add(column);
    }

    private String loadCss() {
        String data = "";
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource("print.css").toURI());
            byte[] fileBytes = Files.readAllBytes(path);
            data = new String(fileBytes);
        } catch (URISyntaxException | IOException ex) {
            LOGGER.getLogger(HtmlTableGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;

    }

    public String buildContent() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element html = doc.createElement("html");
            doc.appendChild(html);

            Element head = doc.createElement("head");
            html.appendChild(head);

            Element style = doc.createElement("style");
            style.setTextContent(loadCss());
            head.appendChild(style);

            Element body = doc.createElement("body");
            html.appendChild(body);

            Element h1 = doc.createElement("h3");
            h1.setTextContent(title);
            body.appendChild(h1);

            Element table = doc.createElement("table");
            body.appendChild(table);

            Element trHeader = doc.createElement("tr");
            table.appendChild(trHeader);

            for (Column<T> column : columns) {
                Element th = doc.createElement("th");
                th.setTextContent(column.getName());
                trHeader.appendChild(th);
            }

            for (T element : data) {
                Element tr = doc.createElement("tr");
                table.appendChild(tr);

                for (Column<T> column : columns) {
                    Element td = doc.createElement(column.isHeader() ? "th" : "td");
                    String text = column.getDataAccessor().apply(element);
                    if ("null".equals(text)) {
                        text = "";
                    }
                    td.setTextContent(text);
                    tr.appendChild(td);
                }
            }

            // write the content into xml file
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "html");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            System.out.println(sw.toString());
            return sw.toString();
        } catch (ParserConfigurationException | TransformerException ex) {
            throw new RuntimeException(ex);
        }
    }

}
