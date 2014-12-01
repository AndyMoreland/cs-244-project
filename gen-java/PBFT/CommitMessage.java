/**
 * Autogenerated by Thrift Compiler (0.9.1)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommitMessage implements org.apache.thrift.TBase<CommitMessage, CommitMessage._Fields>, java.io.Serializable, Cloneable, Comparable<CommitMessage> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CommitMessage");

  private static final org.apache.thrift.protocol.TField SEQUENCE_NUMBER_FIELD_DESC = new org.apache.thrift.protocol.TField("sequenceNumber", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField CHECKPOINT_DIGEST_FIELD_DESC = new org.apache.thrift.protocol.TField("checkpointDigest", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField REPLICA_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("replicaId", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField MESSAGE_SIGNATURE_FIELD_DESC = new org.apache.thrift.protocol.TField("messageSignature", org.apache.thrift.protocol.TType.STRING, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new CommitMessageStandardSchemeFactory());
    schemes.put(TupleScheme.class, new CommitMessageTupleSchemeFactory());
  }

  public int sequenceNumber; // required
  public ByteBuffer checkpointDigest; // required
  public int replicaId; // required
  public ByteBuffer messageSignature; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SEQUENCE_NUMBER((short)1, "sequenceNumber"),
    CHECKPOINT_DIGEST((short)2, "checkpointDigest"),
    REPLICA_ID((short)3, "replicaId"),
    MESSAGE_SIGNATURE((short)4, "messageSignature");

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
        case 1: // SEQUENCE_NUMBER
          return SEQUENCE_NUMBER;
        case 2: // CHECKPOINT_DIGEST
          return CHECKPOINT_DIGEST;
        case 3: // REPLICA_ID
          return REPLICA_ID;
        case 4: // MESSAGE_SIGNATURE
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
  private static final int __SEQUENCENUMBER_ISSET_ID = 0;
  private static final int __REPLICAID_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SEQUENCE_NUMBER, new org.apache.thrift.meta_data.FieldMetaData("sequenceNumber", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.CHECKPOINT_DIGEST, new org.apache.thrift.meta_data.FieldMetaData("checkpointDigest", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Signature")));
    tmpMap.put(_Fields.REPLICA_ID, new org.apache.thrift.meta_data.FieldMetaData("replicaId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MESSAGE_SIGNATURE, new org.apache.thrift.meta_data.FieldMetaData("messageSignature", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Signature")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CommitMessage.class, metaDataMap);
  }

  public CommitMessage() {
  }

  public CommitMessage(
    int sequenceNumber,
    ByteBuffer checkpointDigest,
    int replicaId,
    ByteBuffer messageSignature)
  {
    this();
    this.sequenceNumber = sequenceNumber;
    setSequenceNumberIsSet(true);
    this.checkpointDigest = checkpointDigest;
    this.replicaId = replicaId;
    setReplicaIdIsSet(true);
    this.messageSignature = messageSignature;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CommitMessage(CommitMessage other) {
    __isset_bitfield = other.__isset_bitfield;
    this.sequenceNumber = other.sequenceNumber;
    if (other.isSetCheckpointDigest()) {
      this.checkpointDigest = other.checkpointDigest;
    }
    this.replicaId = other.replicaId;
    if (other.isSetMessageSignature()) {
      this.messageSignature = other.messageSignature;
    }
  }

  public CommitMessage deepCopy() {
    return new CommitMessage(this);
  }

  @Override
  public void clear() {
    setSequenceNumberIsSet(false);
    this.sequenceNumber = 0;
    this.checkpointDigest = null;
    setReplicaIdIsSet(false);
    this.replicaId = 0;
    this.messageSignature = null;
  }

  public int getSequenceNumber() {
    return this.sequenceNumber;
  }

  public CommitMessage setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
    setSequenceNumberIsSet(true);
    return this;
  }

  public void unsetSequenceNumber() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SEQUENCENUMBER_ISSET_ID);
  }

  /** Returns true if field sequenceNumber is set (has been assigned a value) and false otherwise */
  public boolean isSetSequenceNumber() {
    return EncodingUtils.testBit(__isset_bitfield, __SEQUENCENUMBER_ISSET_ID);
  }

  public void setSequenceNumberIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SEQUENCENUMBER_ISSET_ID, value);
  }

  public byte[] getCheckpointDigest() {
    setCheckpointDigest(org.apache.thrift.TBaseHelper.rightSize(checkpointDigest));
    return checkpointDigest == null ? null : checkpointDigest.array();
  }

  public ByteBuffer bufferForCheckpointDigest() {
    return checkpointDigest;
  }

  public CommitMessage setCheckpointDigest(byte[] checkpointDigest) {
    setCheckpointDigest(checkpointDigest == null ? (ByteBuffer)null : ByteBuffer.wrap(checkpointDigest));
    return this;
  }

  public CommitMessage setCheckpointDigest(ByteBuffer checkpointDigest) {
    this.checkpointDigest = checkpointDigest;
    return this;
  }

  public void unsetCheckpointDigest() {
    this.checkpointDigest = null;
  }

  /** Returns true if field checkpointDigest is set (has been assigned a value) and false otherwise */
  public boolean isSetCheckpointDigest() {
    return this.checkpointDigest != null;
  }

  public void setCheckpointDigestIsSet(boolean value) {
    if (!value) {
      this.checkpointDigest = null;
    }
  }

  public int getReplicaId() {
    return this.replicaId;
  }

  public CommitMessage setReplicaId(int replicaId) {
    this.replicaId = replicaId;
    setReplicaIdIsSet(true);
    return this;
  }

  public void unsetReplicaId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __REPLICAID_ISSET_ID);
  }

  /** Returns true if field replicaId is set (has been assigned a value) and false otherwise */
  public boolean isSetReplicaId() {
    return EncodingUtils.testBit(__isset_bitfield, __REPLICAID_ISSET_ID);
  }

  public void setReplicaIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __REPLICAID_ISSET_ID, value);
  }

  public byte[] getMessageSignature() {
    setMessageSignature(org.apache.thrift.TBaseHelper.rightSize(messageSignature));
    return messageSignature == null ? null : messageSignature.array();
  }

  public ByteBuffer bufferForMessageSignature() {
    return messageSignature;
  }

  public CommitMessage setMessageSignature(byte[] messageSignature) {
    setMessageSignature(messageSignature == null ? (ByteBuffer)null : ByteBuffer.wrap(messageSignature));
    return this;
  }

  public CommitMessage setMessageSignature(ByteBuffer messageSignature) {
    this.messageSignature = messageSignature;
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
    case SEQUENCE_NUMBER:
      if (value == null) {
        unsetSequenceNumber();
      } else {
        setSequenceNumber((Integer)value);
      }
      break;

    case CHECKPOINT_DIGEST:
      if (value == null) {
        unsetCheckpointDigest();
      } else {
        setCheckpointDigest((ByteBuffer)value);
      }
      break;

    case REPLICA_ID:
      if (value == null) {
        unsetReplicaId();
      } else {
        setReplicaId((Integer)value);
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
    case SEQUENCE_NUMBER:
      return Integer.valueOf(getSequenceNumber());

    case CHECKPOINT_DIGEST:
      return getCheckpointDigest();

    case REPLICA_ID:
      return Integer.valueOf(getReplicaId());

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
    case SEQUENCE_NUMBER:
      return isSetSequenceNumber();
    case CHECKPOINT_DIGEST:
      return isSetCheckpointDigest();
    case REPLICA_ID:
      return isSetReplicaId();
    case MESSAGE_SIGNATURE:
      return isSetMessageSignature();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof CommitMessage)
      return this.equals((CommitMessage)that);
    return false;
  }

  public boolean equals(CommitMessage that) {
    if (that == null)
      return false;

    boolean this_present_sequenceNumber = true;
    boolean that_present_sequenceNumber = true;
    if (this_present_sequenceNumber || that_present_sequenceNumber) {
      if (!(this_present_sequenceNumber && that_present_sequenceNumber))
        return false;
      if (this.sequenceNumber != that.sequenceNumber)
        return false;
    }

    boolean this_present_checkpointDigest = true && this.isSetCheckpointDigest();
    boolean that_present_checkpointDigest = true && that.isSetCheckpointDigest();
    if (this_present_checkpointDigest || that_present_checkpointDigest) {
      if (!(this_present_checkpointDigest && that_present_checkpointDigest))
        return false;
      if (!this.checkpointDigest.equals(that.checkpointDigest))
        return false;
    }

    boolean this_present_replicaId = true;
    boolean that_present_replicaId = true;
    if (this_present_replicaId || that_present_replicaId) {
      if (!(this_present_replicaId && that_present_replicaId))
        return false;
      if (this.replicaId != that.replicaId)
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
    return 0;
  }

  @Override
  public int compareTo(CommitMessage other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSequenceNumber()).compareTo(other.isSetSequenceNumber());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSequenceNumber()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sequenceNumber, other.sequenceNumber);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCheckpointDigest()).compareTo(other.isSetCheckpointDigest());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCheckpointDigest()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.checkpointDigest, other.checkpointDigest);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetReplicaId()).compareTo(other.isSetReplicaId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetReplicaId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.replicaId, other.replicaId);
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
    StringBuilder sb = new StringBuilder("CommitMessage(");
    boolean first = true;

    sb.append("sequenceNumber:");
    sb.append(this.sequenceNumber);
    first = false;
    if (!first) sb.append(", ");
    sb.append("checkpointDigest:");
    if (this.checkpointDigest == null) {
      sb.append("null");
    } else {
      sb.append(this.checkpointDigest);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("replicaId:");
    sb.append(this.replicaId);
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

  private static class CommitMessageStandardSchemeFactory implements SchemeFactory {
    public CommitMessageStandardScheme getScheme() {
      return new CommitMessageStandardScheme();
    }
  }

  private static class CommitMessageStandardScheme extends StandardScheme<CommitMessage> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CommitMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SEQUENCE_NUMBER
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.sequenceNumber = iprot.readI32();
              struct.setSequenceNumberIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CHECKPOINT_DIGEST
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.checkpointDigest = iprot.readBinary();
              struct.setCheckpointDigestIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // REPLICA_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.replicaId = iprot.readI32();
              struct.setReplicaIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // MESSAGE_SIGNATURE
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CommitMessage struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(SEQUENCE_NUMBER_FIELD_DESC);
      oprot.writeI32(struct.sequenceNumber);
      oprot.writeFieldEnd();
      if (struct.checkpointDigest != null) {
        oprot.writeFieldBegin(CHECKPOINT_DIGEST_FIELD_DESC);
        oprot.writeBinary(struct.checkpointDigest);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(REPLICA_ID_FIELD_DESC);
      oprot.writeI32(struct.replicaId);
      oprot.writeFieldEnd();
      if (struct.messageSignature != null) {
        oprot.writeFieldBegin(MESSAGE_SIGNATURE_FIELD_DESC);
        oprot.writeBinary(struct.messageSignature);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CommitMessageTupleSchemeFactory implements SchemeFactory {
    public CommitMessageTupleScheme getScheme() {
      return new CommitMessageTupleScheme();
    }
  }

  private static class CommitMessageTupleScheme extends TupleScheme<CommitMessage> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CommitMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetSequenceNumber()) {
        optionals.set(0);
      }
      if (struct.isSetCheckpointDigest()) {
        optionals.set(1);
      }
      if (struct.isSetReplicaId()) {
        optionals.set(2);
      }
      if (struct.isSetMessageSignature()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetSequenceNumber()) {
        oprot.writeI32(struct.sequenceNumber);
      }
      if (struct.isSetCheckpointDigest()) {
        oprot.writeBinary(struct.checkpointDigest);
      }
      if (struct.isSetReplicaId()) {
        oprot.writeI32(struct.replicaId);
      }
      if (struct.isSetMessageSignature()) {
        oprot.writeBinary(struct.messageSignature);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CommitMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.sequenceNumber = iprot.readI32();
        struct.setSequenceNumberIsSet(true);
      }
      if (incoming.get(1)) {
        struct.checkpointDigest = iprot.readBinary();
        struct.setCheckpointDigestIsSet(true);
      }
      if (incoming.get(2)) {
        struct.replicaId = iprot.readI32();
        struct.setReplicaIdIsSet(true);
      }
      if (incoming.get(3)) {
        struct.messageSignature = iprot.readBinary();
        struct.setMessageSignatureIsSet(true);
      }
    }
  }

}

