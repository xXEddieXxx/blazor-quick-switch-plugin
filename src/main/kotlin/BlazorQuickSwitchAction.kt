package org.example

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

class BlazorQuickSwitchAction : AnAction() {

    private val extensions = listOf("razor", "razor.cs", "razor.css", "razor.js")

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val currentFile = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val currentFilePath = currentFile.canonicalPath ?: return

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
        // Check longer compound extensions first to avoid partial matches
        // e.g., ".razor.cs" must match before ".razor"
        for (ext in extensions.sortedByDescending { it.length }) {
            if (filePath.endsWith(".$ext")) {
                return Pair(filePath.dropLast(ext.length + 1), ext)
            }
        }
        return null
    }

    private fun cycleExtension(basePath: String, currentExtension: String): String? {
        val currentIndex = extensions.indexOf(currentExtension)
        if (currentIndex == -1) return null

        for (i in 1..extensions.size) {
            val newIndex = (currentIndex + i) % extensions.size
            val newPath = "$basePath.${extensions[newIndex]}"
            val newFile = LocalFileSystem.getInstance().findFileByPath(newPath)
            if (newFile != null && newFile.exists()) return newPath
        }
        return null
    }

    private fun isBlazorFile(fileName: String): Boolean {
        return extensions.any { fileName.endsWith(".$it") }
    }
}
