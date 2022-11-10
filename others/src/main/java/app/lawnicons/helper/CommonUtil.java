package app.lawnicons.helper;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CommonUtil {

    public static List<Element> getElements(Document document, String path) throws DocumentException {
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.elements(path);
        return list;
    }

    public static Document getDocument(String xmlPath) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.setEncoding(Charsets.UTF_8.name());
        Document document = reader.read(xmlPath);
        return document;
    }

    public static String getFileWithXMlExtension(Path target, String extension, String extensionSuffix){
        String svgFilePath =  target.toFile().getAbsolutePath();
        StringBuilder svgBaseFile = new StringBuilder();
        int index = svgFilePath.lastIndexOf(".");
        if(index != -1){
            String subStr = svgFilePath.substring(0, index);
            svgBaseFile.append(subStr);
        }
        svgBaseFile.append(null != extensionSuffix ? extensionSuffix : "");
        svgBaseFile.append(".");
        svgBaseFile.append(extension);
        return svgBaseFile.toString();
    }
    public static void writeDocumentToFile(Document outDocument, String outputConfigPath) throws IOException {
        new File(FilenameUtils.getFullPath(outputConfigPath)).mkdirs();
        FileWriter fileWriter = new FileWriter(outputConfigPath);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(Charsets.UTF_8.name());
        XMLWriter writer = new XMLWriter(fileWriter, format);
        writer.write(outDocument);
        writer.close();
        fileWriter.close();
    }
}
