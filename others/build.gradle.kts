plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

application {
    mainClass.set("app.lawnchair.lawnicons.helper.SvgToVector")
}

dependencies {
    implementation("com.android.tools:sdk-common:30.3.1")
    implementation("com.android.tools:common:30.3.1")
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.14")
    implementation("org.apache.xmlgraphics:batik-codec:1.14")
    implementation("org.apache.commons:commons-text:1.10.0")
}
