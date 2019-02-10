package org.jetbrains.dukat.ast.model.model

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class InterfaceModel(
        val name: String,
        val members: List<MemberDeclaration>,
        val companionObject: ObjectNode,
        val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val annotations: MutableList<AnnotationNode>
) : ClassLikeNode, ClassLikeModel {
    override val generatedReferenceNodes: MutableList<GeneratedInterfaceReferenceNode>
        get() = throw Exception("this exists for historical reason and will be removed")
}