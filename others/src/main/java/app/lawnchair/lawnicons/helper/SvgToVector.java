package app.lawnchair.lawnicons.helper;

import org.apache.commons.lang3.text.WordUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultDocument;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SvgToVector {

    static String ROOT_FOLDER="..";
    static String sourceDirectory = ROOT_FOLDER + "/svgs/";
    static String darkRes = ROOT_FOLDER + "/app/src/dark/res";
    static String LightRes = ROOT_FOLDER+ "/app/src/light/res";
    static String oldIconMapFile = ROOT_FOLDER+ "/app/src/main/res/xml/grayscale_icon_map.xml";

    public static void main(String args[]) throws DocumentException, IOException {


        loadSvgToVector(sourceDirectory, darkRes + "/drawable", "dark");
        loadSvgToVector(sourceDirectory, LightRes + "/drawable", "light");
        createConfigs(ROOT_FOLDER+"/app/assets/appfilter.xml");
        System.out.println("SvgToVector task completed");
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
        Map<String, String> map = new HashMap<>();
        for (Element element : aList) {
            String component = element.attribute("component").getValue();
            String drawable = element.attribute("drawable").getValue();
            if (component.startsWith(compStart) && component.endsWith(compEnd)) {
                String comps = component.substring(compStart.length(), component.length() - compEnd.length());
                map.put(comps, drawable);
            }
        }
        Map sortedMap = CommonUtil.sortedMapByValues(map);
        createDrawable(sortedMap, darkRes + "/xml/drawable.xml");
        createDrawable(sortedMap, LightRes + "/xml/drawable.xml");
        createIconMap(sortedMap, darkRes + "/xml/grayscale_icon_map.xml");
        createIconMap(sortedMap, LightRes + "/xml/grayscale_icon_map.xml");
        CommonUtil.writeDocumentToFile(root, darkRes + "/xml/appfilter.xml");
        CommonUtil.writeDocumentToFile(root, LightRes + "/xml/appfilter.xml");
    }

    private static void createIconMap(Map<String, String> map, String filename) throws IOException {
        Document doc = new DefaultDocument();
        doc.addElement("icons");
        for (Map.Entry<String, String> keyValue : map.entrySet()) {
            String[] comps = keyValue.getKey().split("/");
            doc.getRootElement().addElement("icon")
                .addAttribute("drawable", "@drawable/" + keyValue.getValue())
                .addAttribute("package", comps[0])
                .addAttribute("name", WordUtils.capitalize(keyValue.getValue().replaceAll("_", " ")));
        }
        CommonUtil.writeDocumentToFile(doc, filename);
        //Add icon mapping from old grayscale_icon_map.xml
        updateOldIconMap(doc);
    }

    private static void updateOldIconMap(Document doc) {
        try {
            Document root = CommonUtil.getDocument(oldIconMapFile);
            List<Element> oldElementList = CommonUtil.getElements(root, "icon");
            List<String> packageList = doc.getRootElement().elements().stream()
                .map(i -> i.attribute("package").getValue()).collect(Collectors.toList());
            oldElementList.stream()
                .sorted(Comparator.comparing(i -> i.attribute("drawable").getValue()))
                .filter(i -> !packageList.contains(i.attribute("package").getValue()))
                .forEach(e -> {
                    doc.getRootElement().addElement("icon")
                        .addAttribute("drawable", e.attribute("drawable").getValue())
                        .addAttribute("package", e.attribute("package").getValue())
                        .addAttribute("name", e.attribute("name").getValue());
                });
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createDrawable(Map<String, String> map, String filename) throws IOException {
        Document doc = new DefaultDocument();
        doc.addElement("resources");
        doc.getRootElement().addElement("version").addText("1");
        map.values().forEach(drawable -> doc.getRootElement().addElement("item").addAttribute("drawable", drawable));
        CommonUtil.writeDocumentToFile(doc, filename);
    }


}
