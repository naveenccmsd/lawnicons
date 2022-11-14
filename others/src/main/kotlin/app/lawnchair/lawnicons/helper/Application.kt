package app.lawnchair.lawnicons.helper

fun main() {
    val rootFolder = ".."
    val sourceDirectory = "$rootFolder/svgs/"
    val darkResourceDirectory = "$rootFolder/app/src/dark/res"
    val lightResourceDirectory = "$rootFolder/app/src/light/res"
    val appFilterFile = "$rootFolder/app/assets/appfilter.xml"

    // Convert svg to drawable in runtime
    SvgFilesProcessor.process(sourceDirectory, "$darkResourceDirectory/drawable", "dark")
    SvgFilesProcessor.process(sourceDirectory, "$lightResourceDirectory/drawable", "light")

    // Read appfilter xml and create icon, drawable xml file.
    ConfigProcessor.loadAndCreateConfigs(
        appFilterFile,
        darkResourceDirectory,
        lightResourceDirectory,
    )
    println("SvgToVector task completed")
}
