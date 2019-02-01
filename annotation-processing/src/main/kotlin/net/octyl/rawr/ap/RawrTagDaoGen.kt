package net.octyl.rawr.ap

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.GeneratedAnnotationSpecs
import com.google.auto.common.MoreElements
import com.google.auto.common.MoreTypes
import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import kotlinx.metadata.KmClassVisitor
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import net.octyl.rawr.ap.annotation.AddTagDao
import java.time.Instant
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * Generates the various TagDao variants, at the location specified by the annotated package.
 */
@AutoService(Processor::class)
class RawrTagDaoGen : AbstractProcessor() {
    override fun getSupportedAnnotationTypes() = setOf(AddTagDao::class.java.name)
    override fun getSupportedSourceVersion() = SourceVersion.latest()!!

    override fun process(annotations: Set<TypeElement>, env: RoundEnvironment): Boolean {
        if (annotations.isEmpty()) {
            return false
        }
        env.getElementsAnnotatedWith(AddTagDao::class.java)
                .map(MoreElements::asType)
                .forEach { type ->
                    var pkg = type.enclosingElement
                    if (pkg.kind != ElementKind.PACKAGE) {
                        throw IllegalStateException("Must be on a package, found ${pkg.kind}")
                    }
                    pkg = MoreElements.asPackage(pkg)

                    val addTagDao = MoreElements.getAnnotationMirror(type, AddTagDao::class.java).apply {
                        if (!isPresent) {
                            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "No annotation found.", pkg)
                            return@forEach
                        }
                    }.get()

                    val tagClassElement = addTagDao.getTypeElement("tagClass")
                    val tagClasses = findSealedClassesOf(tagClassElement)
                    val tagDaoParent = TagDaoParent(type)
                    writeTagDaos(pkg, tagClasses, tagDaoParent)
                }
        return true
    }

    private fun AnnotationMirror.getTypeElement(name: String): TypeElement {
        return AnnotationMirrors.getAnnotationValue(this, name)
                .accept(MirrorExtractor(), null)
                .run(MoreTypes::asTypeElement)
    }

    private fun findSealedClassesOf(tagClassElement: TypeElement): List<ClassName> {
        val kotlinMetadata = tagClassElement.getAnnotation(Metadata::class.java)

        val metadata = KotlinClassMetadata.read(KotlinClassHeader(
                kind = kotlinMetadata.kind,
                metadataVersion = kotlinMetadata.metadataVersion,
                bytecodeVersion = kotlinMetadata.bytecodeVersion,
                data1 = kotlinMetadata.data1,
                data2 = kotlinMetadata.data2,
                extraString = kotlinMetadata.extraString,
                packageName = kotlinMetadata.packageName,
                extraInt = kotlinMetadata.extraInt
        ))
        return when (metadata) {
            null -> {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "No metadata!",
                        tagClassElement)
                emptyList()
            }
            is KotlinClassMetadata.Class -> {
                sealedClassesOf(metadata)
            }
            else -> throw IllegalStateException("Unexpected metadata ${metadata.javaClass}")
        }
    }

    private fun sealedClassesOf(metadata: KotlinClassMetadata.Class): List<ClassName> {
        val sealedClasses = mutableListOf<ClassName>()
        metadata.accept(object : KmClassVisitor() {
            override fun visitSealedSubclass(name: kotlinx.metadata.ClassName) {
                sealedClasses.add(ClassName.bestGuess(name.replace('/', '.')))
            }
        })
        return sealedClasses
    }

    /*
     *

    @Dao
    interface StringTagDao : TagDao<StringTag> {
    @Query()
    override fun getSongTags(songId: RawrId): List<StringTag>
    }
     */

    private fun writeTagDaos(pkg: PackageElement, tagClasses: List<ClassName>, tagDaoParent: TagDaoParent) {
        tagClasses.forEach { className ->
            val typeSpec = createTagDao(className, tagDaoParent)
            val file: JavaFile = JavaFile.builder(pkg.qualifiedName.toString(), typeSpec)
                    .indent("    ")
                    .build()
            file.writeTo(processingEnv.filer)
        }
    }

    private val query = ClassName.get("androidx.room", "Query")
    private fun query(sql: String): AnnotationSpec {
        return AnnotationSpec.builder(query)
                .addMember("value", "\$S", sql)
                .build()
    }

    private fun createTagDao(tagClassName: ClassName, tagDaoParent: TagDaoParent): TypeSpec {
        val genTime = Instant.now()!!
        val tableName = tagClassName.simpleName()
        return TypeSpec.interfaceBuilder("${tableName}Dao").apply {
            val generatedInfo = when(tryAddGeneratedAnnotation(genTime)) {
                false -> """
                    <p>
                    Generated by ${javaClass.name} at $genTime.
                    </p>
                """.trimIndent()
                else -> ""
            }
            addJavadoc("\$L", """
                TagDao for $tagClassName.
            """.trimIndent() + generatedInfo + "\n")
            addAnnotation(ClassName.get("androidx.room", "Dao"))

            addModifiers(Modifier.PUBLIC)

            addSuperinterface(ParameterizedTypeName.get(tagDaoParent.className, tagClassName))

            addMethod(tagDaoParent.overrideMethod(tagClassName, "getSongTags").apply {
                addAnnotation(query("SELECT * FROM $tableName WHERE song_id == :songId"))
            }.build())
        }.build()
    }

    private val types: Types
        get() = processingEnv.typeUtils
    private val elements: Elements
        get() = processingEnv.elementUtils

    private fun TagDaoParent.overrideMethod(tagClassName: ClassName, name: String): MethodSpec.Builder {
        val tagTypeMirror = types.getDeclaredType(elements.getTypeElement(tagClassName.toString()))
        val parameterizedTagDao = types.getDeclaredType(element, tagTypeMirror)
        return MethodSpec.overriding(
                method(name),
                parameterizedTagDao,
                types
        ).apply {
            addModifiers(Modifier.ABSTRACT)
        }
    }

    private fun TypeSpec.Builder.tryAddGeneratedAnnotation(genTime: Instant): Boolean {
        GeneratedAnnotationSpecs.generatedAnnotationSpec(
                processingEnv.elementUtils,
                processingEnv.sourceVersion,
                RawrTagDaoGen::class.java
        ).map {
            it.toBuilder()
                    .addMember("date", "\$S", genTime.toString())
                    .build()!!
        }.apply {
            if (isPresent) {
                addAnnotation(get())
            }
            return isPresent
        }
    }
}