cmake_minimum_required(VERSION 3.4.1)
add_library(check SHARED src/main/cpp/checkFile.cpp)
target_link_libraries(check
        android
        log)