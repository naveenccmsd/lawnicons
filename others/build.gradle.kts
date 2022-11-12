plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    application
}
dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.android.tools:sdk-common:30.3.1")
    implementation("com.android.tools:common:30.3.1")
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.14")
    implementation("org.apache.xmlgraphics:batik-codec:1.14")
    implementation("org.apache.commons:commons-text:1.10.0")
}

application {
    mainClass.set("app.lawnchair.lawnicons.helper.SvgToVector")
}
