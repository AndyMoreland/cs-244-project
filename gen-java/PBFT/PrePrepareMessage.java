/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package PBFT;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2014-11-30")
public class PrePrepareMessage implements org.apache.thrift.TBase<PrePrepareMessage, PrePrepareMessage._Fields>, java.io.Serializable, Cloneable, Comparable<PrePrepareMessage> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PrePrepareMessage");

  private static final org.apache.thrift.protocol.TField VIEWSTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("viewstamp", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField TRANSACTION_DIGEST_FIELD_DESC = new org.apache.thrift.protocol.TField("transactionDigest", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField MESSAGE_SIGNATURE_FIELD_DESC = new org.apache.thrift.protocol.TField("messageSignature", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new PrePrepareMessageStandardSchemeFactory());
    schemes.put(TupleScheme.class, new PrePrepareMessageTupleSchemeFactory());
  }

  public Viewstamp viewstamp; // required
  public ByteBuffer transactionDigest; // required
  public ByteBuffer messageSignature; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    VIEWSTAMP((short)1, "viewstamp"),
    TRANSACTION_DIGEST((short)2, "transactionDigest"),
    MESSAGE_SIGNATURE((short)3, "messageSignature");

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
        case 1: // VIEWSTAMP
          return VIEWSTAMP;
        case 2: // TRANSACTION_DIGEST
          return TRANSACTION_DIGEST;
        case 3: // MESSAGE_SIGNATURE
          return MESSAGE_SIGNATURE;
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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.VIEWSTAMP, new org.apache.thrift.meta_data.FieldMetaData("viewstamp", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Viewstamp.class)));
    tmpMap.put(_Fields.TRANSACTION_DIGEST, new org.apache.thrift.meta_data.FieldMetaData("transactionDigest", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Signature")));
    tmpMap.put(_Fields.MESSAGE_SIGNATURE, new org.apache.thrift.meta_data.FieldMetaData("messageSignature", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Signature")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PrePrepareMessage.class, metaDataMap);
  }

  public PrePrepareMessage() {
  }

  public PrePrepareMessage(
    Viewstamp viewstamp,
    ByteBuffer transactionDigest,
    ByteBuffer messageSignature)
  {
    this();
    this.viewstamp = viewstamp;
    this.transactionDigest = org.apache.thrift.TBaseHelper.copyBinary(transactionDigest);
    this.messageSignature = org.apache.thrift.TBaseHelper.copyBinary(messageSignature);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PrePrepareMessage(PrePrepareMessage other) {
    if (other.isSetViewstamp()) {
      this.viewstamp = new Viewstamp(other.viewstamp);
    }
    if (other.isSetTransactionDigest()) {
      this.transactionDigest = other.transactionDigest;
    }
    if (other.isSetMessageSignature()) {
      this.messageSignature = other.messageSignature;
    }
  }

  public PrePrepareMessage deepCopy() {
    return new PrePrepareMessage(this);
  }

  @Override
  public void clear() {
    this.viewstamp = null;
    this.transactionDigest = null;
    this.messageSignature = null;
  }

  public Viewstamp getViewstamp() {
    return this.viewstamp;
  }

  public PrePrepareMessage setViewstamp(Viewstamp viewstamp) {
    this.viewstamp = viewstamp;
    return this;
  }

  public void unsetViewstamp() {
    this.viewstamp = null;
  }

  /** Returns true if field viewstamp is set (has been assigned a value) and false otherwise */
  public boolean isSetViewstamp() {
    return this.viewstamp != null;
  }

  public void setViewstampIsSet(boolean value) {
    if (!value) {
      this.viewstamp = null;
    }
  }

  public byte[] getTransactionDigest() {
    setTransactionDigest(org.apache.thrift.TBaseHelper.rightSize(transactionDigest));
    return transactionDigest == null ? null : transactionDigest.array();
  }

  public ByteBuffer bufferForTransactionDigest() {
    return org.apache.thrift.TBaseHelper.copyBinary(transactionDigest);
  }

  public PrePrepareMessage setTransactionDigest(byte[] transactionDigest) {
    this.transactionDigest = transactionDigest == null ? (ByteBuffer)null : ByteBuffer.wrap(Arrays.copyOf(transactionDigest, transactionDigest.length));
    return this;
  }

  public PrePrepareMessage setTransactionDigest(ByteBuffer transactionDigest) {
    this.transactionDigest = org.apache.thrift.TBaseHelper.copyBinary(transactionDigest);
    return this;
  }

  public void unsetTransactionDigest() {
    this.transactionDigest = null;
  }

  /** Returns true if field transactionDigest is set (has been assigned a value) and false otherwise */
  public boolean isSetTransactionDigest() {
    return this.transactionDigest != null;
  }

  public void setTransactionDigestIsSet(boolean value) {
    if (!value) {
      this.transactionDigest = null;
    }
  }

  public byte[] getMessageSignature() {
    setMessageSignature(org.apache.thrift.TBaseHelper.rightSize(messageSignature));
    return messageSignature == null ? null : messageSignature.array();
  }

  public ByteBuffer bufferForMessageSignature() {
    return org.apache.thrift.TBaseHelper.copyBinary(messageSignature);
  }

  public PrePrepareMessage setMessageSignature(byte[] messageSignature) {
    this.messageSignature = messageSignature == null ? (ByteBuffer)null : ByteBuffer.wrap(Arrays.copyOf(messageSignature, messageSignature.length));
    return this;
  }

  public PrePrepareMessage setMessageSignature(ByteBuffer messageSignature) {
    this.messageSignature = org.apache.thrift.TBaseHelper.copyBinary(messageSignature);
    return this;
  }

  public void unsetMessageSignature() {
    this.messageSignature = null;
  }

  /** Returns true if field messageSignature is set (has been assigned a value) and false otherwise */
  public boolean isSetMessageSignature() {
    return this.messageSignature != null;
  }

  public void setMessageSignatureIsSet(boolean value) {
    if (!value) {
      this.messageSignature = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case VIEWSTAMP:
      if (value == null) {
        unsetViewstamp();
      } else {
        setViewstamp((Viewstamp)value);
      }
      break;

    case TRANSACTION_DIGEST:
      if (value == null) {
        unsetTransactionDigest();
      } else {
        setTransactionDigest((ByteBuffer)value);
      }
      break;

    case MESSAGE_SIGNATURE:
      if (value == null) {
        unsetMessageSignature();
      } else {
        setMessageSignature((ByteBuffer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case VIEWSTAMP:
      return getViewstamp();

    case TRANSACTION_DIGEST:
      return getTransactionDigest();

    case MESSAGE_SIGNATURE:
      return getMessageSignature();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case VIEWSTAMP:
      return isSetViewstamp();
    case TRANSACTION_DIGEST:
      return isSetTransactionDigest();
    case MESSAGE_SIGNATURE:
      return isSetMessageSignature();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof PrePrepareMessage)
      return this.equals((PrePrepareMessage)that);
    return false;
  }

  public boolean equals(PrePrepareMessage that) {
    if (that == null)
      return false;

    boolean this_present_viewstamp = true && this.isSetViewstamp();
    boolean that_present_viewstamp = true && that.isSetViewstamp();
    if (this_present_viewstamp || that_present_viewstamp) {
      if (!(this_present_viewstamp && that_present_viewstamp))
        return false;
      if (!this.viewstamp.equals(that.viewstamp))
        return false;
    }

    boolean this_present_transactionDigest = true && this.isSetTransactionDigest();
    boolean that_present_transactionDigest = true && that.isSetTransactionDigest();
    if (this_present_transactionDigest || that_present_transactionDigest) {
      if (!(this_present_transactionDigest && that_present_transactionDigest))
        return false;
      if (!this.transactionDigest.equals(that.transactionDigest))
        return false;
    }

    boolean this_present_messageSignature = true && this.isSetMessageSignature();
    boolean that_present_messageSignature = true && that.isSetMessageSignature();
    if (this_present_messageSignature || that_present_messageSignature) {
      if (!(this_present_messageSignature && that_present_messageSignature))
        return false;
      if (!this.messageSignature.equals(that.messageSignature))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_viewstamp = true && (isSetViewstamp());
    list.add(present_viewstamp);
    if (present_viewstamp)
      list.add(viewstamp);

    boolean present_transactionDigest = true && (isSetTransactionDigest());
    list.add(present_transactionDigest);
    if (present_transactionDigest)
      list.add(transactionDigest);

    boolean present_messageSignature = true && (isSetMessageSignature());
    list.add(present_messageSignature);
    if (present_messageSignature)
      list.add(messageSignature);

    return list.hashCode();
  }

  @Override
  public int compareTo(PrePrepareMessage other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetViewstamp()).compareTo(other.isSetViewstamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetViewstamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.viewstamp, other.viewstamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTransactionDigest()).compareTo(other.isSetTransactionDigest());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTransactionDigest()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.transactionDigest, other.transactionDigest);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMessageSignature()).compareTo(other.isSetMessageSignature());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessageSignature()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.messageSignature, other.messageSignature);
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
    StringBuilder sb = new StringBuilder("PrePrepareMessage(");
    boolean first = true;

    sb.append("viewstamp:");
    if (this.viewstamp == null) {
      sb.append("null");
    } else {
      sb.append(this.viewstamp);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("transactionDigest:");
    if (this.transactionDigest == null) {
      sb.append("null");
    } else {
      sb.append(this.transactionDigest);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("messageSignature:");
    if (this.messageSignature == null) {
      sb.append("null");
    } else {
      sb.append(this.messageSignature);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (viewstamp != null) {
      viewstamp.validate();
    }
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PrePrepareMessageStandardSchemeFactory implements SchemeFactory {
    public PrePrepareMessageStandardScheme getScheme() {
      return new PrePrepareMessageStandardScheme();
    }
  }

  private static class PrePrepareMessageStandardScheme extends StandardScheme<PrePrepareMessage> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PrePrepareMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // VIEWSTAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.viewstamp = new Viewstamp();
              struct.viewstamp.read(iprot);
              struct.setViewstampIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TRANSACTION_DIGEST
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.transactionDigest = iprot.readBinary();
              struct.setTransactionDigestIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // MESSAGE_SIGNATURE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.messageSignature = iprot.readBinary();
              struct.setMessageSignatureIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, PrePrepareMessage struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.viewstamp != null) {
        oprot.writeFieldBegin(VIEWSTAMP_FIELD_DESC);
        struct.viewstamp.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.transactionDigest != null) {
        oprot.writeFieldBegin(TRANSACTION_DIGEST_FIELD_DESC);
        oprot.writeBinary(struct.transactionDigest);
        oprot.writeFieldEnd();
      }
      if (struct.messageSignature != null) {
        oprot.writeFieldBegin(MESSAGE_SIGNATURE_FIELD_DESC);
        oprot.writeBinary(struct.messageSignature);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PrePrepareMessageTupleSchemeFactory implements SchemeFactory {
    public PrePrepareMessageTupleScheme getScheme() {
      return new PrePrepareMessageTupleScheme();
    }
  }

  private static class PrePrepareMessageTupleScheme extends TupleScheme<PrePrepareMessage> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PrePrepareMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetViewstamp()) {
        optionals.set(0);
      }
      if (struct.isSetTransactionDigest()) {
        optionals.set(1);
      }
      if (struct.isSetMessageSignature()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetViewstamp()) {
        struct.viewstamp.write(oprot);
      }
      if (struct.isSetTransactionDigest()) {
        oprot.writeBinary(struct.transactionDigest);
      }
      if (struct.isSetMessageSignature()) {
        oprot.writeBinary(struct.messageSignature);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PrePrepareMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.viewstamp = new Viewstamp();
        struct.viewstamp.read(iprot);
        struct.setViewstampIsSet(true);
      }
      if (incoming.get(1)) {
        struct.transactionDigest = iprot.readBinary();
        struct.setTransactionDigestIsSet(true);
      }
      if (incoming.get(2)) {
        struct.messageSignature = iprot.readBinary();
        struct.setMessageSignatureIsSet(true);
      }
    }
  }

}
