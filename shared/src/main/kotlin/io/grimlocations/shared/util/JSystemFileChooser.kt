package io.grimlocations.shared.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException
import javax.swing.filechooser.FileFilter


class JSystemFileChooser : JFileChooser() {

    fun applyDirectoryOnly(): JSystemFileChooser {
        controlButtonsAreShown = true
        fileFilter = FolderFilter()
        fileSelectionMode = DIRECTORIES_ONLY
        return this
    }

    override fun updateUI() {
        var old = UIManager.getLookAndFeel()
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (ex: Throwable) {
            old = null
        }

        super.updateUI()

        if (old != null) {
            val background = UIManager.getColor("Label.background")
            setBackground(background)
            isOpaque = true
            try {
                UIManager.setLookAndFeel(old)
            } catch (ignored: UnsupportedLookAndFeelException) {
                logger.error("Should not have made it here.", ignored)
            }
        }
    }

    companion object {
        @JvmStatic
        val logger: Logger = LogManager.getLogger(JSystemFileChooser::class.java)

//        @JvmStatic
//        private fun findFilePane(parent: Container): FilePane? {
//            for (comp in parent.components) {
//                if (FilePane::class.java.isInstance(comp)) {
//                    return comp as FilePane
//                }
//                if (comp is Container) {
//                    val cont: Container = comp as Container
//                    if (cont.componentCount > 0) {
//                        val found = findFilePane(cont)
//                        if (found != null) {
//                            return found
//                        }
//                    }
//                }
//            }
//            return null
//        }
    }
}

class FolderFilter : FileFilter() {
    override fun accept(f: File?): Boolean {
        return f?.isDirectory ?: false
    }

    override fun getDescription(): String {
        return "Directories"
    }

}