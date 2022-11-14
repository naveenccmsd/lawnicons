package app.lawnchair.lawnicons.helper

import java.io.File
import java.io.FileWriter
import java.nio.file.Path
import org.apache.commons.io.FilenameUtils
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter

object XmlUtil {
    private val UTF_8 = Charsets.UTF_8.name()

    fun getElements(document: Document, path: String): List<Element> {
        return document.rootElement.elements(path)
    }

    fun getDocument(xmlPath: String?): Document {
        return SAXReader().apply { encoding = UTF_8 }.read(xmlPath)
    }

    fun getFileWithXMlExtension(target: Path): String {
        val svgFilePath = target.toFile().absolutePath
        val index = svgFilePath.lastIndexOf(".")
        return buildString {
            if (index != -1) {
                append(svgFilePath.substring(0, index))
            }
            append(".")
            append("xml")
        }
    }

    fun writeDocumentToFile(outDocument: Document, outputConfigPath: String) {
        File(FilenameUtils.getFullPath(outputConfigPath)).mkdirs()
        val fileWriter = FileWriter(outputConfigPath)
        val format = OutputFormat.createPrettyPrint().apply { encoding = UTF_8 }
        XMLWriter(fileWriter, format).apply {
            write(outDocument)
            close()
        }
        fileWriter.close()
    }
}
