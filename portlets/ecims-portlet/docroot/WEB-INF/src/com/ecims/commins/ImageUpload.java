package com.ecims.commins;

public class ImageUpload
{
	public ImageUpload()
	{
		
	}
	
	String newFileName;
	public String getNewFileName() {
		return newFileName;
	}
	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}
	public boolean isUploadValid() {
		return uploadValid;
	}
	public void setUploadValid(boolean uploadValid) {
		this.uploadValid = uploadValid;
	}
	boolean uploadValid;
}
