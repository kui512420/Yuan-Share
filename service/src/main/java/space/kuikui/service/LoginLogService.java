package space.kuikui.service;

import com.github.pagehelper.PageInfo;
import space.kuikui.pojo.LoginLog;

public interface LoginLogService {
    PageInfo<LoginLog> getLoginLog(String type,String parm,int pageNo, int pageSize);
    int addLoginLog(LoginLog loginLog);
}
