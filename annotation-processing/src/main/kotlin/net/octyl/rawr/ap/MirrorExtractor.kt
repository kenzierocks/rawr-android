package net.octyl.rawr.ap

import com.google.auto.common.MoreTypes
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleAnnotationValueVisitor8

class MirrorExtractor : SimpleAnnotationValueVisitor8<DeclaredType, Void>() {
    override fun visitType(p0: TypeMirror?, p1: Void?): DeclaredType {
        return MoreTypes.asDeclared(p0)
    }
}