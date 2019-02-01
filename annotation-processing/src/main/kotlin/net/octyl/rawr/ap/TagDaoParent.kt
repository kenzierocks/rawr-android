package net.octyl.rawr.ap

import com.google.auto.common.MoreElements
import com.squareup.javapoet.ClassName
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

data class TagDaoParent(
        val element: TypeElement
) {
    val className = ClassName.get(element)

    fun method(name: String): ExecutableElement {
        val methods = element.enclosedElements.filter {
            it.kind == ElementKind.METHOD
        }.map(MoreElements::asExecutable).filter { it.simpleName.contentEquals(name) }
        return when {
            methods.isEmpty() -> throw IllegalStateException("No method named $name present!")
            methods.size == 1 -> methods[0]
            else -> throw IllegalStateException("Multiple methods named $name present!")
        }
    }
}