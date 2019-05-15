package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.ownerContext.NodeOwner

interface ModelWithOwnerTypeLowering : ModelWithOwnerLowering {

    override fun lowerEnumNode(ownerContext: NodeOwner<EnumNode>): EnumNode {
        return ownerContext.node
    }

    fun lowerMethodModel(ownerContext: NodeOwner<MethodModel>): MethodModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeNode(NodeOwner(declaration.type, ownerContext))
        )
    }

    fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val declaration = ownerContext.node
        return declaration.copy(
                type = lowerTypeNode(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerMemberNode(ownerContext: NodeOwner<MemberNode>): MemberNode {
        val declaration = ownerContext.node
        return when (declaration) {
            is MethodModel -> lowerMethodModel(NodeOwner(declaration, ownerContext))
            is PropertyModel -> lowerPropertyModel(NodeOwner(declaration, ownerContext))
            is ConstructorModel -> lowerConstructorModel(NodeOwner(declaration, ownerContext))
            else -> {
                println("[WARN] [${this::class.simpleName}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>): FunctionModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeNode(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        return declaration.copy(type = lowerTypeNode(NodeOwner(declaration.type, ownerContext)))
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel {
        val declaration = ownerContext.node
        return declaration.copy(type = lowerTypeNode(NodeOwner(declaration.type, ownerContext)))
    }

    fun lowerHeritageNode(ownerContext: NodeOwner<HeritageModel>): HeritageModel {
        val heritageClause = ownerContext.node
        val typeParams = heritageClause.typeParams.map {
            lowerTypeNode(ownerContext.wrap(it))
        }
        return heritageClause.copy(typeParams = typeParams)
    }


    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members
                = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(NodeOwner(heritageClause, ownerContext))
                }
        )
    }


    fun lowerConstructorModel(ownerContext: NodeOwner<ConstructorModel>): ConstructorModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) }
        )
    }

    override fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>): ObjectModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) }
        )

    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(NodeOwner(heritageClause, ownerContext))
                }
        )
    }
}
