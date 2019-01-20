package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.extended.ObjectLiteral

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntity(key: String) = get(key) as Map<String, Any?>?

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntitiesList(key: String) = get(key) as List<Map<String, Any?>>

private fun <T:Declaration> Map<String, Any?>.mapEntities(key: String, mapper: (Map<String, Any?>) -> T) =
        getEntitiesList(key).map(mapper)

private fun Map<String, Any?>.getInitializerExpression(): Expression? {
    return getEntity("initializer")?.let {
        val expression = it.toAst<Declaration>()

        if (expression is Expression) {
            if (expression.kind.value == "@@DEFINED_EXTERNALLY") {
                expression
            } else throw Exception("unkown initializer")
        } else null
    }
}

private fun Map<String, Any?>.parameterDeclarationToAst() =
        ParameterDeclaration(
                get("name") as String,
                (getEntity("type"))!!.toAst(),
                getInitializerExpression()
        )

@Suppress("UNCHECKED_CAST")
fun <T : AstNode> Map<String, Any?>.toAst(): T {
    val reflectionType = get("reflection") as String
    val res: Declaration
    if (reflectionType == Expression::class.simpleName) {
        res = Expression(
                (getEntity("kind"))!!.toAst(),
                get("meta") as String
        )
    } else if (reflectionType == TypeDeclaration::class.simpleName) {
        res = TypeDeclaration(if (get("value") is String) {
            get("value") as String
        } else {
            throw Exception("failed to create type declaration from ${this}")
        }, mapEntities("params") { it.toAst<ParameterValue>() })
    } else if (reflectionType == FunctionDeclaration::class.simpleName) {
        res = FunctionDeclaration(
                get("name") as String,
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") { it.toAst<TypeParameter>() }
        )
    } else if (reflectionType == MethodDeclaration::class.simpleName) {
            res = MethodDeclaration(
                    get("name") as String,
                    mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                    getEntity("type")!!.toAst(),
                    mapEntities("typeParameters") { it.toAst<TypeParameter>() },
                    get("override") as Boolean,
                    get("operator") as Boolean
            )
    } else if (reflectionType == FunctionTypeDeclaration::class.simpleName) {
        res = FunctionTypeDeclaration(
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst()
        )
    } else if (reflectionType == ParameterDeclaration::class.simpleName) {
        res = parameterDeclarationToAst()
    } else if (reflectionType == VariableDeclaration::class.simpleName) {
        res = VariableDeclaration(get("name") as String, getEntity("type")!!.toAst())
    } else if (reflectionType == PropertyDeclaration::class.simpleName) {
        res = PropertyDeclaration(
                get("name") as String,
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") {it.toAst<TypeParameter>()},
                get("getter") as Boolean,
                get("setter") as Boolean,
                get("override") as Boolean
        )
    } else if (reflectionType == DocumentRoot::class.simpleName) {
        res = DocumentRoot(get("packageName") as String, mapEntities("declarations") {
            it.toAst<Declaration>()
        })
    } else if (reflectionType == TypeParameter::class.simpleName) {
        res = TypeParameter(get("name") as String, mapEntities("constraints") { it.toAst<ParameterValue>() })
    } else if (reflectionType == ClassDeclaration::class.simpleName) {
        res = ClassDeclaration(
                get("name") as String,
                mapEntities("members") {it.toAst<MemberDeclaration>()},
                mapEntities("typeParameters") {it.toAst<TypeParameter>()},
                mapEntities("parentEntities") {it.toAst<ClassLikeDeclaration>()}
        )
    } else if (reflectionType == InterfaceDeclaration::class.simpleName) {
        res = InterfaceDeclaration(
                get("name") as String,
                mapEntities("members") {it.toAst<MemberDeclaration>()},
                mapEntities("typeParameters") {it.toAst<TypeParameter>()},
                mapEntities("parentEntities") { it.toAst<InterfaceDeclaration>() }
        )
    } else if (reflectionType == ObjectLiteral::class.simpleName) {
        res = ObjectLiteral(mapEntities("members") {it.toAst<MemberDeclaration>()})
    } else {
        throw Exception("failed to create declaration from mapper: ${this}")
    }

    return res as T
}