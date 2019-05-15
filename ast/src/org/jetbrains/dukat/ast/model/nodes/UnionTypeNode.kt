package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class UnionTypeNode(
        val params: List<ParameterValueDeclaration>,

        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : TypeNode