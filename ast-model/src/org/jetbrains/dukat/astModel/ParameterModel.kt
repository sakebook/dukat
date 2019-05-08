package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.marker.TypeModel
import org.jetbrains.dukat.astCommon.AstEntity

data class ParameterModel(
        val name: String,
        val type: TypeModel,
        val initializer: TypeModel?,

        val vararg: Boolean,
        val optional: Boolean
) : AstEntity