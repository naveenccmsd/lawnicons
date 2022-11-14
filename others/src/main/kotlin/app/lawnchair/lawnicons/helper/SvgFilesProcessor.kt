package app.lawnchair.lawnicons.helper

import com.android.ide.common.vectordrawable.Svg2Vector
import com.google.common.base.Charsets
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.EnumSet
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter

class SvgFilesProcessor {
    private var sourceSvgPath: Path? = null
    private var destinationVectorPath: Path? = null
    private val extension: String? = "xml"
    private val extensionSuffix: String? = ""
    private var mode: String? = "dark"
    private val xmlUtil = XmlUtil()

    fun process(sourceDirectory: String?, destDirectory: String?, mode: String?) {
        this.sourceSvgPath = Paths.get(sourceDirectory)
        this.destinationVectorPath = Paths.get(destDirectory)
        this.mode = mode
        try {
            val options = EnumSet.of(FileVisitOption.FOLLOW_LINKS)
            // check first if source is a directory
            if (Files.isDirectory(sourceSvgPath)) {
                Files.walkFileTree(sourceSvgPath, options, Int.MAX_VALUE, fileVisitor())
            } else {
                println("source not a directory")
            }
        } catch (e: IOException) {
            println("IOException " + e.message)
        }
    }

    private fun fileVisitor() = object : FileVisitor<Path> {
        override fun postVisitDirectory(
            dir: Path?,
            exc: IOException?,
        ): FileVisitResult {
            return FileVisitResult.CONTINUE
        }

        override fun preVisitDirectory(
            dir: Path?,
            attrs: BasicFileAttributes?,
        ): FileVisitResult {
            // Skip folder which is processing svgs to xml
            if (dir == destinationVectorPath) {
                return FileVisitResult.SKIP_SUBTREE
            }
            val newDirectory = destinationVectorPath!!.resolve(
                sourceSvgPath!!.relativize(dir),
            )
            try {
                Files.createDirectories(newDirectory)
            } catch (ex: FileAlreadyExistsException) {
                ex.printStackTrace()
                println("FileAlreadyExistsException $ex")
            } catch (x: IOException) {
                x.printStackTrace()
                println("SKIP_SUBTREE  $x")
                return FileVisitResult.SKIP_SUBTREE
            }
            return FileVisitResult.CONTINUE
        }

        @Throws(IOException::class)
        override fun visitFile(
            file: Path?,
            attrs: BasicFileAttributes?,
        ): FileVisitResult {
            if (file != null) {
                convertToVector(
                    file,
                    destinationVectorPath!!.resolve(sourceSvgPath!!.relativize(file)),
                )
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(
            file: Path,
            exc: IOException,
        ): FileVisitResult {
            return FileVisitResult.CONTINUE
        }
    }

    @Throws(IOException::class)
    private fun convertToVector(svgSource: Path, vectorTargetPath: Path) {
        // convert only if it is .svg
        if (svgSource.fileName.toString().endsWith(".svg")) {
            val targetFile =
                xmlUtil.getFileWithXMlExtension(vectorTargetPath, extension, extensionSuffix)
                    ?: return
            val fileOutputStream = FileOutputStream(targetFile)
            Svg2Vector.parseSvgToXml(svgSource.toFile(), fileOutputStream)
            try {
                if (mode == "dark") {
                    updateXmlPath(targetFile, "android:strokeColor", "#000")
                    updateXmlPath(targetFile, "android:fillColor", "#000")
                } else if (mode == "light") {
                    updateXmlPath(targetFile, "android:strokeColor", "#fff")
                    updateXmlPath(targetFile, "android:fillColor", "#fff")
                }
            } catch (e: DocumentException) {
                throw RuntimeException(e)
            }
        } else {
            println("Skipping file as its not svg " + svgSource.fileName)
        }
    }

    @Throws(DocumentException::class, IOException::class)
    private fun updateXmlPath(xmlPath: String, searchKey: String, attributeValue: String) {
        val xmlDocument = xmlUtil.getDocument(xmlPath)
        val keyWithoutNameSpace = searchKey.substring(searchKey.indexOf(":") + 1)
        if (xmlDocument != null && xmlDocument.rootElement != null) {
            for (e in xmlDocument.rootElement.elements("path")) {
                val attr = e.attribute(keyWithoutNameSpace)
                if (attr != null) {
                    if (attr.value != "#00000000") {
                        attr.value = attributeValue
                    }
                }
            }
            updateDocumentToFile(xmlDocument, xmlPath)
        }
    }

    @Throws(IOException::class)
    private fun updateDocumentToFile(outDocument: Document, outputConfigPath: String) {
        val fileWriter = FileWriter(outputConfigPath)
        val format = OutputFormat.createPrettyPrint()
        format.encoding = Charsets.UTF_8.name()
        val writer = XMLWriter(fileWriter, format)
        writer.write(outDocument)
        writer.close()
    }
}
