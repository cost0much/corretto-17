/*
 * reserved comment block
 * DO NOT REMOVE OR ALTER!
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Const;
import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.Utility;

/**
 * Super class for InvokeInstruction and FieldInstruction, since they have some methods in common!
 */
public abstract class FieldOrMethod extends CPInstruction implements LoadClass {

    /**
     * Empty constructor needed for Instruction.readInstruction. Not to be used otherwise.
     */
    FieldOrMethod() {
        // no init
    }

    /**
     * @param index to constant pool
     */
    protected FieldOrMethod(final short opcode, final int index) {
        super(opcode, index);
    }

    /**
     * @return name of the referenced class/interface
     * @deprecated If the instruction references an array class, this method will return "java.lang.Object". For code
     *             generated by Java 1.5, this answer is sometimes wrong (e.g., if the "clone()" method is called on an
     *             array). A better idea is to use the {@link #getReferenceType(ConstantPoolGen)} method, which correctly
     *             distinguishes between class types and array types.
     *
     */
    @Deprecated
    public String getClassName(final ConstantPoolGen cpg) {
        final ConstantPool cp = cpg.getConstantPool();
        final ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        final String className = cp.getConstantString(cmr.getClassIndex(), Const.CONSTANT_Class);
        if (className.startsWith("[")) {
            // Turn array classes into java.lang.Object.
            return "java.lang.Object";
        }
        return Utility.pathToPackage(className);
    }

    /**
     * @return type of the referenced class/interface
     * @deprecated If the instruction references an array class, the ObjectType returned will be invalid. Use
     *             getReferenceType() instead.
     */
    @Deprecated
    public ObjectType getClassType(final ConstantPoolGen cpg) {
        return ObjectType.getInstance(getClassName(cpg));
    }

    /**
     * Gets the ObjectType of the method return or field.
     *
     * @return type of the referenced class/interface
     * @throws ClassGenException when the field is (or method returns) an array,
     */
    @Override
    public ObjectType getLoadClassType(final ConstantPoolGen cpg) {
        final ReferenceType rt = getReferenceType(cpg);
        if (rt instanceof ObjectType) {
            return (ObjectType) rt;
        }
        throw new ClassGenException(rt.getClass().getCanonicalName() + " " + rt.getSignature() + " does not represent an ObjectType");
    }

    /**
     * @return name of referenced method/field.
     */
    public String getName(final ConstantPoolGen cpg) {
        final ConstantPool cp = cpg.getConstantPool();
        final ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        final ConstantNameAndType cnat = (ConstantNameAndType) cp.getConstant(cmr.getNameAndTypeIndex());
        return ((ConstantUtf8) cp.getConstant(cnat.getNameIndex())).getBytes();
    }

    /**
     * Gets the reference type representing the class, interface, or array class referenced by the instruction.
     *
     * @param cpg the ConstantPoolGen used to create the instruction
     * @return an ObjectType (if the referenced class type is a class or interface), or an ArrayType (if the referenced
     *         class type is an array class)
     */
    public ReferenceType getReferenceType(final ConstantPoolGen cpg) {
        final ConstantPool cp = cpg.getConstantPool();
        final ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        String className = cp.getConstantString(cmr.getClassIndex(), Const.CONSTANT_Class);
        if (className.startsWith("[")) {
            return (ArrayType) Type.getType(className);
        }
        className = Utility.pathToPackage(className);
        return ObjectType.getInstance(className);
    }

    /**
     * @return signature of referenced method/field.
     */
    public String getSignature(final ConstantPoolGen cpg) {
        final ConstantPool cp = cpg.getConstantPool();
        final ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        final ConstantNameAndType cnat = (ConstantNameAndType) cp.getConstant(cmr.getNameAndTypeIndex());
        return ((ConstantUtf8) cp.getConstant(cnat.getSignatureIndex())).getBytes();
    }
}
