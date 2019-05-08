package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.isPrimitive
import org.jetbrains.dukat.ast.model.nodes.metadata.MuteMetadata
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetrbains.dukat.nodeLowering.ParameterValueLowering


private fun mapPrimitiveValue(value: String): String {
    return when (value) {
        "any" -> "Any"
        "boolean" -> "Boolean"
        "string" -> "String"
        "number" -> "Number"
        "Object" -> "Any"
        else -> value
    }
}

private fun NameNode.mapPrimitive(): NameNode {
    return when (this) {
        is IdentifierNode -> {
            copy(value = mapPrimitiveValue(value))
        }
        else -> this
    }
}

private class PrimitiveClassLowering : ParameterValueLowering {
    override fun lowerTypeNode(declaration: TypeValueNode): TypeValueNode {
        if (declaration.value == IdentifierNode("Function")) {
            return declaration.copy(params = listOf(TypeValueNode("*", emptyList())))
        }

        var value = declaration.value.mapPrimitive()
        var nullable = declaration.nullable
        var meta = declaration.meta

        if (declaration.isPrimitive("undefined") || declaration.isPrimitive("null")) {
            value = IdentifierNode("Nothing")
            nullable = true
            meta = MuteMetadata()
        }

        return declaration.copy(
                value = value,
                params = declaration.params.map { lowerParameterValue(it) },
                nullable = nullable,
                meta = meta
        )
    }

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration {
        return declaration.copy(name = mapPrimitiveValue(declaration.name))
    }
}

fun DocumentRootNode.lowerPrimitives(): DocumentRootNode {
    return PrimitiveClassLowering().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerPrimitives() = transform { it.lowerPrimitives() }