package app.lawnchair.lawnicons.helper

import org.apache.commons.text.WordUtils
import org.dom4j.Document
import org.dom4j.tree.DefaultDocument

object ConfigProcessor {
    private const val ITEM = "item"
    private const val COMPONENT = "component"
    private const val PACKAGE = "package"
    private const val DRAWABLE = "drawable"
    private const val ICONS = "icons"
    private const val ICON = "icon"
    private const val RESOURCES = "resources"
    private const val NAME = "name"
    private const val VERSION = "version"

    fun loadAndCreateConfigs(
        appFilterFile: String,
        darkResourceDirectory: String,
        lightResourceDirectory: String,
    ) {
        val drawableMap = hashMapOf<String, String>()
        val iconMap = hashMapOf<String, String>()
        val appFilterDocument = loadConfigFromXml(appFilterFile, drawableMap, iconMap)
        val sortedDrawableMap = drawableMap.toList().sortedBy { (_, value) -> value }.toMap()

        listOf(darkResourceDirectory, lightResourceDirectory).forEach {
            // Create Drawable files
            writeDrawableToFile(sortedDrawableMap, "$it/xml/drawable.xml")
            // Create Icon Map files
            writeIconMapToFile(sortedDrawableMap, iconMap, "$it/xml/theme_config.xml")
            // Write AppFilter to resource directory
            XmlUtil.writeDocumentToFile(appFilterDocument, "$it/xml/appfilter.xml")
        }
    }

    private fun loadConfigFromXml(
        appFilterFile: String,
        drawableMap: MutableMap<String, String>,
        iconMap: MutableMap<String, String>,
    ): Document {
        val componentStart = "ComponentInfo{"
        val componentEnd = "}"
        val appFilterDocument = XmlUtil.getDocument(appFilterFile)
        val appFilterElements = XmlUtil.getElements(appFilterDocument, ITEM)
        for (element in appFilterElements) {
            val componentInfo = element.attribute(COMPONENT).value
            val drawable = element.attribute(DRAWABLE).value
            val name = element.attribute(NAME).value
            if (componentInfo.startsWith(componentStart) && componentInfo.endsWith(componentEnd)) {
                val component = componentInfo.substring(
                    componentStart.length,
                    componentInfo.length - componentEnd.length,
                )
                drawableMap[component] = drawable
                iconMap[component] = name
            }
        }
        return appFilterDocument
    }

    private fun writeIconMapToFile(
        drawableMap: Map<String, String>,
        iconMap: Map<String, String>,
        filename: String,
    ) {
        val iconsDocument = DefaultDocument().apply { addElement(ICONS) }
        drawableMap.forEach { (componentInfo, drawable) ->
            val component = componentInfo.split("/").toTypedArray()
            val name = iconMap.getOrDefault(
                componentInfo,
                WordUtils.capitalize(drawable.replace("_".toRegex(), " ")),
            )
            iconsDocument.rootElement.addElement(ICON)
                .addAttribute(DRAWABLE, "@drawable/$drawable")
                .addAttribute(PACKAGE, component[0])
                .addAttribute(NAME, name)
        }
        XmlUtil.writeDocumentToFile(iconsDocument, filename)
    }

    private fun writeDrawableToFile(drawableMap: Map<String, String>, filename: String) {
        val resourceDocument = DefaultDocument().apply {
            addElement(RESOURCES)
            rootElement.addElement(VERSION).addText("1")
        }
        drawableMap.values.distinct().forEach { drawable: String? ->
            resourceDocument.rootElement.addElement(ITEM).addAttribute(DRAWABLE, drawable)
        }
        XmlUtil.writeDocumentToFile(resourceDocument, filename)
    }
}
