/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/clement/git_dvl_clem_recordtime_free/ProofRecorderMerge/src/org/proofs/recorder/codec/ogg/utils/IServiceRecorderOgg.aidl
 */
package org.proofs.recorder.codec.ogg.utils;
// Declare Interface

public interface IServiceRecorderOgg extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg
{
private static final java.lang.String DESCRIPTOR = "org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg interface,
 * generating a proxy if needed.
 */
public static org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg))) {
return ((org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg)iin);
}
return new org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_parametersRec:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
float _arg2;
_arg2 = data.readFloat();
int _arg3;
_arg3 = data.readInt();
java.lang.String _arg4;
_arg4 = data.readString();
java.lang.String _arg5;
_arg5 = data.readString();
int _result = this.parametersRec(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startRec:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.startRec();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopRec:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.stopRec();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.proofs.recorder.codec.ogg.utils.IServiceRecorderOgg
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
// You can pass values in, out, or inout. 
// Primitive datatypes (such as int, boolean, etc.) can only be passed in.
//int add(in int value1, in int value2);

@Override public int parametersRec(java.lang.String Filename, int mSampleRate, float mQuality, int audioSource, java.lang.String notificationIntent, java.lang.String notificationPkg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(Filename);
_data.writeInt(mSampleRate);
_data.writeFloat(mQuality);
_data.writeInt(audioSource);
_data.writeString(notificationIntent);
_data.writeString(notificationPkg);
mRemote.transact(Stub.TRANSACTION_parametersRec, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int startRec() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startRec, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int stopRec() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopRec, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_parametersRec = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startRec = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopRec = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
// You can pass values in, out, or inout. 
// Primitive datatypes (such as int, boolean, etc.) can only be passed in.
//int add(in int value1, in int value2);

public int parametersRec(java.lang.String Filename, int mSampleRate, float mQuality, int audioSource, java.lang.String notificationIntent, java.lang.String notificationPkg) throws android.os.RemoteException;
public int startRec() throws android.os.RemoteException;
public int stopRec() throws android.os.RemoteException;
}
