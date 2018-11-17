package org.seckill.service.impl;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.seckill.dao.RedisDao;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.mysql.fabric.xmlrpc.base.Data;

@Service
public class SeckillServiceImpl implements SeckillService{
	
	//盐值
	private final String slat="da1;3.'r;3,rjPFNSLKNGSGDLRU230R923OAJDA";
	
	//注入service依赖
	@Autowired
	private SeckillDao SeckillDao;
	@Autowired
	private SuccessKilledDao SuccessKilledDao;
	@Autowired
	private RedisDao redisDao;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public List<Seckill> getSeckillList() {
		return SeckillDao.queryAll(0, 100);
	}

	@Override
	public Seckill getbyId(long seckillId) {
		return SeckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		
//		Seckill seckill =redisDao.getSeckill(seckillId);
//		if (seckill==null) {
//			//说明缓存中没有
//			seckill = SeckillDao.queryById(seckillId);
//			if (seckill==null) {
//				return new Exposer(false, seckillId);
//			}
//			else
//			{
//				redisDao.putSeckill(seckill);
//			}
//		}
		Seckill seckill = SeckillDao.queryById(seckillId);
		if (seckill==null) {
			return new Exposer(false, seckillId);
			
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		//转化特定字符串的过程，不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true,md5,seckillId);
	}
	/*
	 *使用注解控制事务方法的优点：
	 *1.开发团队达成一致约定，明确标注事务方法的编程风格。
	 *2.保证事务方法的执行时间尽可能的短，不要穿插其他网络操作RPC/HTTP请求或者放到事务方法外
	 *3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制.
	 * */
	
	@Override
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		try {
			if (md5==null || !md5.equals(getMD5(seckillId))) {
				throw new SeckillException("seckill data rewrite");
			}
			//执行秒杀逻辑：减少库存并且记录购买行为
			Date nowTime = new Date();
			
			int insertCount = SuccessKilledDao.insertSuccessKilled(seckillId, userPhone);
			if (insertCount<=0) {
				//没有更新到记录，秒杀结束，rollback
				throw new RepeatKillException("seckill repeated");
			}
			else
			{
				int updateCount =SeckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount<=0) {
					throw new SeckillCloseException("seckill is closed");
				}
				else {
					//秒杀成功 commit
					SuccessKilled successKilled = SuccessKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
				}
			}
//			int updateCount =SeckillDao.reduceNumber(seckillId, nowTime);
//			if (updateCount<=0) {
//				throw new SeckillCloseException("seckill is closed");
//			}
//			else {
//				int insertCount = SuccessKilledDao.insertSuccessKilled(seckillId, userPhone);
//				if (insertCount<=0) {
//					throw new RepeatKillException("seckill repeated");
//				}
//				else
//				{
//					SuccessKilled successKilled = SuccessKilledDao.queryByIdWithSeckill(seckillId, userPhone);
//					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
//				}
//			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			//所有编译期异常，转化为运行期异常
			throw new SeckillException("seckill inner error"+e.getMessage());
		}
	}
	
	private String getMD5(long seckillId)
	{
		String base = seckillId +"/" +slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if (md5 ==null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("seckillId", seckillId);
		paramMap.put("phone", userPhone);
		paramMap.put("killTime", killTime);
		paramMap.put("result", null);
		//执行存储过程，result被赋值
		try {
			SeckillDao.killByProcedrue(paramMap);
			int result = MapUtils.getInteger(paramMap,"result",-2);
			if (result==1) {
				SuccessKilled sk = SuccessKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,sk); 
			}else
			{
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}

}
