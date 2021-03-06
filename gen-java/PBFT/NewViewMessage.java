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
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2014-12-10")
public class NewViewMessage implements org.apache.thrift.TBase<NewViewMessage, NewViewMessage._Fields>, java.io.Serializable, Cloneable, Comparable<NewViewMessage> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("NewViewMessage");

  private static final org.apache.thrift.protocol.TField NEW_VIEW_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("newViewID", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField VIEW_CHANGE_MESSAGES_FIELD_DESC = new org.apache.thrift.protocol.TField("viewChangeMessages", org.apache.thrift.protocol.TType.SET, (short)2);
  private static final org.apache.thrift.protocol.TField PRE_PREPARE_MESSAGES_FIELD_DESC = new org.apache.thrift.protocol.TField("prePrepareMessages", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField REPLICA_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("replicaID", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField MESSAGE_SIGNATURE_FIELD_DESC = new org.apache.thrift.protocol.TField("messageSignature", org.apache.thrift.protocol.TType.STRING, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new NewViewMessageStandardSchemeFactory());
    schemes.put(TupleScheme.class, new NewViewMessageTupleSchemeFactory());
  }

  public int newViewID; // required
  public Set<ViewChangeMessage> viewChangeMessages; // required
  public List<PrePrepareMessage> prePrepareMessages; // required
  public int replicaID; // required
  public ByteBuffer messageSignature; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NEW_VIEW_ID((short)1, "newViewID"),
    VIEW_CHANGE_MESSAGES((short)2, "viewChangeMessages"),
    PRE_PREPARE_MESSAGES((short)3, "prePrepareMessages"),
    REPLICA_ID((short)5, "replicaID"),
    MESSAGE_SIGNATURE((short)6, "messageSignature");

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
        case 1: // NEW_VIEW_ID
          return NEW_VIEW_ID;
        case 2: // VIEW_CHANGE_MESSAGES
          return VIEW_CHANGE_MESSAGES;
        case 3: // PRE_PREPARE_MESSAGES
          return PRE_PREPARE_MESSAGES;
        case 5: // REPLICA_ID
          return REPLICA_ID;
        case 6: // MESSAGE_SIGNATURE
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
  private static final int __NEWVIEWID_ISSET_ID = 0;
  private static final int __REPLICAID_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NEW_VIEW_ID, new org.apache.thrift.meta_data.FieldMetaData("newViewID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.VIEW_CHANGE_MESSAGES, new org.apache.thrift.meta_data.FieldMetaData("viewChangeMessages", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ViewChangeMessage.class))));
    tmpMap.put(_Fields.PRE_PREPARE_MESSAGES, new org.apache.thrift.meta_data.FieldMetaData("prePrepareMessages", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, PrePrepareMessage.class))));
    tmpMap.put(_Fields.REPLICA_ID, new org.apache.thrift.meta_data.FieldMetaData("replicaID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MESSAGE_SIGNATURE, new org.apache.thrift.meta_data.FieldMetaData("messageSignature", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Signature")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(NewViewMessage.class, metaDataMap);
  }

  public NewViewMessage() {
  }

  public NewViewMessage(
    int newViewID,
    Set<ViewChangeMessage> viewChangeMessages,
    List<PrePrepareMessage> prePrepareMessages,
    int replicaID,
    ByteBuffer messageSignature)
  {
    this();
    this.newViewID = newViewID;
    setNewViewIDIsSet(true);
    this.viewChangeMessages = viewChangeMessages;
    this.prePrepareMessages = prePrepareMessages;
    this.replicaID = replicaID;
    setReplicaIDIsSet(true);
    this.messageSignature = org.apache.thrift.TBaseHelper.copyBinary(messageSignature);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NewViewMessage(NewViewMessage other) {
    __isset_bitfield = other.__isset_bitfield;
    this.newViewID = other.newViewID;
    if (other.isSetViewChangeMessages()) {
      Set<ViewChangeMessage> __this__viewChangeMessages = new HashSet<ViewChangeMessage>(other.viewChangeMessages.size());
      for (ViewChangeMessage other_element : other.viewChangeMessages) {
        __this__viewChangeMessages.add(new ViewChangeMessage(other_element));
      }
      this.viewChangeMessages = __this__viewChangeMessages;
    }
    if (other.isSetPrePrepareMessages()) {
      List<PrePrepareMessage> __this__prePrepareMessages = new ArrayList<PrePrepareMessage>(other.prePrepareMessages.size());
      for (PrePrepareMessage other_element : other.prePrepareMessages) {
        __this__prePrepareMessages.add(new PrePrepareMessage(other_element));
      }
      this.prePrepareMessages = __this__prePrepareMessages;
    }
    this.replicaID = other.replicaID;
    if (other.isSetMessageSignature()) {
      this.messageSignature = other.messageSignature;
    }
  }

  public NewViewMessage deepCopy() {
    return new NewViewMessage(this);
  }

  @Override
  public void clear() {
    setNewViewIDIsSet(false);
    this.newViewID = 0;
    this.viewChangeMessages = null;
    this.prePrepareMessages = null;
    setReplicaIDIsSet(false);
    this.replicaID = 0;
    this.messageSignature = null;
  }

  public int getNewViewID() {
    return this.newViewID;
  }

  public NewViewMessage setNewViewID(int newViewID) {
    this.newViewID = newViewID;
    setNewViewIDIsSet(true);
    return this;
  }

  public void unsetNewViewID() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NEWVIEWID_ISSET_ID);
  }

  /** Returns true if field newViewID is set (has been assigned a value) and false otherwise */
  public boolean isSetNewViewID() {
    return EncodingUtils.testBit(__isset_bitfield, __NEWVIEWID_ISSET_ID);
  }

  public void setNewViewIDIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NEWVIEWID_ISSET_ID, value);
  }

  public int getViewChangeMessagesSize() {
    return (this.viewChangeMessages == null) ? 0 : this.viewChangeMessages.size();
  }

  public java.util.Iterator<ViewChangeMessage> getViewChangeMessagesIterator() {
    return (this.viewChangeMessages == null) ? null : this.viewChangeMessages.iterator();
  }

  public void addToViewChangeMessages(ViewChangeMessage elem) {
    if (this.viewChangeMessages == null) {
      this.viewChangeMessages = new HashSet<ViewChangeMessage>();
    }
    this.viewChangeMessages.add(elem);
  }

  public Set<ViewChangeMessage> getViewChangeMessages() {
    return this.viewChangeMessages;
  }

  public NewViewMessage setViewChangeMessages(Set<ViewChangeMessage> viewChangeMessages) {
    this.viewChangeMessages = viewChangeMessages;
    return this;
  }

  public void unsetViewChangeMessages() {
    this.viewChangeMessages = null;
  }

  /** Returns true if field viewChangeMessages is set (has been assigned a value) and false otherwise */
  public boolean isSetViewChangeMessages() {
    return this.viewChangeMessages != null;
  }

  public void setViewChangeMessagesIsSet(boolean value) {
    if (!value) {
      this.viewChangeMessages = null;
    }
  }

  public int getPrePrepareMessagesSize() {
    return (this.prePrepareMessages == null) ? 0 : this.prePrepareMessages.size();
  }

  public java.util.Iterator<PrePrepareMessage> getPrePrepareMessagesIterator() {
    return (this.prePrepareMessages == null) ? null : this.prePrepareMessages.iterator();
  }

  public void addToPrePrepareMessages(PrePrepareMessage elem) {
    if (this.prePrepareMessages == null) {
      this.prePrepareMessages = new ArrayList<PrePrepareMessage>();
    }
    this.prePrepareMessages.add(elem);
  }

  public List<PrePrepareMessage> getPrePrepareMessages() {
    return this.prePrepareMessages;
  }

  public NewViewMessage setPrePrepareMessages(List<PrePrepareMessage> prePrepareMessages) {
    this.prePrepareMessages = prePrepareMessages;
    return this;
  }

  public void unsetPrePrepareMessages() {
    this.prePrepareMessages = null;
  }

  /** Returns true if field prePrepareMessages is set (has been assigned a value) and false otherwise */
  public boolean isSetPrePrepareMessages() {
    return this.prePrepareMessages != null;
  }

  public void setPrePrepareMessagesIsSet(boolean value) {
    if (!value) {
      this.prePrepareMessages = null;
    }
  }

  public int getReplicaID() {
    return this.replicaID;
  }

  public NewViewMessage setReplicaID(int replicaID) {
    this.replicaID = replicaID;
    setReplicaIDIsSet(true);
    return this;
  }

  public void unsetReplicaID() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __REPLICAID_ISSET_ID);
  }

  /** Returns true if field replicaID is set (has been assigned a value) and false otherwise */
  public boolean isSetReplicaID() {
    return EncodingUtils.testBit(__isset_bitfield, __REPLICAID_ISSET_ID);
  }

  public void setReplicaIDIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __REPLICAID_ISSET_ID, value);
  }

  public byte[] getMessageSignature() {
    setMessageSignature(org.apache.thrift.TBaseHelper.rightSize(messageSignature));
    return messageSignature == null ? null : messageSignature.array();
  }

  public ByteBuffer bufferForMessageSignature() {
    return org.apache.thrift.TBaseHelper.copyBinary(messageSignature);
  }

  public NewViewMessage setMessageSignature(byte[] messageSignature) {
    this.messageSignature = messageSignature == null ? (ByteBuffer)null : ByteBuffer.wrap(Arrays.copyOf(messageSignature, messageSignature.length));
    return this;
  }

  public NewViewMessage setMessageSignature(ByteBuffer messageSignature) {
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
    case NEW_VIEW_ID:
      if (value == null) {
        unsetNewViewID();
      } else {
        setNewViewID((Integer)value);
      }
      break;

    case VIEW_CHANGE_MESSAGES:
      if (value == null) {
        unsetViewChangeMessages();
      } else {
        setViewChangeMessages((Set<ViewChangeMessage>)value);
      }
      break;

    case PRE_PREPARE_MESSAGES:
      if (value == null) {
        unsetPrePrepareMessages();
      } else {
        setPrePrepareMessages((List<PrePrepareMessage>)value);
      }
      break;

    case REPLICA_ID:
      if (value == null) {
        unsetReplicaID();
      } else {
        setReplicaID((Integer)value);
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
    case NEW_VIEW_ID:
      return Integer.valueOf(getNewViewID());

    case VIEW_CHANGE_MESSAGES:
      return getViewChangeMessages();

    case PRE_PREPARE_MESSAGES:
      return getPrePrepareMessages();

    case REPLICA_ID:
      return Integer.valueOf(getReplicaID());

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
    case NEW_VIEW_ID:
      return isSetNewViewID();
    case VIEW_CHANGE_MESSAGES:
      return isSetViewChangeMessages();
    case PRE_PREPARE_MESSAGES:
      return isSetPrePrepareMessages();
    case REPLICA_ID:
      return isSetReplicaID();
    case MESSAGE_SIGNATURE:
      return isSetMessageSignature();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof NewViewMessage)
      return this.equals((NewViewMessage)that);
    return false;
  }

  public boolean equals(NewViewMessage that) {
    if (that == null)
      return false;

    boolean this_present_newViewID = true;
    boolean that_present_newViewID = true;
    if (this_present_newViewID || that_present_newViewID) {
      if (!(this_present_newViewID && that_present_newViewID))
        return false;
      if (this.newViewID != that.newViewID)
        return false;
    }

    boolean this_present_viewChangeMessages = true && this.isSetViewChangeMessages();
    boolean that_present_viewChangeMessages = true && that.isSetViewChangeMessages();
    if (this_present_viewChangeMessages || that_present_viewChangeMessages) {
      if (!(this_present_viewChangeMessages && that_present_viewChangeMessages))
        return false;
      if (!this.viewChangeMessages.equals(that.viewChangeMessages))
        return false;
    }

    boolean this_present_prePrepareMessages = true && this.isSetPrePrepareMessages();
    boolean that_present_prePrepareMessages = true && that.isSetPrePrepareMessages();
    if (this_present_prePrepareMessages || that_present_prePrepareMessages) {
      if (!(this_present_prePrepareMessages && that_present_prePrepareMessages))
        return false;
      if (!this.prePrepareMessages.equals(that.prePrepareMessages))
        return false;
    }

    boolean this_present_replicaID = true;
    boolean that_present_replicaID = true;
    if (this_present_replicaID || that_present_replicaID) {
      if (!(this_present_replicaID && that_present_replicaID))
        return false;
      if (this.replicaID != that.replicaID)
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

    boolean present_newViewID = true;
    list.add(present_newViewID);
    if (present_newViewID)
      list.add(newViewID);

    boolean present_viewChangeMessages = true && (isSetViewChangeMessages());
    list.add(present_viewChangeMessages);
    if (present_viewChangeMessages)
      list.add(viewChangeMessages);

    boolean present_prePrepareMessages = true && (isSetPrePrepareMessages());
    list.add(present_prePrepareMessages);
    if (present_prePrepareMessages)
      list.add(prePrepareMessages);

    boolean present_replicaID = true;
    list.add(present_replicaID);
    if (present_replicaID)
      list.add(replicaID);

    boolean present_messageSignature = true && (isSetMessageSignature());
    list.add(present_messageSignature);
    if (present_messageSignature)
      list.add(messageSignature);

    return list.hashCode();
  }

  @Override
  public int compareTo(NewViewMessage other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetNewViewID()).compareTo(other.isSetNewViewID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNewViewID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.newViewID, other.newViewID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetViewChangeMessages()).compareTo(other.isSetViewChangeMessages());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetViewChangeMessages()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.viewChangeMessages, other.viewChangeMessages);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPrePrepareMessages()).compareTo(other.isSetPrePrepareMessages());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPrePrepareMessages()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.prePrepareMessages, other.prePrepareMessages);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetReplicaID()).compareTo(other.isSetReplicaID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetReplicaID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.replicaID, other.replicaID);
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
    StringBuilder sb = new StringBuilder("NewViewMessage(");
    boolean first = true;

    sb.append("newViewID:");
    sb.append(this.newViewID);
    first = false;
    if (!first) sb.append(", ");
    sb.append("viewChangeMessages:");
    if (this.viewChangeMessages == null) {
      sb.append("null");
    } else {
      sb.append(this.viewChangeMessages);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("prePrepareMessages:");
    if (this.prePrepareMessages == null) {
      sb.append("null");
    } else {
      sb.append(this.prePrepareMessages);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("replicaID:");
    sb.append(this.replicaID);
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

  private static class NewViewMessageStandardSchemeFactory implements SchemeFactory {
    public NewViewMessageStandardScheme getScheme() {
      return new NewViewMessageStandardScheme();
    }
  }

  private static class NewViewMessageStandardScheme extends StandardScheme<NewViewMessage> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, NewViewMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NEW_VIEW_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.newViewID = iprot.readI32();
              struct.setNewViewIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // VIEW_CHANGE_MESSAGES
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set32 = iprot.readSetBegin();
                struct.viewChangeMessages = new HashSet<ViewChangeMessage>(2*_set32.size);
                ViewChangeMessage _elem33;
                for (int _i34 = 0; _i34 < _set32.size; ++_i34)
                {
                  _elem33 = new ViewChangeMessage();
                  _elem33.read(iprot);
                  struct.viewChangeMessages.add(_elem33);
                }
                iprot.readSetEnd();
              }
              struct.setViewChangeMessagesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PRE_PREPARE_MESSAGES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list35 = iprot.readListBegin();
                struct.prePrepareMessages = new ArrayList<PrePrepareMessage>(_list35.size);
                PrePrepareMessage _elem36;
                for (int _i37 = 0; _i37 < _list35.size; ++_i37)
                {
                  _elem36 = new PrePrepareMessage();
                  _elem36.read(iprot);
                  struct.prePrepareMessages.add(_elem36);
                }
                iprot.readListEnd();
              }
              struct.setPrePrepareMessagesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // REPLICA_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.replicaID = iprot.readI32();
              struct.setReplicaIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // MESSAGE_SIGNATURE
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, NewViewMessage struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(NEW_VIEW_ID_FIELD_DESC);
      oprot.writeI32(struct.newViewID);
      oprot.writeFieldEnd();
      if (struct.viewChangeMessages != null) {
        oprot.writeFieldBegin(VIEW_CHANGE_MESSAGES_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRUCT, struct.viewChangeMessages.size()));
          for (ViewChangeMessage _iter38 : struct.viewChangeMessages)
          {
            _iter38.write(oprot);
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.prePrepareMessages != null) {
        oprot.writeFieldBegin(PRE_PREPARE_MESSAGES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.prePrepareMessages.size()));
          for (PrePrepareMessage _iter39 : struct.prePrepareMessages)
          {
            _iter39.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(REPLICA_ID_FIELD_DESC);
      oprot.writeI32(struct.replicaID);
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

  private static class NewViewMessageTupleSchemeFactory implements SchemeFactory {
    public NewViewMessageTupleScheme getScheme() {
      return new NewViewMessageTupleScheme();
    }
  }

  private static class NewViewMessageTupleScheme extends TupleScheme<NewViewMessage> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, NewViewMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetNewViewID()) {
        optionals.set(0);
      }
      if (struct.isSetViewChangeMessages()) {
        optionals.set(1);
      }
      if (struct.isSetPrePrepareMessages()) {
        optionals.set(2);
      }
      if (struct.isSetReplicaID()) {
        optionals.set(3);
      }
      if (struct.isSetMessageSignature()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetNewViewID()) {
        oprot.writeI32(struct.newViewID);
      }
      if (struct.isSetViewChangeMessages()) {
        {
          oprot.writeI32(struct.viewChangeMessages.size());
          for (ViewChangeMessage _iter40 : struct.viewChangeMessages)
          {
            _iter40.write(oprot);
          }
        }
      }
      if (struct.isSetPrePrepareMessages()) {
        {
          oprot.writeI32(struct.prePrepareMessages.size());
          for (PrePrepareMessage _iter41 : struct.prePrepareMessages)
          {
            _iter41.write(oprot);
          }
        }
      }
      if (struct.isSetReplicaID()) {
        oprot.writeI32(struct.replicaID);
      }
      if (struct.isSetMessageSignature()) {
        oprot.writeBinary(struct.messageSignature);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, NewViewMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.newViewID = iprot.readI32();
        struct.setNewViewIDIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TSet _set42 = new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.viewChangeMessages = new HashSet<ViewChangeMessage>(2*_set42.size);
          ViewChangeMessage _elem43;
          for (int _i44 = 0; _i44 < _set42.size; ++_i44)
          {
            _elem43 = new ViewChangeMessage();
            _elem43.read(iprot);
            struct.viewChangeMessages.add(_elem43);
          }
        }
        struct.setViewChangeMessagesIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list45 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.prePrepareMessages = new ArrayList<PrePrepareMessage>(_list45.size);
          PrePrepareMessage _elem46;
          for (int _i47 = 0; _i47 < _list45.size; ++_i47)
          {
            _elem46 = new PrePrepareMessage();
            _elem46.read(iprot);
            struct.prePrepareMessages.add(_elem46);
          }
        }
        struct.setPrePrepareMessagesIsSet(true);
      }
      if (incoming.get(3)) {
        struct.replicaID = iprot.readI32();
        struct.setReplicaIDIsSet(true);
      }
      if (incoming.get(4)) {
        struct.messageSignature = iprot.readBinary();
        struct.setMessageSignatureIsSet(true);
      }
    }
  }

}

