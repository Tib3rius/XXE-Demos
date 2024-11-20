package com.tib3rius.xxe;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@WebServlet("/basic")
public class BasicServlet extends HttpServlet {

	private TemplateEngine templateEngine;

	@Override
    public void init() throws ServletException {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, null, null, null); // Pass null for GET requests (no form data)
    }

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String xmlData = request.getParameter("xml");
        String content = null;
		String messageType = "info";  // Default to "info" for informational messages

        try {
            // Create a DocumentBuilderFactory (with no XXE mitigation for educational purposes)
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // Parse the input XML string
            Document doc = dBuilder.parse(new org.xml.sax.InputSource(new StringReader(xmlData)));
            doc.getDocumentElement().normalize();

            // Check for the element "name" and get its value
            String elementName = "name";  // Define the element name you want to check
            NodeList nodeList = doc.getElementsByTagName(elementName);

            if (nodeList.getLength() > 0) {
                // Element found, get its value
                content = "Welcome, " + nodeList.item(0).getTextContent() + "!";
            } else {
                content = "Element <" + elementName + "> not found in the XML.";
				messageType = "error";  // Set message type to "error" in case of an exception
            }
        } catch (Exception e) {
            content = "Error parsing XML: " + e.getMessage();
			messageType = "error";  // Set message type to "error" in case of an exception
        }

        processRequest(request, response, content, xmlData, messageType);  // Pass dynamic data for POST requests
    }

	private void processRequest(HttpServletRequest request, HttpServletResponse response, String content, String xmlInput, String messageType) throws IOException {
        // Set a default XML if none is provided
        if (xmlInput == null || xmlInput.trim().isEmpty()) {
            xmlInput = "<?xml version=\"1.0\" ?>\n<root>\n    <name>Tib3rius</name>\n</root>";
        }

        WebContext ctx = new WebContext(request, response, getServletContext());
        ctx.setVariable("content", content);  // Dynamic content after form submission
        ctx.setVariable("xmlContent", xmlInput);  // XML content for textarea
		ctx.setVariable("messageType", messageType);

        response.setContentType("text/html");
        templateEngine.process("basic", ctx, response.getWriter());
    }
}
