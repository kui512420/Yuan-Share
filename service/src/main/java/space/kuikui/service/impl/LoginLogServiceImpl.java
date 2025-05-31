package space.kuikui.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.mapper.LoginLogMapper;
import space.kuikui.pojo.LoginLog;
import space.kuikui.pojo.User;
import space.kuikui.service.LoginLogService;

import java.util.List;

@Service
@Slf4j
public class LoginLogServiceImpl implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public PageInfo<LoginLog> getLoginLog(String type,String parm,int pageNo, int pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<LoginLog> LoginLogs = null;
        if(type.equals("1")){
            LoginLogs = loginLogMapper.selectAllByUsername(parm);
        }else if(type.equals("2")){
            LoginLogs = loginLogMapper.selectAllByIp(parm);
        }else{

            LoginLogs = loginLogMapper.selectAll();
        }
        PageInfo<LoginLog> LoginLogsPageInfo = new PageInfo<>(LoginLogs);
        return LoginLogsPageInfo;
    }
    @Override
    public int addLoginLog(LoginLog loginLog){
        return loginLogMapper.insert(loginLog);
    }
}
