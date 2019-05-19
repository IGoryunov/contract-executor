/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.credits.client.node.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-05-19")
public class TransactionId implements org.apache.thrift.TBase<TransactionId, TransactionId._Fields>, java.io.Serializable, Cloneable, Comparable<TransactionId> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TransactionId");

  private static final org.apache.thrift.protocol.TField POOL_HASH_FIELD_DESC = new org.apache.thrift.protocol.TField("poolHash", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("index", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TransactionIdStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TransactionIdTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer poolHash; // required
  public int index; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    POOL_HASH((short)1, "poolHash"),
    INDEX((short)2, "index");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // POOL_HASH
          return POOL_HASH;
        case 2: // INDEX
          return INDEX;
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
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __INDEX_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.POOL_HASH, new org.apache.thrift.meta_data.FieldMetaData("poolHash", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "PoolHash")));
    tmpMap.put(_Fields.INDEX, new org.apache.thrift.meta_data.FieldMetaData("index", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TransactionId.class, metaDataMap);
  }

  public TransactionId() {
  }

  public TransactionId(
    java.nio.ByteBuffer poolHash,
    int index)
  {
    this();
    this.poolHash = org.apache.thrift.TBaseHelper.copyBinary(poolHash);
    this.index = index;
    setIndexIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TransactionId(TransactionId other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetPoolHash()) {
      this.poolHash = org.apache.thrift.TBaseHelper.copyBinary(other.poolHash);
    }
    this.index = other.index;
  }

  public TransactionId deepCopy() {
    return new TransactionId(this);
  }

  @Override
  public void clear() {
    this.poolHash = null;
    setIndexIsSet(false);
    this.index = 0;
  }

  public byte[] getPoolHash() {
    setPoolHash(org.apache.thrift.TBaseHelper.rightSize(poolHash));
    return poolHash == null ? null : poolHash.array();
  }

  public java.nio.ByteBuffer bufferForPoolHash() {
    return org.apache.thrift.TBaseHelper.copyBinary(poolHash);
  }

  public TransactionId setPoolHash(byte[] poolHash) {
    this.poolHash = poolHash == null ? (java.nio.ByteBuffer)null   : java.nio.ByteBuffer.wrap(poolHash.clone());
    return this;
  }

  public TransactionId setPoolHash(@org.apache.thrift.annotation.Nullable java.nio.ByteBuffer poolHash) {
    this.poolHash = org.apache.thrift.TBaseHelper.copyBinary(poolHash);
    return this;
  }

  public void unsetPoolHash() {
    this.poolHash = null;
  }

  /** Returns true if field poolHash is set (has been assigned a value) and false otherwise */
  public boolean isSetPoolHash() {
    return this.poolHash != null;
  }

  public void setPoolHashIsSet(boolean value) {
    if (!value) {
      this.poolHash = null;
    }
  }

  public int getIndex() {
    return this.index;
  }

  public TransactionId setIndex(int index) {
    this.index = index;
    setIndexIsSet(true);
    return this;
  }

  public void unsetIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __INDEX_ISSET_ID);
  }

  /** Returns true if field index is set (has been assigned a value) and false otherwise */
  public boolean isSetIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __INDEX_ISSET_ID);
  }

  public void setIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __INDEX_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case POOL_HASH:
      if (value == null) {
        unsetPoolHash();
      } else {
        if (value instanceof byte[]) {
          setPoolHash((byte[])value);
        } else {
          setPoolHash((java.nio.ByteBuffer)value);
        }
      }
      break;

    case INDEX:
      if (value == null) {
        unsetIndex();
      } else {
        setIndex((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case POOL_HASH:
      return getPoolHash();

    case INDEX:
      return getIndex();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case POOL_HASH:
      return isSetPoolHash();
    case INDEX:
      return isSetIndex();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof TransactionId)
      return this.equals((TransactionId)that);
    return false;
  }

  public boolean equals(TransactionId that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_poolHash = true && this.isSetPoolHash();
    boolean that_present_poolHash = true && that.isSetPoolHash();
    if (this_present_poolHash || that_present_poolHash) {
      if (!(this_present_poolHash && that_present_poolHash))
        return false;
      if (!this.poolHash.equals(that.poolHash))
        return false;
    }

    boolean this_present_index = true;
    boolean that_present_index = true;
    if (this_present_index || that_present_index) {
      if (!(this_present_index && that_present_index))
        return false;
      if (this.index != that.index)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetPoolHash()) ? 131071 : 524287);
    if (isSetPoolHash())
      hashCode = hashCode * 8191 + poolHash.hashCode();

    hashCode = hashCode * 8191 + index;

    return hashCode;
  }

  @Override
  public int compareTo(TransactionId other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetPoolHash()).compareTo(other.isSetPoolHash());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPoolHash()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.poolHash, other.poolHash);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetIndex()).compareTo(other.isSetIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.index, other.index);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TransactionId(");
    boolean first = true;

    sb.append("poolHash:");
    if (this.poolHash == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.poolHash, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("index:");
    sb.append(this.index);
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

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TransactionIdStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TransactionIdStandardScheme getScheme() {
      return new TransactionIdStandardScheme();
    }
  }

  private static class TransactionIdStandardScheme extends org.apache.thrift.scheme.StandardScheme<TransactionId> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TransactionId struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // POOL_HASH
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.poolHash = iprot.readBinary();
              struct.setPoolHashIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.index = iprot.readI32();
              struct.setIndexIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TransactionId struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.poolHash != null) {
        oprot.writeFieldBegin(POOL_HASH_FIELD_DESC);
        oprot.writeBinary(struct.poolHash);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(INDEX_FIELD_DESC);
      oprot.writeI32(struct.index);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TransactionIdTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TransactionIdTupleScheme getScheme() {
      return new TransactionIdTupleScheme();
    }
  }

  private static class TransactionIdTupleScheme extends org.apache.thrift.scheme.TupleScheme<TransactionId> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TransactionId struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetPoolHash()) {
        optionals.set(0);
      }
      if (struct.isSetIndex()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetPoolHash()) {
        oprot.writeBinary(struct.poolHash);
      }
      if (struct.isSetIndex()) {
        oprot.writeI32(struct.index);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TransactionId struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.poolHash = iprot.readBinary();
        struct.setPoolHashIsSet(true);
      }
      if (incoming.get(1)) {
        struct.index = iprot.readI32();
        struct.setIndexIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

