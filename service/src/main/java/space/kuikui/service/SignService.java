package space.kuikui.service;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import space.kuikui.pojo.Sign;

import java.util.List;


public interface SignService {
    public boolean cheak(Integer id);

    public Integer insertInfo(Sign sign);

    public Sign findSignByUserid(String userid);

    public List<Sign> getSigns();

    public List<Sign> findSignsByUserid(String userid);
}
