package app.lawnicons.helper;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultDocument;

import java.io.IOException;
import java.util.*;

public class SvgToVector {

    static String sourceDirectory = "../svg2/";
    static String darkRes = "../app/src/dark/res";
    static String LightRes = "../app/src/light/res";

    public static void main(String args[]) throws DocumentException, IOException {


        loadSvgToVector(sourceDirectory, darkRes + "/drawable", "dark");
        loadSvgToVector(sourceDirectory, LightRes + "/drawable", "light");
        createConfigs("../app/assets/appfilter.xml");

    }

    public static void loadSvgToVector(String sourceDirectory, String destDirectory, String mode) {
        if (null != sourceDirectory && !sourceDirectory.isEmpty()) {
            SvgFilesProcessor processor = new SvgFilesProcessor(sourceDirectory, destDirectory, mode);
            processor.process();
        }
    }

    private static void createConfigs(String appFilterFile) throws DocumentException, IOException {
        String compStart = "ComponentInfo{";
        String compEnd = "}";
        Document root = CommonUtil.getDocument(appFilterFile);
        List<Element> aList = CommonUtil.getElements(root, "item");
        HashMap<String, String> map = new HashMap<>();
        for (Element element : aList) {
            String component = element.attribute("component").getValue();
            String drawable = element.attribute("drawable").getValue();
            String comps = component.substring(compStart.length(), component.length() - compEnd.length());
            map.put(drawable, comps);
        }
        createDrawable(map, darkRes + "/xml/drawable.xml");
        createDrawable(map, LightRes + "/xml/drawable.xml");
        createIconMap(map, darkRes + "/xml/grayscale_icon_map.xml");
        createIconMap(map, LightRes + "/xml/grayscale_icon_map.xml");
        CommonUtil.writeDocumentToFile(root, darkRes +"/xml/appfilter.xml");
        CommonUtil.writeDocumentToFile(root, LightRes +"/xml/appfilter.xml");
    }

    private static void createIconMap(HashMap<String, String> map, String filename) throws IOException {
        Document doc = new DefaultDocument();
        doc.addElement("icons");
        for (Map.Entry<String, String> keyValue : map.entrySet()) {
            String[] comps = keyValue.getValue().split("/");
            doc.getRootElement().addElement("icon")
                .addAttribute("drawable", keyValue.getKey())
                .addAttribute("package", comps[0]);
        }
        CommonUtil.writeDocumentToFile(doc, filename);
    }

    private static void createDrawable(HashMap<String, String> map, String filename) throws IOException {

        Document doc = new DefaultDocument();
        doc.addElement("resources");
        doc.getRootElement().addElement("version", "1");
        map.keySet().forEach(drawable -> doc.getRootElement().addElement("item").addAttribute("drawable", drawable));
        CommonUtil.writeDocumentToFile(doc, filename);
    }


}
