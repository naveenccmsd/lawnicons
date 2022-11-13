package app.lawnchair.lawnicons.helper

import com.android.ide.common.vectordrawable.Svg2Vector
import com.google.common.base.Charsets
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.io.path.pathString
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter

class SvgFilesProcessor {

    private var sourceSvgPath: Path? = null
    private var destinationVectorPath: Path? = null
    private var extension: String? = null
    private var extensionSuffix: String? = null
    private val commonUtil = CommonUtil()
    private var mode: String? = null

    constructor(sourceDirectory: String, destDirectory: String?, mode: String?){
        this.sourceSvgPath = Paths.get(sourceDirectory)
        this.destinationVectorPath = Paths.get(destDirectory)
        this.mode = mode
        this.extensionSuffix = ""
        this.extension = "xml"
    }
    fun process() {
        try {
            val options = EnumSet.of(FileVisitOption.FOLLOW_LINKS)
            //check first if source is a directory
            if (Files.isDirectory(sourceSvgPath)) {
                Files.walkFileTree(
                    sourceSvgPath, options, Int.MAX_VALUE,
                    fileVisitor(),
                )
            } else {
                println("source not a directory")
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
    private fun convertToVector(source: Path, target: Path) {
        // convert only if it is .svg
        if (source.fileName.toString().endsWith(".svg")) {
            val targetFile = commonUtil.getFileWithXMlExtension(target, extension, extensionSuffix)
                ?: return
            val fileOutputStream = FileOutputStream(targetFile)
            Svg2Vector.parseSvgToXml(source.toFile(), fileOutputStream)
            try {
                if (mode == "dark") {
                    updatePath(targetFile, "android:strokeColor", "#000")
                    updatePath(targetFile, "android:fillColor", "#000")
                } else if (mode == "light") {
                    updatePath(targetFile, "android:strokeColor", "#fff")
                    updatePath(targetFile, "android:fillColor", "#fff")
                }
            } catch (e: DocumentException) {
                throw RuntimeException(e)
            }
        } else {
            println("Skipping file as its not svg " + source.fileName)
        }
    }

    @Throws(DocumentException::class, IOException::class)
    private fun updatePath(xmlPath: String, key: String, value: String) {
        val aDocument = commonUtil.getDocument(xmlPath)
        val keyWithoutNameSpace = key.substring(key.indexOf(":") + 1)
        if (aDocument != null && aDocument.rootElement != null) {
            for (e in aDocument.rootElement.elements("path")) {
                val attr = e.attribute(keyWithoutNameSpace)
                if (attr != null) {
                    if (attr.value != "#00000000") {
                        attr.value = value
                    }
                }
            }
            updateDocumentToFile(aDocument, xmlPath)
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
