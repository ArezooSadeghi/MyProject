package com.example.sipmobileapp.model;

public class AttachResult extends ResultInfo {
    private AttachInfo[] attachs;

    public AttachInfo[] getAttachs() {
        return attachs;
    }

    public void setAttachs(AttachInfo[] attachs) {
        this.attachs = attachs;
    }

    public class AttachInfo {
        private int attachID;
        private int imageTypeID;
        private int attachTypeID;
        private int registerID;
        private int sickID;
        private int timeSheetID;
        private int sickPaymentID;
        private int personID;
        private int userID;
        private int deleteUserID;
        private boolean upload;
        private boolean favorite;
        private String description;
        private String userFullName;
        private String regTime;
        private String deleteUserFullName;
        private String deleteRegTime;
        private String uploadTime;
        private String Telegram_FileID;
        private String SOPinstanceUID;
        private String image;
        private String fileBase64;

        public int getAttachID() {
            return attachID;
        }

        public void setAttachID(int attachID) {
            this.attachID = attachID;
        }

        public int getImageTypeID() {
            return imageTypeID;
        }

        public void setImageTypeID(int imageTypeID) {
            this.imageTypeID = imageTypeID;
        }

        public int getAttachTypeID() {
            return attachTypeID;
        }

        public void setAttachTypeID(int attachTypeID) {
            this.attachTypeID = attachTypeID;
        }

        public int getRegisterID() {
            return registerID;
        }

        public void setRegisterID(int registerID) {
            this.registerID = registerID;
        }

        public int getSickID() {
            return sickID;
        }

        public void setSickID(int sickID) {
            this.sickID = sickID;
        }

        public int getTimeSheetID() {
            return timeSheetID;
        }

        public void setTimeSheetID(int timeSheetID) {
            this.timeSheetID = timeSheetID;
        }

        public int getSickPaymentID() {
            return sickPaymentID;
        }

        public void setSickPaymentID(int sickPaymentID) {
            this.sickPaymentID = sickPaymentID;
        }

        public int getPersonID() {
            return personID;
        }

        public void setPersonID(int personID) {
            this.personID = personID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getDeleteUserID() {
            return deleteUserID;
        }

        public void setDeleteUserID(int deleteUserID) {
            this.deleteUserID = deleteUserID;
        }

        public boolean isUpload() {
            return upload;
        }

        public void setUpload(boolean upload) {
            this.upload = upload;
        }

        public boolean isFavorite() {
            return favorite;
        }

        public void setFavorite(boolean favorite) {
            this.favorite = favorite;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        public String getRegTime() {
            return regTime;
        }

        public void setRegTime(String regTime) {
            this.regTime = regTime;
        }

        public String getDeleteUserFullName() {
            return deleteUserFullName;
        }

        public void setDeleteUserFullName(String deleteUserFullName) {
            this.deleteUserFullName = deleteUserFullName;
        }

        public String getDeleteRegTime() {
            return deleteRegTime;
        }

        public void setDeleteRegTime(String deleteRegTime) {
            this.deleteRegTime = deleteRegTime;
        }

        public String getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(String uploadTime) {
            this.uploadTime = uploadTime;
        }

        public String getTelegram_FileID() {
            return Telegram_FileID;
        }

        public void setTelegram_FileID(String telegram_FileID) {
            Telegram_FileID = telegram_FileID;
        }

        public String getSOPinstanceUID() {
            return SOPinstanceUID;
        }

        public void setSOPinstanceUID(String SOPinstanceUID) {
            this.SOPinstanceUID = SOPinstanceUID;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getFileBase64() {
            return fileBase64;
        }

        public void setFileBase64(String fileBase64) {
            this.fileBase64 = fileBase64;
        }
    }

    public class AttachParameter {
        private int imageTypeID;
        private int AttachTypeID;
        private int sickID;
        private String Description;
        private String Image;

        public int getImageTypeID() {
            return imageTypeID;
        }

        public void setImageTypeID(int imageTypeID) {
            this.imageTypeID = imageTypeID;
        }

        public int getAttachTypeID() {
            return AttachTypeID;
        }

        public void setAttachTypeID(int attachTypeID) {
            AttachTypeID = attachTypeID;
        }

        public int getSickID() {
            return sickID;
        }

        public void setSickID(int sickID) {
            this.sickID = sickID;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getImage() {
            return Image;
        }

        public void setImage(String image) {
            Image = image;
        }
    }
}
