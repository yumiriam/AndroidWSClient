package com.milissa.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;

import android.util.Base64;

public class FileBean implements KvmSerializable {
	
	private byte[] data;
	private String description;
	private String name;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public Object getProperty(int arg0) {
		switch (arg0) {
		case 0:
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			byte[] buffer = new byte[256];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			try {
				int readBytes;
				while ((readBytes = is.read(buffer)) != -1) {
					os.write(buffer, 0, readBytes);
				}
				is.close();
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return os.toByteArray();
		case 1:
			return description;
		case 2:
			return name;
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
            arg2.name = "description";
            break;
        case 2:
        	arg2.type = PropertyInfo.STRING_CLASS;
        	arg2.name = "name";
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
			this.description = arg1.toString();
			break;
		case 2:
			this.name = arg1.toString();
			break;
		}	
	}
}