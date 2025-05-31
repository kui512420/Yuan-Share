package space.kuikui.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import space.kuikui.mapper.InfoMapper;
import space.kuikui.pojo.Article;
import space.kuikui.pojo.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.service.InfoService;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InfoServiceImpl implements InfoService {

    @Autowired
    private InfoMapper infoMapper;

    @Override
    public PageInfo<Map<String, Object>> getInfoAll(int pagenow, int pagesize) {
        PageHelper.startPage(pagenow, pagesize);
        // 修正：将返回值赋给正确的类型 List<Map<String, Object>>
        List<Map<String, Object>> infoes = infoMapper.selectInfoAll();
        // 修正：PageInfo 的泛型类型应与列表元素类型一致
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(infoes);
        return pageInfo;
    }
    @Override
    public List<Map<String,Object>> getInfo() {
        List<Map<String,Object>> infoes = infoMapper.selectInfo();
        return infoes;
    }
    public boolean delInfo() {
        return infoMapper.delInfo();
    }
    @Override
    public boolean  setInfo(Info info) {
        int count = infoMapper.insert(info);
        if(count>=1){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public int count() {
        return infoMapper.count();
    }
}
