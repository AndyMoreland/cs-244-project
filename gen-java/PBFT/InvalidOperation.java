/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package PBFT;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

import java.nio.ByteBuffer;
import java.util.*;

public class InvalidOperation extends TException implements org.apache.thrift.TBase<InvalidOperation, InvalidOperation._Fields>, java.io.Serializable, Cloneable, Comparable<InvalidOperation> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("InvalidOperation");

  private static final org.apache.thrift.protocol.TField ERROR_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("errorType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField ERROR_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("errorMessage", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField SIGNATURE_FIELD_DESC = new org.apache.thrift.protocol.TField("signature", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new InvalidOperationStandardSchemeFactory());
    schemes.put(TupleScheme.class, new InvalidOperationTupleSchemeFactory());
  }

  public int errorType; // required
  public String errorMessage; // required
  public ByteBuffer signature; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ERROR_TYPE((short)1, "errorType"),
    ERROR_MESSAGE((short)2, "errorMessage"),
    SIGNATURE((short)3, "signature");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ERROR_TYPE
          return ERROR_TYPE;
        case 2: // ERROR_MESSAGE
          return ERROR_MESSAGE;
        case 3: // SIGNATURE
          return SIGNATURE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ERRORTYPE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ERROR_TYPE, new org.apache.thrift.meta_data.FieldMetaData("errorType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ERROR_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("errorMessage", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SIGNATURE, new org.apache.thrift.meta_data.FieldMetaData("signature", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Signature")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(InvalidOperation.class, metaDataMap);
  }

  public InvalidOperation() {
  }

  public InvalidOperation(
    int errorType,
    String errorMessage,
    ByteBuffer signature)
  {
    this();
    this.errorType = errorType;
    setErrorTypeIsSet(true);
    this.errorMessage = errorMessage;
    this.signature = signature;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public InvalidOperation(InvalidOperation other) {
    __isset_bitfield = other.__isset_bitfield;
    this.errorType = other.errorType;
    if (other.isSetErrorMessage()) {
      this.errorMessage = other.errorMessage;
    }
    if (other.isSetSignature()) {
      this.signature = other.signature;
    }
  }

  public InvalidOperation deepCopy() {
    return new InvalidOperation(this);
  }

  @Override
  public void clear() {
    setErrorTypeIsSet(false);
    this.errorType = 0;
    this.errorMessage = null;
    this.signature = null;
  }

  public int getErrorType() {
    return this.errorType;
  }

  public InvalidOperation setErrorType(int errorType) {
    this.errorType = errorType;
    setErrorTypeIsSet(true);
    return this;
  }

  public void unsetErrorType() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ERRORTYPE_ISSET_ID);
  }

  /** Returns true if field errorType is set (has been assigned a value) and false otherwise */
  public boolean isSetErrorType() {
    return EncodingUtils.testBit(__isset_bitfield, __ERRORTYPE_ISSET_ID);
  }

  public void setErrorTypeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ERRORTYPE_ISSET_ID, value);
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public InvalidOperation setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public void unsetErrorMessage() {
    this.errorMessage = null;
  }

  /** Returns true if field errorMessage is set (has been assigned a value) and false otherwise */
  public boolean isSetErrorMessage() {
    return this.errorMessage != null;
  }

  public void setErrorMessageIsSet(boolean value) {
    if (!value) {
      this.errorMessage = null;
    }
  }

  public byte[] getSignature() {
    setSignature(org.apache.thrift.TBaseHelper.rightSize(signature));
    return signature == null ? null : signature.array();
  }

  public ByteBuffer bufferForSignature() {
    return signature;
  }

  public InvalidOperation setSignature(byte[] signature) {
    setSignature(signature == null ? (ByteBuffer)null : ByteBuffer.wrap(signature));
    return this;
  }

  public InvalidOperation setSignature(ByteBuffer signature) {
    this.signature = signature;
    return this;
  }

  public void unsetSignature() {
    this.signature = null;
  }

  /** Returns true if field signature is set (has been assigned a value) and false otherwise */
  public boolean isSetSignature() {
    return this.signature != null;
  }

  public void setSignatureIsSet(boolean value) {
    if (!value) {
      this.signature = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ERROR_TYPE:
      if (value == null) {
        unsetErrorType();
      } else {
        setErrorType((Integer)value);
      }
      break;

    case ERROR_MESSAGE:
      if (value == null) {
        unsetErrorMessage();
      } else {
        setErrorMessage((String)value);
      }
      break;

    case SIGNATURE:
      if (value == null) {
        unsetSignature();
      } else {
        setSignature((ByteBuffer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ERROR_TYPE:
      return Integer.valueOf(getErrorType());

    case ERROR_MESSAGE:
      return getErrorMessage();

    case SIGNATURE:
      return getSignature();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ERROR_TYPE:
      return isSetErrorType();
    case ERROR_MESSAGE:
      return isSetErrorMessage();
    case SIGNATURE:
      return isSetSignature();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof InvalidOperation)
      return this.equals((InvalidOperation)that);
    return false;
  }

  public boolean equals(InvalidOperation that) {
    if (that == null)
      return false;

    boolean this_present_errorType = true;
    boolean that_present_errorType = true;
    if (this_present_errorType || that_present_errorType) {
      if (!(this_present_errorType && that_present_errorType))
        return false;
      if (this.errorType != that.errorType)
        return false;
    }

    boolean this_present_errorMessage = true && this.isSetErrorMessage();
    boolean that_present_errorMessage = true && that.isSetErrorMessage();
    if (this_present_errorMessage || that_present_errorMessage) {
      if (!(this_present_errorMessage && that_present_errorMessage))
        return false;
      if (!this.errorMessage.equals(that.errorMessage))
        return false;
    }

    boolean this_present_signature = true && this.isSetSignature();
    boolean that_present_signature = true && that.isSetSignature();
    if (this_present_signature || that_present_signature) {
      if (!(this_present_signature && that_present_signature))
        return false;
      if (!this.signature.equals(that.signature))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(InvalidOperation other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetErrorType()).compareTo(other.isSetErrorType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetErrorType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.errorType, other.errorType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetErrorMessage()).compareTo(other.isSetErrorMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetErrorMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.errorMessage, other.errorMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSignature()).compareTo(other.isSetSignature());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSignature()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.signature, other.signature);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("InvalidOperation(");
    boolean first = true;

    sb.append("errorType:");
    sb.append(this.errorType);
    first = false;
    if (!first) sb.append(", ");
    sb.append("errorMessage:");
    if (this.errorMessage == null) {
      sb.append("null");
    } else {
      sb.append(this.errorMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("signature:");
    if (this.signature == null) {
      sb.append("null");
    } else {
      sb.append(this.signature);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class InvalidOperationStandardSchemeFactory implements SchemeFactory {
    public InvalidOperationStandardScheme getScheme() {
      return new InvalidOperationStandardScheme();
    }
  }

  private static class InvalidOperationStandardScheme extends StandardScheme<InvalidOperation> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, InvalidOperation struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ERROR_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.errorType = iprot.readI32();
              struct.setErrorTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ERROR_MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.errorMessage = iprot.readString();
              struct.setErrorMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SIGNATURE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.signature = iprot.readBinary();
              struct.setSignatureIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, InvalidOperation struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ERROR_TYPE_FIELD_DESC);
      oprot.writeI32(struct.errorType);
      oprot.writeFieldEnd();
      if (struct.errorMessage != null) {
        oprot.writeFieldBegin(ERROR_MESSAGE_FIELD_DESC);
        oprot.writeString(struct.errorMessage);
        oprot.writeFieldEnd();
      }
      if (struct.signature != null) {
        oprot.writeFieldBegin(SIGNATURE_FIELD_DESC);
        oprot.writeBinary(struct.signature);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class InvalidOperationTupleSchemeFactory implements SchemeFactory {
    public InvalidOperationTupleScheme getScheme() {
      return new InvalidOperationTupleScheme();
    }
  }

  private static class InvalidOperationTupleScheme extends TupleScheme<InvalidOperation> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, InvalidOperation struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetErrorType()) {
        optionals.set(0);
      }
      if (struct.isSetErrorMessage()) {
        optionals.set(1);
      }
      if (struct.isSetSignature()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetErrorType()) {
        oprot.writeI32(struct.errorType);
      }
      if (struct.isSetErrorMessage()) {
        oprot.writeString(struct.errorMessage);
      }
      if (struct.isSetSignature()) {
        oprot.writeBinary(struct.signature);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, InvalidOperation struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.errorType = iprot.readI32();
        struct.setErrorTypeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.errorMessage = iprot.readString();
        struct.setErrorMessageIsSet(true);
      }
      if (incoming.get(2)) {
        struct.signature = iprot.readBinary();
        struct.setSignatureIsSet(true);
      }
    }
  }

}

