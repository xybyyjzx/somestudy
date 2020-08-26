package com.bjpowernode.crm.settings.domain;

/**
 * 2019/9/29
 */
public class User {

    /*

        关于时间：
        日期（年月日）：yyyy-MM-dd  10位
        日期加时间（年月日时分秒）：yyyy-MM-dd HH:mm:ss 19位

        关于登录：

        1.验证账号和密码
            int count = select count(*) from tbl_user where loginAct=? and loginPwd=?
            count:0 说明没查到 账号密码错误
            count:1 查到了一条 账号密码正确
            count:>1 查询得到多条，说明有垃圾数据

            我们使用该形式，如果查询得到user对象，继续从user对象中取得其他信息继续验证
            User user = select * from tbl_user where loginAct=? and loginPwd=?
            user==null:说明没查到 账号密码错误
            user!=null:查到了一条 账号密码正确

        2.验证失效时间 user.get失效时间

        3.验证锁定状态 user.get锁定状态

        4.验证ip地址 user.get允许访问的ip地址群
            验证浏览器端的ip地址，有没有在ip地址群中出现，如果有，则访问成功

     */

    private String id;  //编号
    private String loginAct;    //登录账号
    private String name;    //用户真实姓名
    private String loginPwd;    //登录密码
    private String email;   //邮箱
    private String expireTime;  //失效时间  yyyy-MM-dd HH:mm:ss 19位
    private String lockState;   //锁定状态
    private String deptno;  //部门编号
    private String allowIps;    //允许访问的ip地址群
    private String createTime;  //创建时间  yyyy-MM-dd HH:mm:ss 19位
    private String createBy;    //创建人
    private String editTime;    //修改时间  yyyy-MM-dd HH:mm:ss 19位
    private String editBy;  //修改人

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getLoginAct() {
        return loginAct;
    }

    public User setLoginAct(String loginAct) {
        this.loginAct = loginAct;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public User setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public User setExpireTime(String expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public String getLockState() {
        return lockState;
    }

    public User setLockState(String lockState) {
        this.lockState = lockState;
        return this;
    }

    public String getDeptno() {
        return deptno;
    }

    public User setDeptno(String deptno) {
        this.deptno = deptno;
        return this;
    }

    public String getAllowIps() {
        return allowIps;
    }

    public User setAllowIps(String allowIps) {
        this.allowIps = allowIps;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public User setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public User setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public String getEditTime() {
        return editTime;
    }

    public User setEditTime(String editTime) {
        this.editTime = editTime;
        return this;
    }

    public String getEditBy() {
        return editBy;
    }

    public User setEditBy(String editBy) {
        this.editBy = editBy;
        return this;
    }
}
