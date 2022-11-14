package app.lawnchair.lawnicons.helper

fun main() {
    val rootFolder = ".."
    val sourceDirectory = "$rootFolder/svgs/"
    val darkResourceDirectory = "$rootFolder/app/src/dark/res"
    val lightResourceDirectory = "$rootFolder/app/src/light/res"
    val appFilterFile = "$rootFolder/app/assets/appfilter.xml"

    val processSvg = SvgFilesProcessor()
    val configProcessor = ConfigProcessor()
    // Convert svg to drawable in runtime
    processSvg.process(sourceDirectory, "$darkResourceDirectory/drawable", "dark")
    processSvg.process(sourceDirectory, "$lightResourceDirectory/drawable", "dark")

    // Read appfilter xml and create icon, drawable xml file.
    configProcessor.loadAndCreateConfigs(
        appFilterFile,
        darkResourceDirectory,
        lightResourceDirectory,
    )
    println("SvgToVector task completed")
}
