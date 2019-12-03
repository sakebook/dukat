package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor

class DescriptorContext(val config: JsConfig) {

    var currentPackageName: NameEntity = IdentifierEntity("")
    private val registeredDescriptors: MutableMap<NameEntity, ClassDescriptor> = mutableMapOf()
    private val setOfRegisteredDescriptors: MutableSet<ClassDescriptor> = mutableSetOf()
    private val registeredTypeAliases: MutableMap<NameEntity, TypeAliasDescriptor> = mutableMapOf()
    private val typeParameters: MutableMap<NameEntity, TypeParameterDescriptor> = mutableMapOf()
    val registeredImports: MutableList<String> = mutableListOf()
    private val registeredMethods: MutableMap<NameEntity, SimpleFunctionDescriptor> = mutableMapOf()
    private val registeredProperties: MutableMap<NameEntity, PropertyDescriptorImpl> = mutableMapOf()
    private val alreadyResolvedClasses: MutableSet<ClassDescriptor> = mutableSetOf()
    private val constraintToInitialize: MutableMap<TypeParameterDescriptorImpl, MutableList<KotlinType>> =
        mutableMapOf()

    fun registerMethod(methodFqName: NameEntity, methodDescriptor: SimpleFunctionDescriptor) {
        registeredMethods[methodFqName] = methodDescriptor
    }

    fun resolveMethod(classFqName: NameEntity, methodName: NameEntity): SimpleFunctionDescriptor? {
        return registeredMethods[classFqName.appendLeft(methodName)]
    }

    fun registerProperty(propertyFqName: NameEntity, propertyDescriptor: PropertyDescriptorImpl) {
        registeredProperties[propertyFqName] = propertyDescriptor
    }

    fun resolveProperty(classFqName: NameEntity, propertyName: NameEntity): PropertyDescriptorImpl? {
        return registeredProperties[classFqName.appendLeft(propertyName)]
    }

    fun registerDescriptor(name: NameEntity, descriptor: ClassDescriptor) {
        registeredDescriptors[currentPackageName.appendLeft(name)] = descriptor
        setOfRegisteredDescriptors += descriptor
    }

    fun registerTypeAlias(name: NameEntity, descriptor: TypeAliasDescriptor) {
        registeredTypeAliases[name] = descriptor
    }

    fun getDescriptor(name: NameEntity): ClassDescriptor? {
        return registeredDescriptors[name]
    }

    fun getTypeAlias(name: NameEntity): TypeAliasDescriptor? {
        return registeredTypeAliases[name]
    }

    fun getTypeAliasDescriptorByConstructor(typeConstructor: TypeConstructor): TypeAliasDescriptor? {
        return registeredTypeAliases.filter { (_, descriptor) -> descriptor.defaultType.constructor == typeConstructor }
            .values.firstOrNull()
    }

    fun registerTypeParameter(name: NameEntity, descriptor: TypeParameterDescriptor) {
        typeParameters[name] = descriptor
    }

    fun getTypeParameter(name: NameEntity): TypeParameterDescriptor? {
        return typeParameters[name]
    }

    fun removeTypeParameter(name: NameEntity) {
        typeParameters.remove(name)
    }

    fun getAllClassDescriptors(): List<ClassDescriptor> {
        return setOfRegisteredDescriptors.toList()
    }

    fun addResolved(classDescriptor: ClassDescriptor) {
        alreadyResolvedClasses += classDescriptor
    }

    fun shouldBeResolved(classDescriptor: ClassDescriptor): Boolean {
        return !alreadyResolvedClasses.contains(classDescriptor) &&
                setOfRegisteredDescriptors.contains(classDescriptor)
    }

    fun addConstraintInitialization(
        typeParameterDescriptor: TypeParameterDescriptorImpl,
        constraint: KotlinType
    ) {
        if (constraintToInitialize[typeParameterDescriptor] == null) {
            constraintToInitialize[typeParameterDescriptor] = mutableListOf()
        }
        constraintToInitialize[typeParameterDescriptor]!!.add(constraint)
    }

    fun canBeInitialized(typeParameterDescriptor: TypeParameterDescriptorImpl): Boolean {
        return constraintToInitialize[typeParameterDescriptor] == null
    }

    fun initializeConstraints() {
        for ((descriptor, constraints) in constraintToInitialize) {
            constraints.forEach { descriptor.addUpperBound(it) }
            descriptor.setInitialized()
        }
    }

}