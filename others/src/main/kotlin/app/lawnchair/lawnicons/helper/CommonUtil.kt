package app.lawnchair.lawnicons.helper

import com.google.common.base.Charsets
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Path
import org.apache.commons.io.FilenameUtils
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter

class CommonUtil {
    @Throws(DocumentException::class)
    fun getElements(
        document: Document,
        path: String?,
    ): List<Element?>? {
        val rootElement = document.rootElement
        return rootElement.elements(path)
    }

    fun sortedMapByValues(map: Map<String, String>): Map<String, String>? {
        val result2: MutableMap<String, String> = LinkedHashMap()
        map.entries.stream()
            .sorted(java.util.Map.Entry.comparingByValue())
            .forEachOrdered { (key, value): Map.Entry<String, String> ->
                result2[key] = value
            }
        return result2
    }

    @Throws(DocumentException::class)
    fun getDocument(xmlPath: String?): Document? {
        val reader = SAXReader()
        reader.encoding = Charsets.UTF_8.name()
        return reader.read(xmlPath)
    }

    fun getFileWithXMlExtension(
        target: Path,
        extension: String?,
        extensionSuffix: String?,
    ): String? {
        val svgFilePath = target.toFile().absolutePath
        val svgBaseFile = StringBuilder()
        val index = svgFilePath.lastIndexOf(".")
        if (index != -1) {
            val subStr = svgFilePath.substring(0, index)
            svgBaseFile.append(subStr)
        }
        svgBaseFile.append(extensionSuffix ?: "")
        svgBaseFile.append(".")
        svgBaseFile.append(extension)
        return svgBaseFile.toString()
    }

    @Throws(IOException::class)
    fun writeDocumentToFile(outDocument: Document?, outputConfigPath: String?) {
        File(FilenameUtils.getFullPath(outputConfigPath)).mkdirs()
        val fileWriter = FileWriter(outputConfigPath)
        val format = OutputFormat.createPrettyPrint()
        format.encoding = Charsets.UTF_8.name()
        val writer = XMLWriter(fileWriter, format)
        writer.write(outDocument)
        writer.close()
        fileWriter.close()
    }
}
