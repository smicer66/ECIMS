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
	public Boolean isUploadValid() {
		return uploadValid;
	}
	public void setUploadValid(Boolean uploadValid) {
		this.uploadValid = uploadValid;
	}
	Boolean uploadValid =null;
}
