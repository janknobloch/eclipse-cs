package net.sf.eclipsecs.core.transformer;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

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

import net.sf.eclipsecs.core.util.CheckstyleLog;

public class XmlProfileWriter {
  private static final String XML_NODE_ROOT = "profiles";
  private static final String XML_NODE_PROFILE = "profile";
  private static final String XML_NODE_SETTING = "setting";

  private static final String XML_ATTRIBUTE_VERSION = "version";
  private static final String XML_ATTRIBUTE_ID = "id";
  private static final String XML_ATTRIBUTE_NAME = "name";
  private static final String XML_ATTRIBUTE_PROFILE_KIND = "kind";
  private static final String XML_ATTRIBUTE_VALUE = "value";

  private static final String CLEANUP_PROFILE_VERSION = "2";
  private static final String CLEANUP_PROFILE_KIND = "CleanUpProfile";

  private static final String FORMATTER_PROFILE_VERSION = "10";
  private static final String FORMATTER_PROFILE_KIND = "CodeFormatterProfile";

  public static void writeCleanupProfileToStream(OutputStream stream, String name,
          Map<String, String> settings) {
    writeProfileToStream(stream, name, CLEANUP_PROFILE_VERSION, CLEANUP_PROFILE_KIND, settings);
  }

  public static void writeFormatterProfileToStream(OutputStream stream, String name,
          Map<String, String> settings) {
    writeProfileToStream(stream, name, FORMATTER_PROFILE_VERSION, FORMATTER_PROFILE_KIND, settings);
  }

  public static void writeProfileToStream(OutputStream stream, String name, String profileVersion,
          String profileKind, Map<String, String> settings) {

    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document document = builder.newDocument();

      final Element rootElement = document.createElement(XML_NODE_ROOT);
      rootElement.setAttribute(XML_ATTRIBUTE_VERSION, profileVersion);

      document.appendChild(rootElement);

      final Element profileElement = document.createElement(XML_NODE_PROFILE);
      profileElement.setAttribute(XML_ATTRIBUTE_NAME, name);
      profileElement.setAttribute(XML_ATTRIBUTE_VERSION, profileVersion);
      profileElement.setAttribute(XML_ATTRIBUTE_PROFILE_KIND, profileKind);

      final Iterator<String> keyIter = settings.keySet().iterator();

      while (keyIter.hasNext()) {
        final String key = keyIter.next();
        final String value = settings.get(key);
        if (value != null) {
          final Element setting = document.createElement(XML_NODE_SETTING);
          setting.setAttribute(XML_ATTRIBUTE_ID, key);
          setting.setAttribute(XML_ATTRIBUTE_VALUE, value);
          profileElement.appendChild(setting);
        } else {
          CheckstyleLog.log(null, String.format("Profile is missing value for [key=%s]", key));
        }
      }
      rootElement.appendChild(profileElement);

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(new DOMSource(document), new StreamResult(stream));
    } catch (TransformerException | ParserConfigurationException e) {
      CheckstyleLog.log(e, "Error serializing XML");
    }
  }

}
