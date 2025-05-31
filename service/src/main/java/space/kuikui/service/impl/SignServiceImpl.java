package space.kuikui.service.impl;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.mapper.SignMapper;
import space.kuikui.pojo.Sign;

import space.kuikui.service.SignService;

import java.util.List;

@Service
public class SignServiceImpl implements SignService {

    @Autowired
    private SignMapper signMapper;
    @Override
    public boolean cheak(Integer id) {
        if(signMapper.cheak(id)==null){
            return false;
        }else{
            return true;
        }

    }
    @Override
    public Integer insertInfo(Sign sign) {
        return signMapper.insert(sign);
    }

    @Override
    public Sign findSignByUserid(String userid) {
        return signMapper.findByUserid(userid).get(0);
    }

    @Override
    public List<Sign> getSigns() {
        List<Sign> signs = signMapper.all();
        return signs;
    }
    @Override
    public List<Sign> findSignsByUserid( String userid){
        List<Sign> signs = signMapper.findByUserid(userid);
        return signs;
    }
}
