package com.github.xxeddiexxx.blazorquickswitch

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.LocalFileSystem

class BlazorQuickSwitchAction : AnAction() {

    private val extensions = listOf("razor", "razor.cs", "razor.css", "razor.js")
    private val extensionsByLength = extensions.sortedByDescending { it.length }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val currentFile = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val currentFilePath = currentFile.path

        val (basePath, currentExtension) = extractBaseAndExtension(currentFilePath) ?: return
        val newPath = cycleExtension(basePath, currentExtension) ?: return
        if (newPath == currentFilePath) return

        val newFile = LocalFileSystem.getInstance().findFileByPath(newPath) ?: return

        FileEditorManager.getInstance(project).closeFile(currentFile)
        OpenFileDescriptor(project, newFile).navigate(true)
    }

    override fun update(event: AnActionEvent) {
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabledAndVisible = file != null && isBlazorFile(file.name)
    }

    private fun extractBaseAndExtension(filePath: String): Pair<String, String>? {
        for (ext in extensionsByLength) {
            if (filePath.endsWith(".$ext")) {
                return Pair(filePath.dropLast(ext.length + 1), ext)
            }
        }
        return null
    }

    private fun cycleExtension(basePath: String, currentExtension: String): String? {
        val currentIndex = extensions.indexOf(currentExtension)
        if (currentIndex == -1) return null

        for (i in 1 until extensions.size) {
            val newIndex = (currentIndex + i) % extensions.size
            val newPath = "$basePath.${extensions[newIndex]}"
            val newFile = LocalFileSystem.getInstance().findFileByPath(newPath)
            if (newFile != null && newFile.exists()) return newPath
        }
        return null
    }

    private fun isBlazorFile(fileName: String): Boolean {
        return extensionsByLength.any { fileName.endsWith(".$it") }
    }
}
