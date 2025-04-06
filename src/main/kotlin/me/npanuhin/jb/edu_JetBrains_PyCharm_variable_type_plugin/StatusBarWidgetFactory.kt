package me.npanuhin.jb.edu_JetBrains_PyCharm_variable_type_plugin

import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.project.Project


class VariableTypeInStatusBarFactory : StatusBarWidgetFactory {
    override fun getId(): String = "VariableTypeInStatusBar"
    override fun getDisplayName(): String = "Variable Type In Status Bar"
    override fun createWidget(project: Project): StatusBarWidget = VariableTypeInStatusBar()
}
