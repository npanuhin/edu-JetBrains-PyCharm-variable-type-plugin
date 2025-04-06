package me.npanuhin.jb.edu_JetBrains_PyCharm_variable_type_plugin

import com.intellij.openapi.wm.StatusBarWidget.TextPresentation
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.wm.StatusBar
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.types.TypeEvalContext
import com.jetbrains.python.psi.PyExpression
import com.jetbrains.python.psi.PyFile
import java.awt.Component


class VariableTypeInStatusBar : StatusBarWidget, TextPresentation {
    override fun ID() = "VariableTypeInStatusBar"
    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT
    override fun getTooltipText(): String = "Displays the type of the variable under the caret"
    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    private var currentType: String? = null
    override fun getText(): String = if (currentType != null) {
        "Variable Type: $currentType"
    } else {
        ""
    }

    override fun install(statusBar: StatusBar) =
        EditorFactory.getInstance().eventMulticaster.addCaretListener(CustomCaretListener(statusBar), this)

    inner class CustomCaretListener(private val statusBar: StatusBar) : CaretListener {
        override fun caretPositionChanged(event: CaretEvent) {
            val curProject = event.editor.project ?: return
            val caretOffset = event.caret?.offset ?: return

            currentType = runCatching {
                val psiFile = PsiDocumentManager.getInstance(curProject).getPsiFile(event.editor.document)
                    ?: return@runCatching null
                if (psiFile !is PyFile) return@runCatching null

                val elementAtCaret = psiFile.findElementAt(caretOffset) ?: return@runCatching null
                val pyExpression = PsiTreeUtil.getParentOfType(elementAtCaret, PyExpression::class.java, false)
                    ?: return@runCatching null

                TypeEvalContext.userInitiated(curProject, psiFile).getType(pyExpression)?.name ?: "Unknown Type"
            }.getOrNull()

            statusBar.updateWidget(ID())
        }
    }
}
