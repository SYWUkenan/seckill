package com.xxxx.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxxx.seckill.pojo.Goods;
import com.xxxx.seckill.vo.GoodsVo;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yswu
 * @since 2021-02-12
 */
public interface GoodsMapper extends BaseMapper<Goods> {



    List<GoodsVo> findGoodsVo();



    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
