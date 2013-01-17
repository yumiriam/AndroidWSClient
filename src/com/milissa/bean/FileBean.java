package com.milissa.bean;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;

import android.util.Base64;

public class FileBean implements KvmSerializable {
	
	private byte[] data;
	private String name;
	private String type;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public Object getProperty(int arg0) {
		switch (arg0) {
		case 0:
			return data;
		case 1:
			return name;
		case 2:
			return type;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 3;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch (arg0) {
        case 0:
        	arg2.type = MarshalBase64.BYTE_ARRAY_CLASS;
        	arg2.name = "data";
            break;
        case 1:
        	arg2.type = PropertyInfo.STRING_CLASS;
        	arg2.name = "name";
            break;
        case 2:
            arg2.type = PropertyInfo.STRING_CLASS;
            arg2.name = "type";
            break;
        default:
        	break;
        }
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch (arg0) {
		case 0:
			this.data = Base64.decode(arg1.toString(), Base64.DEFAULT);
			break;
		case 1:
			this.type = arg1.toString();
			break;
		case 2:
			this.name = arg1.toString();
			break;
		}
	}
}