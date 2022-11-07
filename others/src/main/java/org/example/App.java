package org.example;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultDocument;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class App {

    public static String APP_DIR = "E:\\git\\singleicons\\";

    public static String ROOT_DIR = "E:\\git\\lawnicons-fork\\";
    public static final String UTF_8 = "UTF-8";
    static String tempDirectory = "E:\\git\\temp\\";
    static String arcVecPath = ROOT_DIR+"Arcticons\\icons\\black\\";
    static  String arcConfigPath = ROOT_DIR + "Arcticons\\app\\src\\main\\assets\\appfilter.xml";
    static String frostVecPath = ROOT_DIR +"frost\\icons\\";
    static  String frostConfigPath = ROOT_DIR+"frost\\app\\src\\main\\res\\xml\\appfilter.xml";
    static String customSvgPath = APP_DIR + "customIcons\\svg\\";
    static String customConfigPath = APP_DIR+"customIcons\\xml\\grayscale_icon_map.xml";

//    static String lawnPath = "D:\\git\\lawnicons\\app\\src\\main\\res\\";
//    static String tfPath = "D:\\git\\teamFiles-Lawnicons\\app\\src\\main\\res\\";
    static String
            outputConfigPath = APP_DIR+ "app\\src\\main\\res\\xml\\grayscale_icon_map.xml";
    static String outDrawablePath = APP_DIR+"app\\src\\main\\res\\drawable\\";
    static String outImagePath = APP_DIR+"app\\src\\main\\res\\png\\";
    static String[] lawnPorts = {"lawnicons#lawn","rkicons#rk","teamFiles-Lawnicons#tf"};
    public static void main(String[] args) throws DocumentException, IOException {
        System.setProperty("file.encoding", UTF_8);
//        loadProps();
        createFolder();

        List<Layout> finalObj  = new ArrayList<>();

        loadCustomIcons(finalObj);
        System.out.println("loadCustomIcons completed");

        for(String lawn : lawnPorts ){
            String[] lawnDetails = lawn.split("#");
            String lawnPath = ROOT_DIR + lawnDetails[0] +"\\app\\src\\main\\res\\";
            System.out.println(" PATH : "+ lawnPath);
            String prefix = lawnPath.replaceAll("-", "_");
            if(lawnDetails.length >1) {
                prefix = lawnDetails[1];
            }
            prefix +="_";
            loadLawnIcons(finalObj,lawnPath,prefix);
            System.out.println(lawn +" completed");
        }


        loadIconConfigFromArcIcons(arcVecPath,arcConfigPath,finalObj,"arc_");
        System.out.println("ArcIcons completed");
        loadIconConfigFromArcIcons(frostVecPath,frostConfigPath,finalObj,"frost_");
        System.out.println("Frost completed");


        FileUtil.copyFilesFromDir(FileUtil.file(APP_DIR+"customIcons\\launcher\\"),FileUtil.file(outDrawablePath),true);

        updateOrCreateMappingFile(finalObj);
//        VectorToImage.createPNGForMIUI(finalObj);
    }

    private static void createFolder() {
        FileUtil.del(outDrawablePath);
        FileUtil.del(tempDirectory);
        FileUtil.mkdir(tempDirectory);
        FileUtil.mkdir(outDrawablePath);
        FileUtil.mkdir(outImagePath);
    }

    private static void updateOrCreateMappingFile(List<Layout> finalObj) throws IOException {
        Document outDocument= ObjectToXml(finalObj);
        writeDocumentToFile(outDocument, outputConfigPath);
    }

    private static void writeDocumentToFile(Document outDocument, String outputConfigPath) throws IOException {
        FileWriter fileWriter = new FileWriter(outputConfigPath);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(UTF_8);
        XMLWriter writer = new XMLWriter(fileWriter, format);
        writer.write(outDocument);
        writer.close();
    }

    private static void loadProps() {
        Props prop = new Props("common.properties");
        tempDirectory = prop.getStr("tempDirectory");
          arcVecPath = prop.getStr("arcVecPath");
           arcConfigPath = prop.getStr("arcConfigPath");
          customSvgPath = prop.getStr("customSvgPath");
          customConfigPath = prop.getStr("customConfigPath");
          outputConfigPath = prop.getStr("outputConfigPath");
          APP_DIR =prop.getStr("outPath");
          outDrawablePath = APP_DIR+"app\\src\\main\\res\\drawable\\";
          System.out.println(tempDirectory);
    }


    private static void loadCustomIcons(List<Layout> finalObj) throws DocumentException, UnsupportedEncodingException {
        FileUtil.mkdir(tempDirectory +"drawable\\");
        SvgToVector.loadSvgToVector(customSvgPath, tempDirectory +"drawable\\");

        Document lawnDocument = getDocument(customConfigPath);
        copyDrawables(finalObj, tempDirectory, "ccmsd_", lawnDocument);
    }

    private static void loadIconConfigFromArcIcons(String arcVecPath, String arcConfigPath, List<Layout> finalObj, String prefix) throws DocumentException, IOException {
        SvgToVector.loadSvgToVector(arcVecPath, tempDirectory);
        System.out.println("SVG to vec "+arcVecPath +" completed");
        Document aDocument = getDocument(arcConfigPath);
        copyVectorToRepo(finalObj, aDocument,prefix);
    }

    private static void copyVectorToRepo(List<Layout> finalObj, Document aDocument, String prefix) throws DocumentException, IOException {
        List<Element> aList = getElements(aDocument, "item");
        for (Element element : aList) {
            String component = element.attribute("component").getValue();
            String[] comps = component
                .replaceAll("ComponentInfo\\{","")
                .replaceAll("}","")
                .replaceAll("ComponentInfo\\(","")
                .replaceAll("\\)","")
                .split("/");
           String drawable =element.attribute("drawable").getValue();
            if(!FileUtil.isFile(tempDirectory +drawable+".xml")) {
                continue;
            }
            for(String comp : comps) {
//                 String comp =  comps[0];
//                if(!comp.endsWith("Activity")) {
                    if(!pkgExists(finalObj,comp)) {
                        Layout out = new Layout();
                        out.setName(StrUtil.upperFirst(drawable));
                        out.setComponent(component);
                        out.setPkg(comp);
                        out.setDrawable(prefix+drawable);
                        finalObj.add(out);
                        if(prefix.equalsIgnoreCase("arc_")){
                            System.out.println("Updating arc_ icons " + tempDirectory+drawable+".xml");
                            updatePath(tempDirectory+drawable+".xml","android:strokeWidth",String.valueOf(3));
                        }
                        if(prefix.equalsIgnoreCase("frost_")){
                            System.out.println("Updating frost_ icons " + tempDirectory+drawable+".xml");
                            updatePath(tempDirectory+drawable+".xml","android:strokeWidth",String.valueOf(3));
                            updatePath(tempDirectory+drawable+".xml","android:strokeColor","#000000");
                        }
                        FileUtil.copyFile(tempDirectory+drawable+".xml",outDrawablePath+prefix+drawable+".xml",REPLACE_EXISTING);
                    }
//                }
            }
        }
    }

    private static void updatePath(String xmlPath, String key, String value) throws DocumentException, IOException {
        Document aDocument = getDocument(xmlPath);
        String keyWithoutNameSpace = key.substring(key.indexOf(":")+1,key.length());
        for(Element e : aDocument.getRootElement().elements("path")){
           Attribute attr = e.attribute(keyWithoutNameSpace);
           if(attr!=null){
               attr.setValue(String.valueOf(value));
            }else {
               e.addAttribute(key, String.valueOf(value));
           }
        }
        writeDocumentToFile(aDocument,xmlPath);
    }

    private static Document ObjectToXml(List<Layout> finalObj) {
        List<Layout> obj = finalObj.stream().distinct().collect(Collectors.toList());
        Document doc = new DefaultDocument();
        doc.addElement("icons");
        List<String> ignoreList = Arrays.asList("com.google.android.deskclock","com.google.android.calendar");
        for(Layout out : obj){
            if(!ignoreList.contains(out.getPkg())) {
                doc.getRootElement().addElement("icon")
                        .addAttribute("drawable", out.getDrawable().startsWith("@drawable") ? out.getDrawable() : "@drawable/" + out.getDrawable())
                        .addAttribute("package", out.getPkg())
//                .addAttribute("component", out.getComponent())
                        .addAttribute("name", out.name);
            }
        }
         return doc;
    }

    private static boolean pkgExists(List<Layout> finalObj, final String comp) {
        return finalObj.stream().anyMatch(f->f.getPkg().equals(comp));
    }

    private static void loadLawnIcons( List<Layout> finalObj,String rootPath,String prefix) throws DocumentException, UnsupportedEncodingException {
        Document lawnDocument = getDocument(rootPath+"xml\\grayscale_icon_map.xml");
        copyDrawables(finalObj, rootPath, prefix, lawnDocument);
    }

    private static void copyDrawables(List<Layout> finalObj, String rootPath, String prefix, Document lawnDocument) throws DocumentException, UnsupportedEncodingException {
        List<Element> lawnList = getElements(lawnDocument, "icon");
        for (Element element : lawnList) {
            Layout out = new Layout();
            String name = element.attribute("name").getValue();
            String pkg = element.attribute("package").getValue();
            String drawable =element.attribute("drawable").getValue().replaceAll("@drawable/","");
            if(drawable.contains("@array") && !FileUtil.isFile(outDrawablePath+"drawable\\"+drawable+".xml"))
                continue;
            if(pkgExists(finalObj,pkg)) {
                continue;
            }
            out.setName(name);
            out.setPkg(pkg);
            out.setDrawable(prefix +drawable);
            finalObj.add(out);
           List<String> fileList = FileUtil.listFileNames(rootPath +"drawable\\");
            for(String filename: fileList){
               if(filename.startsWith(drawable)) {
                   if(filename.startsWith(drawable+".xml")) {
                       FileUtil.copyFile(rootPath + "drawable\\" + filename, outDrawablePath + "\\" + prefix + filename, REPLACE_EXISTING);
                   }else{
                       FileUtil.copyFile(rootPath + "drawable\\" + filename, outDrawablePath + "\\" +filename, REPLACE_EXISTING);
                   }
               }
           }
        }
    }

    private static List<Element> getElements(Document document, String path) throws DocumentException {
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.elements(path);
        return list;
    }

    private static Document getDocument(String xmlPath) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.setEncoding(UTF_8);
        Document document = reader.read(xmlPath);
        return document;
    }
}
