package org.seckill.service;

//站在使用者的角度去设计接口
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

public interface SeckillService {
	
	List<Seckill> getSeckillList();
	
	Seckill getbyId(long seckillId);
	
	Exposer exportSeckillUrl(long seckillId);
	
	//执行秒杀动作
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
	throws SeckillException,RepeatKillException,SeckillCloseException;
	
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5)
			throws SeckillException,RepeatKillException,SeckillCloseException;
}
