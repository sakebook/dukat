
declare interface AstNode {
}


declare interface Declaration extends AstNode {
}

declare interface MemberDeclaration extends Declaration {}

declare class ClassDeclaration implements Declaration {
    name: String;
}

declare class InterfaceDeclaration implements Declaration {}

declare class ObjectLiteral implements ParameterValue {};

declare class VariableDeclaration implements MemberDeclaration {
    name: String;
    type: TypeDeclaration;
}

declare class PropertyDeclaration implements MemberDeclaration {
    name: string;
    type: TypeDeclaration;
    immmutable: boolean;
}

declare class Expression implements Declaration {
    kind: TypeDeclaration;
    meta: String;
}

declare interface ParameterValue extends Declaration {}

declare class ParameterDeclaration {
  name: String;
  type: ParameterValue;
}

declare class DocumentRoot implements AstNode {
    declarations: Declaration[]
}

declare class AstTree {
    root: DocumentRoot;
}


declare class TypeParameter {
    name: String;
}

type ClassLikeDeclaration = ClassDeclaration | InterfaceDeclaration

declare class TypeDeclaration implements ParameterValue {
    constructor(value: string, params: Array<ParameterValue>);
}

declare class FunctionDeclaration implements MemberDeclaration {}
declare class MethodDeclaration implements MemberDeclaration {}

declare class FunctionTypeDeclaration implements ParameterValue {}
