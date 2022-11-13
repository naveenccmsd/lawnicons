package app.lawnchair.lawnicons.helper

import java.io.IOException
import org.apache.commons.text.WordUtils
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.tree.DefaultDocument

class SvgToVector {

    val rootFolder = ".."
    val sourceDirectory = "$rootFolder/svgs/"
    val darkRes = "$rootFolder/app/src/dark/res"
    val lightRes = "$rootFolder/app/src/light/res"
    private val commonUtil = CommonUtil()

    fun loadSvgToVector(sourceDirectory: String?, destDirectory: String?, mode: String?) {
        if (null != sourceDirectory && sourceDirectory.isNotEmpty()) {
            val processor = SvgFilesProcessor(sourceDirectory, destDirectory, mode)
            processor.process()
        }
    }

    @Throws(DocumentException::class, IOException::class)
    fun createConfigs(appFilterFile: String) {
        val compStart = "ComponentInfo{"
        val compEnd = "}"
        val root = commonUtil.getDocument(appFilterFile)!!
        val aList = commonUtil.getElements(root, "item")
        val map: MutableMap<String, String> = HashMap()
        assert(aList != null)
        for (element in aList!!) {
            val component = element!!.attribute("component").value
            val drawable = element.attribute("drawable").value
            if (component.startsWith(compStart) && component.endsWith(compEnd)) {
                val comps = component.substring(compStart.length, component.length - compEnd.length)
                map[comps] = drawable
            }
        }
        val sortedMap = commonUtil.sortedMapByValues(map)!!
        createDrawable(sortedMap, "$darkRes/xml/drawable.xml")
        createDrawable(sortedMap, "$lightRes/xml/drawable.xml")
        createIconMap(sortedMap, "$darkRes/xml/theme_config.xml")
        createIconMap(sortedMap, "$lightRes/xml/theme_config.xml")
        commonUtil.writeDocumentToFile(root, "$darkRes/xml/appfilter.xml")
        commonUtil.writeDocumentToFile(root, "$lightRes/xml/appfilter.xml")
    }

    @Throws(IOException::class)
    private fun createIconMap(map: Map<String, String>?, filename: String) {
        val doc: Document = DefaultDocument()
        doc.addElement("icons")
        map?.forEach { (key, value) ->
            val comps = key.split("/").toTypedArray()
            doc.rootElement.addElement("icon").addAttribute("drawable", "@drawable/$value")
                .addAttribute("package", comps[0])
                .addAttribute("name", WordUtils.capitalize(value.replace("_".toRegex(), " ")))
        }
        commonUtil.writeDocumentToFile(doc, filename)
    }

    @Throws(IOException::class)
    private fun createDrawable(map: Map<String, String>?, filename: String) {
        val doc: Document = DefaultDocument()
        doc.addElement("resources")
        doc.rootElement.addElement("version").addText("1")
        map!!.values.stream().distinct().forEach { drawable: String? ->
            doc.rootElement.addElement("item").addAttribute("drawable", drawable)
        }
        commonUtil.writeDocumentToFile(doc, filename)
    }
}

fun main() {
    val app = SvgToVector()
    app.loadSvgToVector(app.sourceDirectory, app.darkRes + "/drawable", "dark")
    app.loadSvgToVector(app.sourceDirectory, app.lightRes + "/drawable", "light")
    app.createConfigs(app.rootFolder + "/app/assets/appfilter.xml")
    println("SvgToVector task completed")
}
