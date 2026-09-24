cmake_minimum_required(VERSION 3.22.1)

project("arvo_native")

add_library(
    arvo_native
    SHARED
    native-lib.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    arvo_native
    ${log-lib}
)

# Rigueur Sommité : Alignement 16 KB pour compatibilité Android 15+
target_link_options(arvo_native PRIVATE "-Wl,-z,max-page-size=16384")
