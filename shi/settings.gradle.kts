rootProject.name = "com.storyteller_f.shi"

val l = listOf("config-core", "filter-core", "sort-core")
val home = System.getProperty("user.home")
l.forEach {
    include(it)
    project(":$it").projectDir = file("$home/AndroidStudioProjects/FilterUIProject/$it")
}
include("shi-config")
project(":shi-config").projectDir = file("../amiqin/shi-config")
