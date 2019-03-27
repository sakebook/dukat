package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.Declaration


interface
ParameterValueDeclaration : Declaration {
    val nullable: Boolean
    var meta: ParameterValueDeclaration?
}