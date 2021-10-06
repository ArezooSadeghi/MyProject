package com.example.sipmobileapp.model;

public class UserResult {

    private String errorCode;
    private String error;
    private UserInfo[] users;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public UserInfo[] getUsers() {
        return users;
    }

    public void setUsers(UserInfo[] users) {
        this.users = users;
    }

    public class UserInfo {

        private int UserID;
        private int UserGroupID;
        private boolean Disable;
        private String UserLoginKey;
        private String User_Name;
        private String GroupName;
        private String UserFullName;

        public int getUserID() {
            return UserID;
        }

        public void setUserID(int userID) {
            UserID = userID;
        }

        public int getUserGroupID() {
            return UserGroupID;
        }

        public void setUserGroupID(int userGroupID) {
            UserGroupID = userGroupID;
        }

        public boolean isDisable() {
            return Disable;
        }

        public void setDisable(boolean disable) {
            Disable = disable;
        }

        public String getUserLoginKey() {
            return UserLoginKey;
        }

        public void setUserLoginKey(String userLoginKey) {
            UserLoginKey = userLoginKey;
        }

        public String getUser_Name() {
            return User_Name;
        }

        public void setUser_Name(String user_Name) {
            User_Name = user_Name;
        }

        public String getGroupName() {
            return GroupName;
        }

        public void setGroupName(String groupName) {
            GroupName = groupName;
        }

        public String getUserFullName() {
            return UserFullName;
        }

        public void setUserFullName(String userFullName) {
            UserFullName = userFullName;
        }
    }

    public class UserParameter {

        private String userName;
        private String password;

        public UserParameter(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
